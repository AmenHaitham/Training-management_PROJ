package com.tms.Model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.User;
import com.tms.Model.User.Gender;
import com.tms.Model.User.Role;
import com.tms.Services.EmailSender;
import com.tms.Services.PasswordUtil;

public class UserDAO {
    private static final int VERIFICATION_CODE_EXPIRY_MINUTES = 10;
    private static final int VERIFICATION_CODE_LENGTH = 6;
    
    private final Connection con;
    private final EmailSender emailSender;
    private final PasswordUtil passwordUtil;
    public String generatedCode;
    public LocalDateTime codeGenerationTime;

    public UserDAO() throws SQLException, ClassNotFoundException {
        this.con = DatabaseConnection.getDatabaseConnection();
        this.emailSender = new EmailSender();
        this.passwordUtil = new PasswordUtil();
    }

    // =================== Sign-Up ===================
    public boolean signUp(User user) {
    if (user == null) {
        throw new IllegalArgumentException("User cannot be null");
    }
    if (isEmailExists(user.getEmail())) {
        throw new IllegalStateException("Email already exists");
    }
    if (isPhoneExists(user.getPhoneNumber())) {
        throw new IllegalStateException("Phone number already exists");
    }
    
    // Set status to false if role is TRAINER or ADMIN
    boolean shouldActivate = user.getRole() != Role.TRAINER && user.getRole() != Role.ADMIN;
    user.setStatus(shouldActivate);
    
    String query = """
        INSERT INTO Users (first_name, last_name, phone_number, email, password, 
                          status, gender, address, role, photo, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    try (PreparedStatement stmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
        LocalDateTime now = LocalDateTime.now();
        stmt.setString(1, user.getFirstName());
        stmt.setString(2, user.getLastName());
        stmt.setString(3, user.getPhoneNumber());
        stmt.setString(4, user.getEmail());
        stmt.setString(5, passwordUtil.hashPassword(user.getPassword()));
        stmt.setBoolean(6, user.isActive());
        stmt.setString(7, user.getGender().name());
        stmt.setString(8, user.getAddress());
        stmt.setString(9, user.getRole().name());
        
        if (user.getPhoto() != null) {
            stmt.setBytes(10, user.getPhoto());
        } else {
            stmt.setNull(10, Types.BINARY);
        }
        
        stmt.setObject(11, now);
        
        int rowsInserted = stmt.executeUpdate();
        if (rowsInserted > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            sendWelcomeEmail(user);
            return true;
        }
    } catch (SQLException e) {
        System.err.println("Error during sign-up: " + e.getMessage());
    }
    return false;
}

    // =================== Sign-In ===================
    public Optional<User> signIn(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        String query = "SELECT * FROM Users WHERE email = ? AND status = true";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email.toLowerCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (passwordUtil.verifyPassword(password, storedHash) && rs.getBoolean("status") == true ) {
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during sign-in: " + e.getMessage());
        }
        return Optional.empty();
    }

    // =================== Password Reset ===================
    public boolean sendVerificationCode(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!isEmailExists(email)) {
            return false;
        }
        
        generatedCode = String.format("%0" + VERIFICATION_CODE_LENGTH + "d", 
            new Random().nextInt((int) Math.pow(10, VERIFICATION_CODE_LENGTH)));
        codeGenerationTime = LocalDateTime.now();
        
        String subject = "Password Reset Verification Code";
        String body = """
            Dear User,

            Your password reset verification code is: %s

            Please use this code to reset your password.
            This code will expire in %d minutes.

            Regards,
            TMS Team
        """.formatted(generatedCode, VERIFICATION_CODE_EXPIRY_MINUTES);
        
        return emailSender.sendEmail(email, subject, body);
    }

    public boolean verifyCode(String enteredCode) {
        if (generatedCode == null || codeGenerationTime == null) {
            return false;
        }
        
        long minutesElapsed = TimeUnit.MINUTES.convert(
            java.time.Duration.between(codeGenerationTime, LocalDateTime.now()).toNanos(),
            TimeUnit.NANOSECONDS
        );
        
        if (minutesElapsed > VERIFICATION_CODE_EXPIRY_MINUTES) {
            generatedCode = null;
            codeGenerationTime = null;
            return false;
        }
        
        return generatedCode.equals(enteredCode);
    }

    public boolean resetPassword(String email, String newPassword, String enteredCode) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        if (!verifyCode(enteredCode)) {
            return false;
        }

        String query = "UPDATE Users SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, passwordUtil.hashPassword(newPassword));
            stmt.setString(2, email.toLowerCase().trim());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                sendPasswordResetNotification(email);
                generatedCode = null;
                codeGenerationTime = null;
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            
        }
        return false;
    }

    // =================== User Management ===================
    public Optional<User> getUserById(int userId) {
        String query = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> getUserByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email.toLowerCase().trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        String query = """
            UPDATE Users SET 
                first_name = ?, 
                last_name = ?, 
                phone_number = ?, 
                email = ?, 
                status = ?, 
                gender = ?, 
                address = ?, 
                role = ?, 
                photo = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setString(4, user.getEmail());
            stmt.setBoolean(5, user.isActive());
            stmt.setString(6, user.getGender().name());
            stmt.setString(7, user.getAddress());
            stmt.setString(8, user.getRole().name());
            stmt.setBytes(9, user.getPhoto());
            stmt.setInt(10, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        String query = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        String query = "DELETE FROM Users WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email.toLowerCase().trim());
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user by email: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateUser(int userId) {
    if (userId <= 0) {
        throw new IllegalArgumentException("Invalid user ID");
    }
    
    // First get user details for email notification
    Optional<User> userOptional = getUserById(userId);
    if (userOptional.isEmpty()) {
        System.err.println("User not found with ID: " + userId);
        return false;
    }
    User user = userOptional.get();

    String query = "UPDATE Users SET status = false WHERE id = ?";
    try (PreparedStatement stmt = con.prepareStatement(query)) {
        stmt.setInt(1, userId);
        
        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated > 0) {
            // Send deactivation email
            sendAccountStatusEmail(user, false);
            return true;
        }
        return false;
    } catch (SQLException e) {
        System.err.println("Error deactivating user: " + e.getMessage());
        return false;
    }
}

public boolean activateUser(int userId) {
    if (userId <= 0) {
        throw new IllegalArgumentException("Invalid user ID");
    }
    
    // First get user details for email notification
    Optional<User> userOptional = getUserById(userId);
    if (userOptional.isEmpty()) {
        System.err.println("User not found with ID: " + userId);
        return false;
    }
    User user = userOptional.get();

    String query = "UPDATE Users SET status = true WHERE id = ?";
    try (PreparedStatement stmt = con.prepareStatement(query)) {
        stmt.setInt(1, userId);
        
        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated > 0) {
            // Send activation email
            sendAccountStatusEmail(user, true);
            return true;
        }
        return false;
    } catch (SQLException e) {
        System.err.println("Error activating user: " + e.getMessage());
        return false;
    }
}

private void sendAccountStatusEmail(User user, boolean activated) {
    try {
        String subject = activated ? "Account Activated" : "Account Deactivated";
        String message = "Dear " + user.getFirstName() + ",\n\n" +
            "Your account has been " + (activated ? "activated" : "deactivated") + ".\n" +
            (activated ? 
                "You can now access all features of our system." : 
                "You will no longer be able to access the system until your account is reactivated.") +
            "\n\nThank you,\nThe Support Team";
        
        // Use your existing email sending mechanism
        emailSender.sendEmail(user.getEmail(), subject, message);
        
        System.out.println("Notification email sent to " + user.getEmail());
    } catch (Exception e) {
        System.err.println("Failed to send status email to " + user.getEmail() + ": " + e.getMessage());
    }
}

