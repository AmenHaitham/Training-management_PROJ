package com.training.dao.impl;

import com.training.dao.LessonDAO;
import com.training.model.Lesson;
import com.training.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LessonDAOImpl implements LessonDAO {
    
    @Override
    public Lesson create(Lesson lesson) {
        String sql = "INSERT INTO lessons (title, content, video_url, duration, module_id, order_number, " +
                    "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, lesson.getTitle());
            pstmt.setString(2, lesson.getContent());
            pstmt.setString(3, lesson.getVideoUrl());
            pstmt.setInt(4, lesson.getDuration());
            pstmt.setLong(5, lesson.getModule().getId());
            pstmt.setInt(6, lesson.getOrderNumber());
            pstmt.setTimestamp(7, new Timestamp(lesson.getCreatedAt().getTime()));
            pstmt.setTimestamp(8, new Timestamp(lesson.getUpdatedAt().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating lesson failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lesson.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating lesson failed, no ID obtained.");
                }
            }
            
            return lesson;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Lesson findById(Long id) {
        String sql = "SELECT * FROM lessons WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractLessonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Lesson> findAll() {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lessons ORDER BY order_number";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lessons.add(extractLessonFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }
    
    @Override
    public List<Lesson> findByModule(Long moduleId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lessons WHERE module_id = ? ORDER BY order_number";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, moduleId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lessons.add(extractLessonFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }
    
    @Override
    public Lesson update(Lesson lesson) {
        String sql = "UPDATE lessons SET title = ?, content = ?, video_url = ?, duration = ?, " +
                    "module_id = ?, order_number = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lesson.getTitle());
            pstmt.setString(2, lesson.getContent());
            pstmt.setString(3, lesson.getVideoUrl());
            pstmt.setInt(4, lesson.getDuration());
            pstmt.setLong(5, lesson.getModule().getId());
            pstmt.setInt(6, lesson.getOrderNumber());
            pstmt.setTimestamp(7, new Timestamp(lesson.getUpdatedAt().getTime()));
            pstmt.setLong(8, lesson.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating lesson failed, no rows affected.");
            }
            
            return lesson;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM lessons WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void reorderLessons(Long moduleId, List<Long> lessonIds) {
        String sql = "UPDATE lessons SET order_number = ?, updated_at = ? WHERE id = ? AND module_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (int i = 0; i < lessonIds.size(); i++) {
                pstmt.setInt(1, i + 1);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setLong(3, lessonIds.get(i));
                pstmt.setLong(4, moduleId);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Lesson extractLessonFromResultSet(ResultSet rs) throws SQLException {
        Lesson lesson = new Lesson();
        lesson.setId(rs.getLong("id"));
        lesson.setTitle(rs.getString("title"));
        lesson.setContent(rs.getString("content"));
        lesson.setVideoUrl(rs.getString("video_url"));
        lesson.setDuration(rs.getInt("duration"));
        // Note: You'll need to fetch the module separately using ModuleDAO
        lesson.setOrderNumber(rs.getInt("order_number"));
        lesson.setCreatedAt(rs.getTimestamp("created_at"));
        lesson.setUpdatedAt(rs.getTimestamp("updated_at"));
        return lesson;
    }
} 