package com.tms.Model.DAO;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Feedback;
import com.tms.Model.Session;
import com.tms.Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeedbackDAO implements AutoCloseable {
    private final Connection connection;

    public FeedbackDAO() throws SQLException, ClassNotFoundException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Integer> createFeedback(Feedback feedback) throws SQLException {
        validateFeedback(feedback);
        
        String sql = """
            INSERT INTO Feedbacks (session_id, trainee_id, comment, rating)
            VALUES (?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feedback.getSession().getId());
            stmt.setInt(2, feedback.getTrainee().getId());
            stmt.setString(3, feedback.getComment());
            stmt.setInt(4, feedback.getRating());

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

    public Optional<Feedback> getFeedbackById(int id) throws SQLException {
        String sql = """
            SELECT f.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Feedbacks f
            LEFT JOIN Users u ON f.trainee_id = u.id
            LEFT JOIN Sessions s ON f.session_id = s.id
            WHERE f.id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Feedback> getAllFeedbacks() throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
            SELECT f.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Feedbacks f
            LEFT JOIN Users u ON f.trainee_id = u.id
            LEFT JOIN Sessions s ON f.session_id = s.id
            ORDER BY f.submitted_at DESC
            """;
            
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                feedbacks.add(mapResultSetToFeedback(rs));
            }
        }
        return feedbacks;
    }

    // =================== Specialized Queries ===================

    public List<Feedback> getFeedbacksBySessionId(int sessionId) throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
            SELECT f.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Feedbacks f
            LEFT JOIN Users u ON f.trainee_id = u.id
            LEFT JOIN Sessions s ON f.session_id = s.id
            WHERE f.session_id = ?
            ORDER BY f.submitted_at DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
        }
        return feedbacks;
    }

    public List<Feedback> getFeedbacksByTraineeId(int traineeId) throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = """
            SELECT f.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Feedbacks f
            LEFT JOIN Users u ON f.trainee_id = u.id
            LEFT JOIN Sessions s ON f.session_id = s.id
            WHERE f.trainee_id = ?
            ORDER BY f.submitted_at DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(mapResultSetToFeedback(rs));
                }
            }
        }
        return feedbacks;
    }

    public boolean deleteFeedback(int id) throws SQLException {
        String sql = "DELETE FROM Feedbacks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // =================== Helper Methods ===================

    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getInt("id"));
        feedback.setComment(rs.getString("comment"));
        
        // Convert rating value to enum
        int ratingValue = rs.getInt("rating");
        feedback.setRating(ratingValue);
        
        // Handle timestamps
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        feedback.setSubmittedAt(submittedAt != null ? submittedAt : null);
        
        // Create minimal trainee object
        User trainee = new User();
        trainee.setId(rs.getInt("trainee_id"));
        trainee.setFirstName(rs.getString("first_name"));
        trainee.setLastName(rs.getString("last_name"));
        feedback.setTrainee(trainee);
        
        // Create minimal session object
        Session session = new Session();
        session.setId(rs.getInt("session_id"));
        if (rs.getDate("session_date") != null) {
            session.setSessionDate(rs.getDate("session_date").toLocalDate());
            session.setStartTime(rs.getTime("start_time"));
        }
        feedback.setSession(session);
        
        return feedback;
    }

    private void validateFeedback(Feedback feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }
        if (feedback.getSession().getId() <= 0) {
            throw new IllegalArgumentException("Invalid session ID");
        }
        if (feedback.getSession().getId() <= 0) {
            throw new IllegalArgumentException("Invalid trainee ID");
        }
        if (feedback.getComment() == null || feedback.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (feedback.getRating() < 0 ) {
            throw new IllegalArgumentException("Rating must be specified");
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // =================== Business Logic Methods ===================

    public boolean hasTraineeSubmittedFeedback(int traineeId, int sessionId) throws SQLException {
        String sql = "SELECT 1 FROM Feedbacks WHERE trainee_id = ? AND session_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public double getAverageRatingForSession(int sessionId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM Feedbacks WHERE session_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
                return 0.0;
            }
        }
    }

    public int countFeedbacksForSession(int sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE session_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
}