package com.training.dao.impl;

import com.training.dao.EnrollmentDAO;
import com.training.model.Enrollment;
import com.training.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {
    
    @Override
    public Enrollment create(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (trainee_id, course_id, program_id, enrollment_date, status, progress, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, enrollment.getTrainee().getId());
            pstmt.setLong(2, enrollment.getCourse() != null ? enrollment.getCourse().getId() : null);
            pstmt.setLong(3, enrollment.getTrainingProgram() != null ? enrollment.getTrainingProgram().getId() : null);
            pstmt.setTimestamp(4, new Timestamp(enrollment.getEnrollmentDate().getTime()));
            pstmt.setString(5, enrollment.getStatus());
            pstmt.setDouble(6, enrollment.getProgress());
            pstmt.setTimestamp(7, new Timestamp(enrollment.getCreatedAt().getTime()));
            pstmt.setTimestamp(8, new Timestamp(enrollment.getUpdatedAt().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating enrollment failed, no ID obtained.");
                }
            }
            
            return enrollment;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Enrollment findById(Long id) {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractEnrollmentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Enrollment> findByTraineeId(Long traineeId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE trainee_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, traineeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(extractEnrollmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }
    
    @Override
    public List<Enrollment> findByCourseId(Long courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(extractEnrollmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }
    
    @Override
    public List<Enrollment> findByTrainingProgramId(Long programId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE program_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, programId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(extractEnrollmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }
    
    @Override
    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }
    
    @Override
    public Enrollment update(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET trainee_id = ?, course_id = ?, program_id = ?, " +
                    "status = ?, progress = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, enrollment.getTrainee().getId());
            pstmt.setLong(2, enrollment.getCourse() != null ? enrollment.getCourse().getId() : null);
            pstmt.setLong(3, enrollment.getTrainingProgram() != null ? enrollment.getTrainingProgram().getId() : null);
            pstmt.setString(4, enrollment.getStatus());
            pstmt.setDouble(5, enrollment.getProgress());
            pstmt.setTimestamp(6, new Timestamp(enrollment.getUpdatedAt().getTime()));
            pstmt.setLong(7, enrollment.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating enrollment failed, no rows affected.");
            }
            
            return enrollment;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getLong("id"));
        // Note: We need to fetch the related entities (trainee, course, program) separately
        enrollment.setEnrollmentDate(rs.getTimestamp("enrollment_date"));
        enrollment.setCompletionDate(rs.getTimestamp("completion_date"));
        enrollment.setStatus(rs.getString("status"));
        enrollment.setProgress(rs.getDouble("progress"));
        enrollment.setCreatedAt(rs.getTimestamp("created_at"));
        enrollment.setUpdatedAt(rs.getTimestamp("updated_at"));
        return enrollment;
    }
} 