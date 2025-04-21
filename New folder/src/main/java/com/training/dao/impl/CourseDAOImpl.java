package com.training.dao.impl;

import com.training.dao.CourseDAO;
import com.training.model.Course;
import com.training.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {
    
    @Override
    public Course create(Course course) {
        String sql = "INSERT INTO courses (title, description, category, duration, price, trainer_id, " +
                    "start_date, end_date, status, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setString(3, course.getCategory());
            pstmt.setInt(4, course.getDuration());
            pstmt.setDouble(5, course.getPrice());
            pstmt.setLong(6, course.getTrainer().getId());
            pstmt.setTimestamp(7, new Timestamp(course.getStartDate().getTime()));
            pstmt.setTimestamp(8, new Timestamp(course.getEndDate().getTime()));
            pstmt.setString(9, course.getStatus());
            pstmt.setTimestamp(10, new Timestamp(course.getCreatedAt().getTime()));
            pstmt.setTimestamp(11, new Timestamp(course.getUpdatedAt().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating course failed, no ID obtained.");
                }
            }
            
            return course;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Course findById(Long id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCourseFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    @Override
    public List<Course> findByTrainer(Long trainerId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE trainer_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, trainerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    @Override
    public List<Course> findByCategory(String category) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE category = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    @Override
    public List<Course> findByStatus(String status) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE status = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    @Override
    public Course update(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, category = ?, duration = ?, " +
                    "price = ?, trainer_id = ?, start_date = ?, end_date = ?, status = ?, " +
                    "updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setString(3, course.getCategory());
            pstmt.setInt(4, course.getDuration());
            pstmt.setDouble(5, course.getPrice());
            pstmt.setLong(6, course.getTrainer().getId());
            pstmt.setTimestamp(7, new Timestamp(course.getStartDate().getTime()));
            pstmt.setTimestamp(8, new Timestamp(course.getEndDate().getTime()));
            pstmt.setString(9, course.getStatus());
            pstmt.setTimestamp(10, new Timestamp(course.getUpdatedAt().getTime()));
            pstmt.setLong(11, course.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating course failed, no rows affected.");
            }
            
            return course;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Course> search(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE title ILIKE ? OR description ILIKE ? OR category ILIKE ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setCategory(rs.getString("category"));
        course.setDuration(rs.getInt("duration"));
        course.setPrice(rs.getDouble("price"));
        // Note: You'll need to fetch the trainer separately using UserDAO
        course.setStartDate(rs.getTimestamp("start_date"));
        course.setEndDate(rs.getTimestamp("end_date"));
        course.setStatus(rs.getString("status"));
        course.setCreatedAt(rs.getTimestamp("created_at"));
        course.setUpdatedAt(rs.getTimestamp("updated_at"));
        return course;
    }
} 