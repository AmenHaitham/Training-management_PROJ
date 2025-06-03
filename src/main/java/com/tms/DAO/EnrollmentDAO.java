package com.tms.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Enrollment;
import com.tms.Model.Training;
import com.tms.Model.User;

public class EnrollmentDAO implements AutoCloseable {
    private final Connection connection;

    public EnrollmentDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Integer> createEnrollment(Enrollment enrollment) throws SQLException {
    validateEnrollment(enrollment);
    
    String sql = """
        INSERT INTO Enrollments (trainee_id, training_id)
        VALUES (?, ?)
        """;
    
    try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setInt(1, enrollment.getTrainee().getId());
        stmt.setInt(2, enrollment.getTraining().getId());

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            return Optional.empty();
        }

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return Optional.of(generatedKeys.getInt(1));
            }
            return Optional.empty();
        }
    }
}


    public Optional<Enrollment> getEnrollmentById(int id) throws SQLException {
        String sql = """
            SELECT e.*, u.first_name, u.last_name, u.email, t.title as training_title
            FROM Enrollments e
            LEFT JOIN Users u ON e.trainee_id = u.id
            LEFT JOIN Trainings t ON e.training_id = t.id
            WHERE e.id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEnrollment(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, u.first_name, u.last_name, u.email, t.title as training_title
            FROM Enrollments e
            LEFT JOIN Users u ON e.trainee_id = u.id
            LEFT JOIN Trainings t ON e.training_id = t.id
            ORDER BY e.enrollment_date DESC
            """;
            
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        }
        return enrollments;
    }

    // =================== Specialized Queries ===================

    public List<Enrollment> getEnrollmentsByTraineeId(int traineeId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, u.first_name, u.last_name, u.email, t.title as training_title
            FROM Enrollments e
            LEFT JOIN Users u ON e.trainee_id = u.id
            LEFT JOIN Trainings t ON e.training_id = t.id
            WHERE e.trainee_id = ?
            ORDER BY e.enrollment_date DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByTrainingId(int trainingId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.*, u.first_name, u.last_name, u.email, t.title as training_title
            FROM Enrollments e
            LEFT JOIN Users u ON e.trainee_id = u.id
            LEFT JOIN Trainings t ON e.training_id = t.id
            WHERE e.training_id = ?
            ORDER BY e.enrollment_date DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }
        return enrollments;
    }

    public boolean deleteEnrollment(int id) throws SQLException {
        String sql = "DELETE FROM Enrollments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // =================== Helper Methods ===================

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getInt("id"));
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date"));
        
        // Create minimal trainee object
        User trainee = new User();
        trainee.setId(rs.getInt("trainee_id"));
        trainee.setFirstName(rs.getString("first_name"));
        trainee.setLastName(rs.getString("last_name"));
        trainee.setEmail(rs.getString("email"));
        enrollment.setTrainee(trainee);
        
        // Create minimal training object
        Training training = new Training();
        training.setId(rs.getInt("training_id"));
        training.setTitle(rs.getString("training_title"));
        enrollment.setTraining(training);
        
        return enrollment;
    }

    private void validateEnrollment(Enrollment enrollment) {
    if (enrollment == null) {
        throw new IllegalArgumentException("Enrollment cannot be null");
    }
    if (enrollment.getTrainee() == null || enrollment.getTrainee().getId() <= 0) {
        throw new IllegalArgumentException("Invalid trainee");
    }
    if (enrollment.getTraining() == null || enrollment.getTraining().getId() <= 0) {
        throw new IllegalArgumentException("Invalid training");
    }
}

    @Override
    public void close() {
        if (connection != null) {
            try {
                DatabaseConnection.releaseConnection(connection);
            } catch (Exception e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            }
        }
    }

    // =================== Business Logic Methods ===================

    public boolean isTraineeEnrolled(int traineeId, int trainingId) throws SQLException {
        String sql = "SELECT 1 FROM Enrollments WHERE trainee_id = ? AND training_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countEnrollmentsForTraining(int trainingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Enrollments WHERE training_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    public boolean unenrollTrainee(int traineeId, int trainingId) throws SQLException {
        String sql = "DELETE FROM Enrollments WHERE trainee_id = ? AND training_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, trainingId);
            return stmt.executeUpdate() > 0;
        }
    }

    public static void main(String[] args) {
    try (EnrollmentDAO enrollmentDAO = new EnrollmentDAO()) {
        System.out.println("=== Testing EnrollmentDAO ===");

        // Create test data
        User testTrainee = new User();
        testTrainee.setId(5); // Replace with valid trainee ID
        testTrainee.setFirstName("John");
        testTrainee.setLastName("Doe");
        testTrainee.setEmail("john.doe@example.com");
        
        Training testTraining = new Training();
        testTraining.setId(2); // Replace with valid training ID
        testTraining.setTitle("Advanced Java Programming");

        // 1. Test createEnrollment
        System.out.println("\n--- Testing createEnrollment ---");
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setTrainee(testTrainee);
        newEnrollment.setTraining(testTraining);
        
        Optional<Integer> enrollmentId = enrollmentDAO.createEnrollment(newEnrollment);
        if (enrollmentId.isPresent()) {
            System.out.println("Created enrollment with ID: " + enrollmentId.get());
            newEnrollment.setId(enrollmentId.get());
        } else {
            System.out.println("Failed to create enrollment");
            return;
        }

        // 2. Test getEnrollmentById
        System.out.println("\n--- Testing getEnrollmentById ---");
        Optional<Enrollment> fetchedEnrollment = enrollmentDAO.getEnrollmentById(newEnrollment.getId());
        fetchedEnrollment.ifPresentOrElse(
            e -> System.out.printf("Fetched enrollment: ID=%d, Trainee=%s %s, Training=%s%n",
                e.getId(), 
                e.getTrainee().getFirstName(), 
                e.getTrainee().getLastName(),
                e.getTraining().getTitle()),
            () -> System.out.println("Enrollment not found")
        );

        // 3. Test getAllEnrollments
        System.out.println("\n--- Testing getAllEnrollments ---");
        List<Enrollment> allEnrollments = enrollmentDAO.getAllEnrollments();
        System.out.println("Total enrollments: " + allEnrollments.size());
        allEnrollments.forEach(e -> System.out.printf(
            "Enrollment %d: %s %s in %s%n", 
            e.getId(), 
            e.getTrainee().getFirstName(),
            e.getTrainee().getLastName(),
            e.getTraining().getTitle())
        );

        // 4. Test getEnrollmentsByTraineeId
        System.out.println("\n--- Testing getEnrollmentsByTraineeId ---");
        List<Enrollment> traineeEnrollments = enrollmentDAO.getEnrollmentsByTraineeId(testTrainee.getId());
        System.out.println("Enrollments for trainee " + testTrainee.getId() + ": " + traineeEnrollments.size());
        traineeEnrollments.forEach(e -> System.out.println(
            "Training: " + e.getTraining().getTitle() + 
            ", Enrolled on: " + e.getEnrollmentDate())
        );

        // 5. Test getEnrollmentsByTrainingId
        System.out.println("\n--- Testing getEnrollmentsByTrainingId ---");
        List<Enrollment> trainingEnrollments = enrollmentDAO.getEnrollmentsByTrainingId(testTraining.getId());
        System.out.println("Enrollments for training " + testTraining.getId() + ": " + trainingEnrollments.size());
        trainingEnrollments.forEach(e -> System.out.println(
            "Trainee: " + e.getTrainee().getFirstName() + " " + e.getTrainee().getLastName() +
            ", Enrolled on: " + e.getEnrollmentDate())
        );

        // 6. Test isTraineeEnrolled
        System.out.println("\n--- Testing isTraineeEnrolled ---");
        boolean isEnrolled = enrollmentDAO.isTraineeEnrolled(testTrainee.getId(), testTraining.getId());
        System.out.println("Is trainee enrolled? " + isEnrolled);

        // 7. Test countEnrollmentsForTraining
        System.out.println("\n--- Testing countEnrollmentsForTraining ---");
        int enrollmentCount = enrollmentDAO.countEnrollmentsForTraining(testTraining.getId());
        System.out.println("Number of enrollments for training: " + enrollmentCount);

        // 8. Test unenrollTrainee
        System.out.println("\n--- Testing unenrollTrainee ---");
        boolean unenrolled = enrollmentDAO.unenrollTrainee(testTrainee.getId(), testTraining.getId());
        System.out.println("Unenrollment successful? " + unenrolled);
        
        // Verify unenrollment
        isEnrolled = enrollmentDAO.isTraineeEnrolled(testTrainee.getId(), testTraining.getId());
        System.out.println("Is trainee still enrolled? " + isEnrolled);

        // 9. Test deleteEnrollment (if needed)
        System.out.println("\n--- Testing deleteEnrollment ---");
        boolean deleted = enrollmentDAO.deleteEnrollment(newEnrollment.getId());
        System.out.println("Deletion successful? " + deleted);
        
        // Verify deletion
        fetchedEnrollment = enrollmentDAO.getEnrollmentById(newEnrollment.getId());
        System.out.println("Enrollment still exists? " + fetchedEnrollment.isPresent());

    } catch (Exception e) {
        e.printStackTrace();
    }
}
}