package com.tms.Model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Material;

public class MaterialDAO implements AutoCloseable {
    private final Connection connection;

    public MaterialDAO() throws SQLException, ClassNotFoundException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Integer> createMaterial(Material material) throws SQLException  , ClassNotFoundException{
        validateMaterial(material);
        
        String sql = """
            INSERT INTO Materials (title, description, session_id, file_data)
            VALUES (?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getDescription());
            stmt.setInt(3, material.getSession().getId());
            stmt.setBytes(4, material.getFileData());

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

    public Optional<Material> getMaterialById(int id) throws SQLException , ClassNotFoundException {
        String sql = """
            SELECT m.*, s.session_date, s.start_time, s.end_time
            FROM Materials m
            LEFT JOIN Sessions s ON m.session_id = s.id
            WHERE m.id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMaterial(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<Material> getAllMaterials() throws SQLException , ClassNotFoundException{
        List<Material> materials = new ArrayList<>();
        String sql = """
            SELECT m.*, s.session_date, s.start_time, s.end_time
            FROM Materials m
            LEFT JOIN Sessions s ON m.session_id = s.id
            ORDER BY m.created_at DESC
            """;
            
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
        }
        return materials;
    }

    public List<Material> getMaterialsBySessionId(int sessionId) throws SQLException , ClassNotFoundException{
        List<Material> materials = new ArrayList<>();
        String sql = """
            SELECT m.*, s.session_date, s.start_time, s.end_time
            FROM Materials m
            LEFT JOIN Sessions s ON m.session_id = s.id
            WHERE m.session_id = ?
            ORDER BY m.created_at DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapResultSetToMaterial(rs));
                }
            }
        }
        return materials;
    }

    public boolean updateMaterial(Material material) throws SQLException {
        validateMaterial(material);
        
        String sql = """
            UPDATE Materials SET 
                title = ?, 
                description = ?, 
                session_id = ?, 
                file_data = ?, 
                updated_at = CURRENT_TIMESTAMP 
            WHERE id = ?
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getDescription());
            stmt.setInt(3, material.getSession().getId());
            stmt.setBytes(4, material.getFileData());
            stmt.setInt(5, material.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMaterial(int id) throws SQLException {
        String sql = "DELETE FROM Materials WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // =================== Helper Methods ===================

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException, ClassNotFoundException {
        Material material = new Material();
        material.setId(rs.getInt("id"));
        material.setTitle(rs.getString("title"));
        material.setDescription(rs.getString("description"));
        material.setSession(new SessionDAO().getSessionById(rs.getInt("session_id")).get());
        material.setFileData(rs.getBytes("file_data"));
        
        // Calculate file size from bytes
        byte[] fileData = material.getFileData();
        material.setFileSize(fileData != null ? fileData.length : 0);
        
        // Handle timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        material.setCreatedAt(createdAt != null ? createdAt : null);
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        material.setUpdatedAt(updatedAt != null ? updatedAt : null);
        
        return material;
    }

    private void validateMaterial(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        if (material.getTitle() == null || material.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Material title cannot be empty");
        }
        if (material.getSession().getId() <= 0) {
            throw new IllegalArgumentException("Invalid session ID");
        }
        if (material.getFileData() == null || material.getFileData().length == 0) {
            throw new IllegalArgumentException("File data cannot be empty");
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // =================== Business Logic Methods ===================

    public boolean materialExists(int materialId) throws SQLException {
        String sql = "SELECT 1 FROM Materials WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Material> searchMaterials(String searchTerm) throws SQLException, ClassNotFoundException {
        List<Material> materials = new ArrayList<>();
        String sql = """
            SELECT m.*, s.session_date, s.start_time, s.end_time
            FROM Materials m
            LEFT JOIN Sessions s ON m.session_id = s.id
            WHERE m.title ILIKE ? OR m.description ILIKE ?
            ORDER BY m.created_at DESC
            """;
            
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapResultSetToMaterial(rs));
                }
            }
        }
        return materials;
    }

    public Optional<byte[]> getMaterialFileData(int materialId) throws SQLException {
        String sql = "SELECT file_data FROM Materials WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getBytes("file_data"));
                }
                return Optional.empty();
            }
        }
    }
}