package com.example.dao.impl;

import com.example.dao.UserDao;
import com.example.model.User;
import com.example.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDao
 */
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String SELECT_BY_ID = 
            "SELECT id, username, first_name, last_name, email, phone, password, role, birth_date, created_at, updated_at FROM users WHERE id = ?";
    private static final String SELECT_ALL = 
            "SELECT id, username, first_name, last_name, email, phone, password, role, birth_date, created_at, updated_at FROM users";
    private static final String SELECT_BY_USERNAME = 
            "SELECT id, username, first_name, last_name, email, phone, password, role, birth_date, created_at, updated_at FROM users WHERE username = ?";
    private static final String SELECT_BY_EMAIL = 
            "SELECT id, username, first_name, last_name, email, phone, password, role, birth_date, created_at, updated_at FROM users WHERE email = ?";
    private static final String SELECT_BY_ROLE = 
            "SELECT id, username, first_name, last_name, email, phone, password, role, birth_date, created_at, updated_at FROM users WHERE role = ?";
    private static final String COUNT_ALL = 
            "SELECT COUNT(*) FROM users";
    private static final String EXISTS_BY_ID = 
            "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";
    private static final String EXISTS_BY_USERNAME = 
            "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
    private static final String EXISTS_BY_EMAIL = 
            "SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)";
    private static final String INSERT = 
            "INSERT INTO users (username, first_name, last_name, email, phone, password, role, birth_date, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String UPDATE = 
            "UPDATE users SET username = ?, first_name = ?, last_name = ?, email = ?, phone = ?, " +
            "password = ?, role = ?, birth_date = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE = 
            "DELETE FROM users WHERE id = ?";

    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    conn.commit();
                    return Optional.of(user);
                }
            }
            conn.commit();
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }
        
        return users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    conn.commit();
                    return Optional.of(user);
                }
            }
            conn.commit();
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by username: " + username, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EMAIL)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    conn.commit();
                    return Optional.of(user);
                }
            }
            conn.commit();
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by email: " + email, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ROLE)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding users by role: " + role, e);
        }
        
        return users;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            
            // Hash the password if it's not already hashed (assumes passwords starting with $2a$ are already hashed)
            String password = user.getPassword();
            if (password != null && !password.startsWith("$2a$")) {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
            }
            stmt.setString(6, password);
            
            stmt.setString(7, user.getRole());
            
            if (user.getBirthDate() != null) {
                stmt.setDate(8, Date.valueOf(user.getBirthDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            stmt.setTimestamp(10, Timestamp.valueOf(now));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
            
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error inserting user: " + user.getUsername(), e);
        }
        
        return user;
    }

    private User update(User user) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            
            // Only update password if it has changed and is not already hashed
            String password = user.getPassword();
            if (password != null && !password.startsWith("$2a$")) {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
            }
            stmt.setString(6, password);
            
            stmt.setString(7, user.getRole());
            
            if (user.getBirthDate() != null) {
                stmt.setDate(8, Date.valueOf(user.getBirthDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            stmt.setLong(10, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                user.setUpdatedAt(now);
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error updating user: " + user.getId(), e);
        }
        
        return user;
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user by ID: " + id, e);
            return false;
        }
    }

    @Override
    public boolean existsById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getBoolean(1);
                    conn.commit();
                    return exists;
                }
            }
            conn.commit();
            return false;
        } catch (SQLException e) {
            logger.error("Error checking if user exists by ID: " + id, e);
            return false;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getBoolean(1);
                    conn.commit();
                    return exists;
                }
            }
            conn.commit();
            return false;
        } catch (SQLException e) {
            logger.error("Error checking if user exists by username: " + username, e);
            return false;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_EMAIL)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getBoolean(1);
                    conn.commit();
                    return exists;
                }
            }
            conn.commit();
            return false;
        } catch (SQLException e) {
            logger.error("Error checking if user exists by email: " + email, e);
            return false;
        }
    }

    @Override
    public long count() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                long count = rs.getLong(1);
                conn.commit();
                return count;
            }
            conn.commit();
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users", e);
            return 0;
        }
    }

    @Override
    public Optional<User> authenticate(String username, String rawPassword) {
        Optional<User> optionalUser = findByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (BCrypt.checkpw(rawPassword, user.getPassword())) {
                return optionalUser;
            }
        }
        
        return Optional.empty();
    }

    /**
     * Maps a ResultSet row to a User object
     * @param rs The ResultSet positioned at the current row
     * @return The mapped User object
     * @throws SQLException if a database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        
        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            user.setBirthDate(birthDate.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
} 