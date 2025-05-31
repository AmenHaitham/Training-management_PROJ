package com.tms.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Session;
import com.tms.Model.Session.Status;

public class SessionDAO implements AutoCloseable {
    private Connection connection;

    public SessionDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                DatabaseConnection.releaseConnection(connection);
            } catch (Exception e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    // =================== CRUD Operations ===================

    public Optional<Session> createSession(Session session) throws SQLException, ClassNotFoundException {
        validateSession(session);
        
        String sql = """
            INSERT INTO Sessions (training_course_id, session_date, start_time, end_time, status)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, session.getTrainingCourse().getId());
            stmt.setDate(2, java.sql.Date.valueOf(session.getSessionDate()));
            stmt.setTime(3, session.getStartTime());
            stmt.setTime(4, session.getEndTime());
            stmt.setString(5, session.getStatus().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Optional.of(mapResultSetToSession(generatedKeys));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Session> getSessionById(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Sessions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSession(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Session> getAllSessions() throws SQLException, ClassNotFoundException {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM Sessions ORDER BY session_date, start_time";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        }
        return sessions;
    }

    public Optional<Session> updateSession(Session session) throws SQLException, ClassNotFoundException {
        validateSession(session);
        
        String sql = """
            UPDATE Sessions SET 
                training_course_id = ?, 
                session_date = ?, 
                start_time = ?, 
                end_time = ?, 
                status = ?, 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, session.getTrainingCourse().getId());
            stmt.setDate(2, java.sql.Date.valueOf(session.getSessionDate()));
            stmt.setTime(3, session.getStartTime());
            stmt.setTime(4, session.getEndTime());
            stmt.setString(5, session.getStatus().name());
            stmt.setInt(6, session.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return Optional.of(session); // Return the updated session object
            } else {
                return Optional.empty(); // No session was updated (maybe ID didn't exist)
            }
        }
    }

    public boolean deleteSession(int id) throws SQLException {
        String sql = "DELETE FROM Sessions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // =================== Specialized Queries ===================

    public List<Session> getSessionsByTrainingCourse(int trainingCourseId) throws SQLException, ClassNotFoundException {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT * FROM Sessions 
            WHERE training_course_id = ? 
            ORDER BY session_date, start_time
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingCourseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }
        }
        return sessions;
    }

    public List<Session> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException, ClassNotFoundException {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT * FROM Sessions 
            WHERE session_date BETWEEN ? AND ? 
            ORDER BY session_date, start_time
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }
        }
        return sessions;
    }

    public List<Session> getSessionsByStatus(Session.Status status) throws SQLException, ClassNotFoundException {
        List<Session> sessions = new ArrayList<>();
        String sql = """
            SELECT * FROM Sessions 
            WHERE status = ? 
            ORDER BY session_date, start_time
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }
        }
        return sessions;
    }

    public boolean hasTimeConflict(int trainingCourseId, LocalDate date, LocalTime startTime, 
                             LocalTime endTime, Integer excludeSessionId) throws SQLException {
        String sql = """
            SELECT 1 FROM Sessions 
            WHERE training_course_id = ? 
            AND session_date = ? 
            AND (
                (start_time <= ? AND end_time > ?) OR  -- New session starts during existing
                (start_time < ? AND end_time >= ?) OR  -- New session ends during existing
                (start_time >= ? AND end_time <= ?)    -- New session completely within existing
            )
            AND (? IS NULL OR id != ?)
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingCourseId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(startTime));
            stmt.setTime(4, Time.valueOf(startTime));
            stmt.setTime(5, Time.valueOf(endTime));
            stmt.setTime(6, Time.valueOf(endTime));
            stmt.setTime(7, Time.valueOf(startTime));
            stmt.setTime(8, Time.valueOf(endTime));
            if (excludeSessionId != null) {
                stmt.setInt(9, excludeSessionId);
                stmt.setInt(10, excludeSessionId);
            } else {
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Session> getRecentSessions(int limit) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Sessions ORDER BY created_at DESC LIMIT ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Session> sessions = new ArrayList<>();
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
                return sessions;
            }
        }
    }

    // =================== Helper Methods ===================

    private Session mapResultSetToSession(ResultSet rs) throws SQLException, ClassNotFoundException {
        Session session = new Session();
        session.setId(rs.getInt("id"));
        
        // Get training course using a new connection
        try (TrainingCourseDAO trainingCourseDAO = new TrainingCourseDAO()) {
            session.setTrainingCourse(trainingCourseDAO.getTrainingCourseById(rs.getInt("training_course_id")).get());
        }
        
        session.setSessionDate(rs.getDate("session_date").toLocalDate());
        session.setStartTime(rs.getTime("start_time"));
        session.setEndTime(rs.getTime("end_time"));
        session.setStatus(Session.Status.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        session.setCreatedAt(createdAt != null ? createdAt : null);
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        session.setUpdatedAt(updatedAt != null ? updatedAt : null);
        
        return session;
    }

    private void validateSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (session.getTrainingCourse().getId() <= 0) {
            throw new IllegalArgumentException("Invalid training course ID");
        }
        if (session.getSessionDate() == null) {
            throw new IllegalArgumentException("Session date cannot be null");
        }
        if (session.getStartTime() == null || session.getEndTime() == null) {
            throw new IllegalArgumentException("Start and end times cannot be null");
        }
        if (!session.getEndTime().after(session.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (session.getStatus() == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    // =================== Business Logic Methods ===================

    public boolean updateSessionStatus(int sessionId, Status newStatus) throws SQLException {
        String sql = "UPDATE Sessions SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, sessionId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean completeSession(int sessionId) throws SQLException {
        return updateSessionStatus(sessionId, Status.COMPLETED);
    }

    public boolean cancelSession(int sessionId) throws SQLException {
        return updateSessionStatus(sessionId, Status.CANCELLED);
    }

    public static void main(String[] args) {
        // Existing main method content
    }
}