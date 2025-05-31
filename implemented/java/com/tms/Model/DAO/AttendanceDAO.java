package com.tms.Model.DAO;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Attendance;

import com.tms.Model.Session;
import com.tms.Model.User;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDAO implements AutoCloseable {

    private final Connection connection;

    public AttendanceDAO() throws SQLException, ClassNotFoundException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================
    public boolean markAttendance(Attendance attendance) throws SQLException {
        validateAttendance(attendance);

        String sql = """
            INSERT INTO Attendance (trainee_id, session_id, status) 
            VALUES (?, ?, ?) 
            ON CONFLICT (trainee_id, session_id) 
            DO UPDATE SET status = EXCLUDED.status, recorded_at = CURRENT_TIMESTAMP
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getTrainee().getId());
            stmt.setInt(2, attendance.getSession().getId());
            stmt.setString(3, attendance.getStatus());
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Attendance> getAttendance(int traineeId, int sessionId) throws SQLException, ClassNotFoundException {
        String sql = """
            SELECT a.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Attendance a
            LEFT JOIN Users u ON a.trainee_id = u.id
            LEFT JOIN Sessions s ON a.session_id = s.id
            WHERE a.trainee_id = ? AND a.session_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
                return Optional.empty();
            }
        }
    }

    // =================== Specialized Queries ===================
    public List<Attendance> getAttendanceBySession(int sessionId) throws SQLException, ClassNotFoundException {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = """
            SELECT a.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Attendance a
            LEFT JOIN Users u ON a.trainee_id = u.id
            LEFT JOIN Sessions s ON a.session_id = s.id
            WHERE a.session_id = ?
            ORDER BY u.last_name, u.first_name
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return attendanceList;
    }

    public List<Attendance> getAttendanceByTrainee(int traineeId) throws SQLException, ClassNotFoundException {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = """
            SELECT a.*, u.first_name, u.last_name, s.session_date, s.start_time
            FROM Attendance a
            LEFT JOIN Users u ON a.trainee_id = u.id
            LEFT JOIN Sessions s ON a.session_id = s.id
            WHERE a.trainee_id = ?
            ORDER BY s.session_date, s.start_time
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return attendanceList;
    }

    public int countAttendanceByStatus(int sessionId, String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Attendance WHERE session_id = ? AND status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    // =================== Helper Methods ===================
    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException, ClassNotFoundException {
        Attendance attendance = new Attendance();
        attendance.setTrainee(new UserDAO().getUserById(rs.getInt("trainee_id")).get());
        attendance.setSession(new SessionDAO().getSessionById(rs.getInt("session_id")).get());
        attendance.setStatus(rs.getString("status"));

        // Create minimal trainee object
        User trainee = new User();
        trainee.setId(rs.getInt("trainee_id"));
        trainee.setFirstName(rs.getString("first_name"));
        trainee.setLastName(rs.getString("last_name"));
        attendance.setTrainee(trainee);

        // Create minimal session object
        Session session = new Session();
        session.setId(rs.getInt("session_id"));
        session.setSessionDate(rs.getDate("session_date").toLocalDate());
        session.setStartTime(rs.getTime("start_time"));

        attendance.setSession(session);

        // Handle timestamps
        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        attendance.setRecordedAt(recordedAt != null ? recordedAt : null);

        attendance.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());

        return attendance;
    }

    private void validateAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        if (attendance.getTrainee().getId() <= 0) {
            throw new IllegalArgumentException("Invalid trainee ID");
        }
        if (attendance.getSession().getId() <= 0) {
            throw new IllegalArgumentException("Invalid session ID");
        }
        if (attendance.getStatus() == null) {
            throw new IllegalArgumentException("Status must be specified");
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // =================== Business Logic Methods ===================
    public boolean isTraineePresent(int traineeId, int sessionId) throws SQLException {
        String sql = "SELECT 1 FROM Attendance WHERE trainee_id = ? AND session_id = ? AND status = 'PRESENT'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, traineeId);
            stmt.setInt(2, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean bulkMarkAttendance(List<Attendance> attendanceList) throws SQLException {
        String sql = """
            INSERT INTO Attendance (trainee_id, session_id, status) 
            VALUES (?, ?, ?) 
            ON CONFLICT (trainee_id, session_id) 
            DO UPDATE SET status = EXCLUDED.status, recorded_at = CURRENT_TIMESTAMP
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            for (Attendance attendance : attendanceList) {
                validateAttendance(attendance);
                stmt.setInt(1, attendance.getTrainee().getId());
                stmt.setInt(2, attendance.getSession().getId());
                stmt.setString(3, attendance.getStatus());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            connection.commit();

            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
