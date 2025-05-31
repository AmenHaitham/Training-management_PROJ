package com.tms.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Attendance;
import com.tms.Model.Session;
import com.tms.Model.User;

public class AttendanceDAO implements AutoCloseable {

    private final Connection connection;

    public AttendanceDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================
    public Attendance markAttendance(Attendance attendance) throws SQLException {
    validateAttendance(attendance);

    String sql = """
        INSERT INTO Attendance (trainee_id, session_id, status) 
        VALUES (?, ?, ?) 
        ON CONFLICT (trainee_id, session_id) 
        DO UPDATE SET status = EXCLUDED.status, recorded_at = CURRENT_TIMESTAMP
        RETURNING recorded_at
        """;

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, attendance.getTrainee().getId());
        stmt.setInt(2, attendance.getSession().getId());
        stmt.setString(3, attendance.getStatus());

        // Execute update and fetch the updated timestamp
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Timestamp recordedAt = rs.getTimestamp("recorded_at");
                attendance.setRecordedAt(recordedAt); // Assuming a setRecordedAt() method exists
            }
        }

        System.out.println("Attendance marked for Trainee ID: " + attendance.getTrainee().getId() +
                           ", Session ID: " + attendance.getSession().getId());

        return attendance;
    } catch (SQLException e) {
        System.err.println("Error while marking attendance: " + e.getMessage());
        throw e; // Re-throw to allow higher-level handling
    }
}

public List<Attendance> getAllAttendances() throws SQLException, ClassNotFoundException {
    String sql = """
        SELECT a.*, u.first_name, u.last_name, s.session_date, s.start_time
        FROM Attendance a
        LEFT JOIN Users u ON a.trainee_id = u.id
        LEFT JOIN Sessions s ON a.session_id = s.id
        ORDER BY s.session_date, s.start_time
        """;

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        try (ResultSet rs = stmt.executeQuery()) {
            List<Attendance> attendances = new ArrayList<>();
            while (rs.next()) {
                attendances.add(mapResultSetToAttendance(rs));
            }
            return attendances;
        }
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

    public boolean deleteAttendance(int attendanceId) throws SQLException {
        String sql = "DELETE FROM Attendance WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Attendance updatAttendance(Attendance attendance) throws SQLException {
        validateAttendance(attendance);
        String sql = "UPDATE Attendance SET trainee_id = ?, session_id = ?, status = ?, recorded_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attendance.getTrainee().getId());
            stmt.setInt(2, attendance.getSession().getId());
            stmt.setString(3, attendance.getStatus());
            stmt.setInt(4, attendance.getId());
            stmt.executeUpdate();
            return attendance;
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
        attendance.setId(rs.getInt("id"));
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

    public static void main(String[] args) {
    try (AttendanceDAO attendanceDAO = new AttendanceDAO()) {
        // Create test data
        User testTrainee = new User();
        testTrainee.setId(3); // Replace with valid trainee ID
        testTrainee.setFirstName("John");
        testTrainee.setLastName("Doe");
        
        Session testSession = new Session();
        testSession.setId(1); // Replace with valid session ID
        testSession.setSessionDate(java.time.LocalDate.now());
        testSession.setStartTime(java.sql.Time.valueOf("10:00:00"));
        
        Attendance testAttendance = new Attendance();
        testAttendance.setTrainee(testTrainee);
        testAttendance.setSession(testSession);
        testAttendance.setStatus("PRESENT");

        // Test markAttendance
        System.out.println("=== Testing markAttendance ===");
        Attendance markedAttendance = attendanceDAO.markAttendance(testAttendance);
        System.out.println("Marked attendance: " + markedAttendance);
        System.out.println("Recorded at: " + markedAttendance.getRecordedAt());
        
        // Test getAttendance
        System.out.println("\n=== Testing getAttendance ===");
        Optional<Attendance> retrievedAttendance = attendanceDAO.getAttendance(
            testTrainee.getId(), testSession.getId());
        retrievedAttendance.ifPresentOrElse(
            att -> System.out.println("Retrieved attendance: " + att),
            () -> System.out.println("Attendance not found"));
        
        // Test getAllAttendances
        System.out.println("\n=== Testing getAllAttendances ===");
        List<Attendance> allAttendances = attendanceDAO.getAllAttendances();
        System.out.println("Total attendances: " + allAttendances.size());
        allAttendances.forEach(att -> System.out.println(att));
        
        // Test getAttendanceBySession
        System.out.println("\n=== Testing getAttendanceBySession ===");
        List<Attendance> sessionAttendances = attendanceDAO.getAttendanceBySession(testSession.getId());
        System.out.println("Attendances for session " + testSession.getId() + ": " + sessionAttendances.size());
        sessionAttendances.forEach(att -> System.out.println(att));
        
        // Test getAttendanceByTrainee
        System.out.println("\n=== Testing getAttendanceByTrainee ===");
        List<Attendance> traineeAttendances = attendanceDAO.getAttendanceByTrainee(testTrainee.getId());
        System.out.println("Attendances for trainee " + testTrainee.getId() + ": " + traineeAttendances.size());
        traineeAttendances.forEach(att -> System.out.println(att));
        
        // Test countAttendanceByStatus
        System.out.println("\n=== Testing countAttendanceByStatus ===");
        int presentCount = attendanceDAO.countAttendanceByStatus(testSession.getId(), "PRESENT");
        System.out.println("Present count for session " + testSession.getId() + ": " + presentCount);
        
        // Test isTraineePresent
        System.out.println("\n=== Testing isTraineePresent ===");
        boolean isPresent = attendanceDAO.isTraineePresent(testTrainee.getId(), testSession.getId());
        System.out.println("Is trainee " + testTrainee.getId() + " present in session " + 
            testSession.getId() + ": " + isPresent);
        
        // Test bulkMarkAttendance
        System.out.println("\n=== Testing bulkMarkAttendance ===");
        List<Attendance> bulkAttendances = new ArrayList<>();
        
        // Create another test attendance
        User testTrainee2 = new User();
        testTrainee2.setId(2); // Replace with valid trainee ID
        testTrainee2.setFirstName("Jane");
        testTrainee2.setLastName("Smith");
        
        Attendance testAttendance2 = new Attendance();
        testAttendance2.setTrainee(testTrainee2);
        testAttendance2.setSession(testSession);
        testAttendance2.setStatus("ABSENT");
        
        bulkAttendances.add(testAttendance);
        bulkAttendances.add(testAttendance2);
        
        boolean bulkResult = attendanceDAO.bulkMarkAttendance(bulkAttendances);
        System.out.println("Bulk mark result: " + bulkResult);
        
        // Test updateAttendance
        System.out.println("\n=== Testing updatAttendance ===");
        if (retrievedAttendance.isPresent()) {
            Attendance toUpdate = retrievedAttendance.get();
            toUpdate.setStatus("LATE");
            Attendance updated = attendanceDAO.updatAttendance(toUpdate);
            System.out.println("Updated attendance: " + updated);
        }
        
        // Test deleteAttendance
        System.out.println("\n=== Testing deleteAttendance ===");
        if (retrievedAttendance.isPresent()) {
            boolean deleted = attendanceDAO.deleteAttendance(retrievedAttendance.get().getId());
            System.out.println("Delete result: " + deleted);
        }
        
    } catch (SQLException | ClassNotFoundException e) {
        System.err.println("Error: " + e.getMessage());
    }
}

}
