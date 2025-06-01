package com.tms.DAO;

import com.tms.Model.AttendanceStats;
import com.tms.Model.Certificate;
import com.tms.Model.DashboardStats;
import com.tms.Model.Material;
import com.tms.Model.Session;
import com.tms.Model.Session.Status;
import com.tms.Model.User.Role;
import com.tms.Model.TrainingProgress;
import com.tms.Model.User;

import java.sql.*;
import java.util.*;

import com.tms.DB.DatabaseConnection;

public class TraineeDashboardDAO {
    private final DatabaseConnection dataSource;

    public TraineeDashboardDAO(DatabaseConnection dataSource) {
        this.dataSource = dataSource;
    }

    // 1. User Data Methods
    public User getUserById(int userId) throws SQLException {
        final String sql = "SELECT id, first_name, last_name, email, role, photo FROM Users WHERE id = ?";
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();

                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(Role.valueOf(rs.getString("role")));    
                    user.setPhoto(rs.getBytes("photo"));    
                        
                        
                        
                }
            }
        }
        return null;
    }

    // 2. Dashboard Statistics
    public DashboardStats getDashboardStats(int traineeId) throws SQLException {
        DashboardStats stats = new DashboardStats();
        
        // Upcoming Sessions Count
        final String sessionsSql = """
            SELECT COUNT(*) AS count 
            FROM Sessions s
            JOIN Training_Courses tc ON s.training_course_id = tc.id
            JOIN Enrollments e ON tc.training_id = e.training_id
            WHERE e.trainee_id = ? AND s.status = 'COMING'
            AND s.session_date >= CURRENT_DATE""";
        
        // Active Courses Count
        final String coursesSql = """
            SELECT COUNT(DISTINCT tc.course_id) AS count 
            FROM Training_Courses tc
            JOIN Enrollments e ON tc.training_id = e.training_id
            WHERE e.trainee_id = ? AND tc.status IN ('COMING', 'LIVE')""";
        
        // Attendance Rate
        final String attendanceSql = """
            SELECT 
                COUNT(CASE WHEN status = 'PRESENT' THEN 1 END) * 100.0 / 
                NULLIF(COUNT(CASE WHEN status IN ('PRESENT', 'ABSENT', 'LATE') THEN 1 END), 0) AS rate
            FROM Attendance
            WHERE trainee_id = ?""";
        
        // Certificates Count
        final String certificatesSql = "SELECT COUNT(*) AS count FROM Certificates WHERE trainee_id = ?";
        
        try (Connection conn = dataSource.getDatabaseConnection()) {
            // Upcoming Sessions
            try (PreparedStatement stmt = conn.prepareStatement(sessionsSql)) {
                stmt.setInt(1, traineeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setUpcomingSessions(rs.getInt("count"));
                    }
                }
            }
            
            // Active Courses
            try (PreparedStatement stmt = conn.prepareStatement(coursesSql)) {
                stmt.setInt(1, traineeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setActiveCourses(rs.getInt("count"));
                    }
                }
            }
            
            // Attendance Rate
            try (PreparedStatement stmt = conn.prepareStatement(attendanceSql)) {
                stmt.setInt(1, traineeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setAttendanceRate(rs.getDouble("rate"));
                    }
                }
            }
            
            // Certificates
            try (PreparedStatement stmt = conn.prepareStatement(certificatesSql)) {
                stmt.setInt(1, traineeId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setCertificatesCount(rs.getInt("count"));
                    }
                }
            }
        }
        
        return stats;
    }

    // 3. Upcoming Sessions
    public List<Session> getUpcomingSessions(int traineeId, int limit) throws SQLException {
        final String sql = """
            SELECT s.id, s.session_date, s.start_time, s.end_time, s.status,
                   t.title AS training_title, r.location AS room_location
            FROM Sessions s
            JOIN Training_Courses tc ON s.training_course_id = tc.id
            JOIN Trainings t ON tc.training_id = t.id
            LEFT JOIN Rooms r ON t.room_id = r.id
            JOIN Enrollments e ON t.id = e.training_id
            WHERE e.trainee_id = ? AND s.status = 'COMING'
            AND s.session_date >= CURRENT_DATE
            ORDER BY s.session_date, s.start_time
            LIMIT ?""";
        
        List<Session> sessions = new ArrayList<>();
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, traineeId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Session session = new Session();
                    session.setId(rs.getInt("id"));
                    session.setSessionDate(rs.getDate("session_date").toLocalDate());
                    session.setStartTime(rs.getTime("start_time"));
                    session.setEndTime(rs.getTime("end_time"));
                    session.setStatus(Status.valueOf(rs.getString("status")));
                    session.setTitle(rs.getString("training_title"));
                    session.setLocation(rs.getString("room_location"));
                    
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

    // 4. Current Trainings with Progress
    public List<TrainingProgress> getCurrentTrainings(int traineeId) throws SQLException {
        final String sql = """
            SELECT 
                t.id, t.title, 
                tc.start_date, tc.end_date,
                tc.total_sessions, tc.completed_sessions,
                u.id AS trainer_id, u.first_name, u.last_name, u.photo
            FROM Trainings t
            JOIN Training_Courses tc ON t.id = tc.training_id
            JOIN Courses c ON tc.course_id = c.id
            JOIN Users u ON c.trainer_id = u.id
            JOIN Enrollments e ON t.id = e.training_id
            WHERE e.trainee_id = ? AND tc.status IN ('COMING', 'LIVE')
            ORDER BY tc.start_date""";
        
        List<TrainingProgress> trainings = new ArrayList<>();
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, traineeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TrainingProgress training = new TrainingProgress();
                    training.setId(rs.getInt("id"));
                    training.setTitle(rs.getString("title"));
                    
                    // Calculate progress percentage
                    int total = rs.getInt("total_sessions");
                    int completed = rs.getInt("completed_sessions");
                    training.setProgress((int) Math.round((completed * 100.0) / total));
                    
                    // Format dates
                    training.setDates(String.format("%s - %s", 
                        rs.getDate("start_date"), 
                        rs.getDate("end_date")));
                    
                    training.setSessionsCompleted(completed);
                    training.setTotalSessions(total);
                    
                    // Trainer info
                    User trainer = new User();
                    trainer.setId(rs.getInt("trainer_id"));
                    trainer.setFirstName(rs.getString("first_name"));
                    trainer.setLastName( rs.getString("last_name"));
                    trainer.setPhoto(rs.getBytes("photo"));
                    training.setTrainer(trainer);
                    
                    trainings.add(training);
                }
            }
        }
        return trainings;
    }

    // 5. Attendance Data
    public AttendanceStats getAttendanceStats(int traineeId) throws SQLException {
        final String sql = """
            SELECT 
                COUNT(CASE WHEN status = 'PRESENT' THEN 1 END) AS present,
                COUNT(CASE WHEN status = 'ABSENT' THEN 1 END) AS absent,
                COUNT(CASE WHEN status = 'LATE' THEN 1 END) AS late
            FROM Attendance
            WHERE trainee_id = ?""";
        
        AttendanceStats stats = new AttendanceStats();
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, traineeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setPresent(rs.getInt("present"));
                    stats.setAbsent(rs.getInt("absent"));
                    stats.setLate(rs.getInt("late"));
                }
            }
        }
        return stats;
    }

    // 6. Recent Materials
    public List<Material> getRecentMaterials(int traineeId, int limit) throws SQLException {
        final String sql = """
            SELECT m.id, m.title, m.file_type, m.file_size, m.created_at
            FROM Materials m
            JOIN Sessions s ON m.session_id = s.id
            JOIN Training_Courses tc ON s.training_course_id = tc.id
            JOIN Enrollments e ON tc.training_id = e.training_id
            WHERE e.trainee_id = ?
            ORDER BY m.created_at DESC
            LIMIT ?""";
        
        List<Material> materials = new ArrayList<>();
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, traineeId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getInt("id"));
                    material.setTitle(rs.getString("title"));
                    material.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    materials.add(material);
                }
            }
        }
        return materials;
    }

    // 7. Certificates
    public List<Certificate> getCertificates(int traineeId) throws SQLException {
        final String sql = """
            SELECT c.id, t.title, c.issued_at, c.certificate_number
            FROM Certificates c
            JOIN Trainings t ON c.training_id = t.id
            WHERE c.trainee_id = ?
            ORDER BY c.issued_at DESC""";
        
        List<Certificate> certificates = new ArrayList<>();
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, traineeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Certificate cert = new Certificate();
                    cert.setId(rs.getInt("id"));
                    cert.setIssuedAt(rs.getTimestamp("issued_at"));
                    
                    certificates.add(cert);
                }
            }
        }
        return certificates;
    }

    // 8. Download Material File
    public byte[] getMaterialFile(int materialId) throws SQLException {
        final String sql = "SELECT file_data FROM Materials WHERE id = ?";
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, materialId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("file_data");
                }
            }
        }
        return null;
    }

    // 9. Download Certificate File
    public byte[] getCertificateFile(int certificateId) throws SQLException {
        final String sql = "SELECT certificate_file FROM Certificates WHERE id = ?";
        
        try (Connection conn = dataSource.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, certificateId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("certificate_file");
                }
            }
        }
        return null;
    }

    // Helper method to format file size
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
}