    // =================== Helper Methods ===================
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getBoolean("status"));
        user.setGender(Gender.valueOf(rs.getString("gender")));
        user.setAddress(rs.getString("address"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setPhoto(rs.getBytes("photo"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }

    private boolean isEmailExists(String email) {
        String query = "SELECT 1 FROM Users WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email.toLowerCase().trim());
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return false;
        }
    }

    private boolean isPhoneExists(String phoneNumber) {
        String query = "SELECT 1 FROM Users WHERE phone_number = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, phoneNumber.trim());
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
            return false;
        }
    }

    private void sendWelcomeEmail(User user) {
    String subject = "Welcome to TMS";
    String body;
    
    if (user.getRole() == Role.TRAINER || user.getRole() == Role.ADMIN) {
        body = """
            Dear %s,

            Welcome to the Training Management System!

            Your account has been created and is pending activation:
            - Name: %s %s
            - Email: %s
            - Role: %s

            Our admin team will review and activate your account shortly.
            You will receive another email once your account is activated.

            Regards,
            TMS Team
        """.formatted(
            user.getFirstName(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().toString()
        );
    } else {
        body = """
            Dear %s,

            Welcome to the Training Management System!

            Your account has been successfully created:
            - Name: %s %s
            - Email: %s
            - Role: %s

            You can now log in using your registered credentials.

            Regards,
            TMS Team
        """.formatted(
            user.getFirstName(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().toString()
        );
    }

    emailSender.sendEmail(user.getEmail(), subject, body);
}

    private void sendPasswordResetNotification(String email) {
        String subject = "Your TMS Password Has Been Reset";
        String body = """
            Dear User,

            Your password has been successfully reset at %s.
            
            If you did not request this change, please contact our support team immediately.

            Regards,
            TMS Team
        """.formatted(LocalDateTime.now());

        emailSender.sendEmail(email, subject, body);
    }

    // =================== Additional Features ===================
    public List<User> getAllUsers() {
        String query = "SELECT * FROM Users ORDER BY last_name, first_name";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            List<User> users = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<User> getUsersByRole(Role role) {
        String query = "SELECT * FROM Users WHERE role = ? ORDER BY last_name, first_name";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, role.name());
            
            List<User> users = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            System.err.println("Error fetching users by role: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}