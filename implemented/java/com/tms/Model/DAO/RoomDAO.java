package com.tms.Model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Room;
import com.tms.Model.Room.Status;

public class RoomDAO {
    private final Connection con;

    public RoomDAO() throws SQLException, ClassNotFoundException {
        this.con = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Integer> createRoom(Room room) throws SQLException {
        String query = """
            INSERT INTO Rooms (capacity, location, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, room.getCapacity());
            stmt.setString(2, room.getLocation());
            stmt.setString(3, room.getStatus().name());
            stmt.setObject(4, room.getCreatedAt());
            stmt.setObject(5, room.getUpdatedAt());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return Optional.of(generatedKeys.getInt(1));
                    }
                }
            }
            return Optional.empty();
        }
    }

    public boolean updateRoom(Room room) throws SQLException {
        String query = """
            UPDATE Rooms SET 
                capacity = ?, 
                location = ?, 
                status = ?, 
                updated_at = ?
            WHERE id = ?
            """;
            
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, room.getCapacity());
            stmt.setString(2, room.getLocation());
            stmt.setString(3, room.getStatus().name());
            stmt.setObject(4, LocalDateTime.now());
            stmt.setInt(5, room.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public boolean updateRoomStatus(int roomId, Status newStatus) throws SQLException {
        String query = "UPDATE Rooms SET status = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newStatus.name());
            stmt.setObject(2, LocalDateTime.now());
            stmt.setInt(3, roomId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public Optional<Room> getRoomById(int id) throws SQLException {
        String query = "SELECT * FROM Rooms WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoom(rs));
                }
                return Optional.empty();
            }
        }
    }

    public boolean roomExists(int roomId) throws SQLException {
        String query = "SELECT 1 FROM Rooms WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Rooms";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }

    public boolean deleteRoom(int roomId) throws SQLException {
        String query = "DELETE FROM Rooms WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    // =================== Specialized Queries ===================

    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE status = 'AVAILABLE'";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }

    public List<Room> getRoomsByCapacity(int minCapacity) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE capacity >= ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, minCapacity);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        }
        return rooms;
    }

    public List<Room> getAvailableRoomsWithCapacity(int minCapacity) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE status = 'AVAILABLE' AND capacity >= ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, minCapacity);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        }
        return rooms;
    }

    // =================== Helper Methods ===================

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setCapacity(rs.getInt("capacity"));
        room.setLocation(rs.getString("location"));
        room.setStatus(Status.valueOf(rs.getString("status")));
        
        // Handle possible NULL timestamps
        Timestamp createdAt = rs.getTimestamp("created_at");
        room.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        room.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        
        return room;
    }

    // =================== Business Logic Methods ===================

    public boolean isRoomAvailable(int roomId) throws SQLException {
        String query = "SELECT 1 FROM Rooms WHERE id = ? AND status = 'AVAILABLE'";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean canRoomAccommodate(int roomId, int numberOfPeople) throws SQLException {
        String query = "SELECT 1 FROM Rooms WHERE id = ? AND status = 'AVAILABLE' AND capacity >= ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, numberOfPeople);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =================== Resource Management ===================
    
    public void close() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}