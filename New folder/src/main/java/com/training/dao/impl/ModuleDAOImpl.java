package com.training.dao.impl;

import com.training.dao.ModuleDAO;
import com.training.model.Module;
import com.training.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleDAOImpl implements ModuleDAO {
    
    @Override
    public Module create(Module module) {
        String sql = "INSERT INTO modules (title, description, order_number, course_id, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, module.getTitle());
            pstmt.setString(2, module.getDescription());
            pstmt.setInt(3, module.getOrderNumber());
            pstmt.setLong(4, module.getCourse().getId());
            pstmt.setTimestamp(5, new Timestamp(module.getCreatedAt().getTime()));
            pstmt.setTimestamp(6, new Timestamp(module.getUpdatedAt().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating module failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    module.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating module failed, no ID obtained.");
                }
            }
            
            return module;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Module findById(Long id) {
        String sql = "SELECT * FROM modules WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractModuleFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Module> findAll() {
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules ORDER BY order_number";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                modules.add(extractModuleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modules;
    }
    
    @Override
    public List<Module> findByCourse(Long courseId) {
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE course_id = ? ORDER BY order_number";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    modules.add(extractModuleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modules;
    }
    
    @Override
    public Module update(Module module) {
        String sql = "UPDATE modules SET title = ?, description = ?, order_number = ?, " +
                    "course_id = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, module.getTitle());
            pstmt.setString(2, module.getDescription());
            pstmt.setInt(3, module.getOrderNumber());
            pstmt.setLong(4, module.getCourse().getId());
            pstmt.setTimestamp(5, new Timestamp(module.getUpdatedAt().getTime()));
            pstmt.setLong(6, module.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating module failed, no rows affected.");
            }
            
            return module;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM modules WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void reorderModules(Long courseId, List<Long> moduleIds) {
        String sql = "UPDATE modules SET order_number = ?, updated_at = ? WHERE id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (int i = 0; i < moduleIds.size(); i++) {
                pstmt.setInt(1, i + 1);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setLong(3, moduleIds.get(i));
                pstmt.setLong(4, courseId);
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Module extractModuleFromResultSet(ResultSet rs) throws SQLException {
        Module module = new Module();
        module.setId(rs.getLong("id"));
        module.setTitle(rs.getString("title"));
        module.setDescription(rs.getString("description"));
        module.setOrderNumber(rs.getInt("order_number"));
        // Note: You'll need to fetch the course separately using CourseDAO
        module.setCreatedAt(rs.getTimestamp("created_at"));
        module.setUpdatedAt(rs.getTimestamp("updated_at"));
        return module;
    }
} 