package com.example.dao.impl;

import com.example.dao.CourseDao;
import com.example.model.Course;
import com.example.util.DbConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CourseDao interface for database operations
 */
public class CourseDAOImpl implements CourseDao {
    private static final Logger logger = LoggerFactory.getLogger(CourseDAOImpl.class);
    
    @Override
    public Optional<Course> findById(Long id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding course by ID: " + id, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Course save(Course course) {
        String sql = "INSERT INTO courses (title, description, training_program_id, status, " +
                     "duration_hours, assigned_trainer_id, order_num, start_date, end_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setLong(3, course.getTrainingProgramId());
            stmt.setString(4, course.getStatus());
            stmt.setInt(5, course.getDurationHours());
            
            if (course.getAssignedTrainerId() != null) {
                stmt.setLong(6, course.getAssignedTrainerId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setInt(7, course.getOrderNum());
            stmt.setDate(8, course.getStartDate());
            stmt.setDate(9, course.getEndDate());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    course.setId(id);
                    return course;
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving course: " + course.getTitle(), e);
        }
        
        return null;
    }
    
    @Override
    public Course update(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, training_program_id = ?, " +
                     "status = ?, duration_hours = ?, assigned_trainer_id = ?, order_num = ?, " +
                     "start_date = ?, end_date = ? WHERE id = ?";
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setLong(3, course.getTrainingProgramId());
            stmt.setString(4, course.getStatus());
            stmt.setInt(5, course.getDurationHours());
            
            if (course.getAssignedTrainerId() != null) {
                stmt.setLong(6, course.getAssignedTrainerId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setInt(7, course.getOrderNum());
            stmt.setDate(8, course.getStartDate());
            stmt.setDate(9, course.getEndDate());
            stmt.setLong(10, course.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return course;
            }
        } catch (SQLException e) {
            logger.error("Error updating course with ID: " + course.getId(), e);
        }
        
        return null;
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting course with ID: " + id, e);
            return false;
        }
    }
    
    @Override
    public List<Course> findAll() {
        String sql = "SELECT * FROM courses ORDER BY order_num";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all courses", e);
        }
        
        return courses;
    }
    
    @Override
    public List<Course> findByProgramId(Long programId) {
        String sql = "SELECT * FROM courses WHERE training_program_id = ? ORDER BY order_num";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, programId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding courses by program ID: " + programId, e);
        }
        
        return courses;
    }
    
    @Override
    public List<Course> findByTrainerId(Long trainerId) {
        String sql = "SELECT * FROM courses WHERE assigned_trainer_id = ? ORDER BY order_num";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, trainerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding courses by trainer ID: " + trainerId, e);
        }
        
        return courses;
    }
    
    @Override
    public List<Course> findByStatus(String status) {
        String sql = "SELECT * FROM courses WHERE status = ? ORDER BY order_num";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding courses by status: " + status, e);
        }
        
        return courses;
    }
    
    @Override
    public int countByProgramId(Long programId) {
        String sql = "SELECT COUNT(*) FROM courses WHERE training_program_id = ?";
        
        try (Connection conn = DbConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, programId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting courses by program ID: " + programId, e);
        }
        
        return 0;
    }
    
    /**
     * Map a ResultSet to a Course object
     *
     * @param rs the ResultSet
     * @return a Course object
     * @throws SQLException if a database access error occurs
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setTrainingProgramId(rs.getLong("training_program_id"));
        course.setStatus(rs.getString("status"));
        course.setDurationHours(rs.getInt("duration_hours"));
        
        Long trainerId = rs.getLong("assigned_trainer_id");
        if (!rs.wasNull()) {
            course.setAssignedTrainerId(trainerId);
        }
        
        course.setOrderNum(rs.getInt("order_num"));
        course.setStartDate(rs.getDate("start_date"));
        course.setEndDate(rs.getDate("end_date"));
        
        return course;
    }
} 