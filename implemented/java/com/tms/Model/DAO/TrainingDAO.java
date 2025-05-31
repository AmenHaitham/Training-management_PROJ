package com.tms.Model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Room;
import com.tms.Model.Training;
import com.tms.Model.Training.Status;

public class TrainingDAO {
    private final Connection con;

    public TrainingDAO() throws SQLException, ClassNotFoundException {
        this.con = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Integer> createTraining(Training training) throws SQLException {
        String query = """
            INSERT INTO Trainings (title, description, room_id, start_date, end_date, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, training.getTitle());
            stmt.setString(2, training.getDescription());
            stmt.setInt(3, training.getRoom().getId());
            stmt.setObject(4, training.getStartDate());
            stmt.setObject(5, training.getEndDate());
            stmt.setString(6, training.getStatus().name());
            stmt.setObject(7, training.getCreatedAt());
            stmt.setObject(8, training.getUpdatedAt());

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

    public boolean updateTraining(Training training) throws SQLException {
        String query = """
            UPDATE Trainings SET 
                title = ?, 
                description = ?, 
                room_id = ?, 
                start_date = ?, 
                end_date = ?, 
                status = ?, 
                updated_at = ?
            WHERE id = ?
            """;
            
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, training.getTitle());
            stmt.setString(2, training.getDescription());
            stmt.setInt(3, training.getRoom().getId());
            stmt.setObject(4, training.getStartDate());
            stmt.setObject(5, training.getEndDate());
            stmt.setString(6, training.getStatus().name());
            stmt.setObject(7, LocalDateTime.now());
            stmt.setInt(8, training.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public boolean updateTrainingStatus(int trainingId, Status newStatus) throws SQLException {
        String query = "UPDATE Trainings SET status = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newStatus.name());
            stmt.setObject(2, LocalDateTime.now());
            stmt.setInt(3, trainingId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public Optional<Training> getTrainingById(int id) throws SQLException {
        String query = """
            SELECT t.*, r.capacity, r.location, r.status as room_status 
            FROM Trainings t
            JOIN Rooms r ON t.room_id = r.id
            WHERE t.id = ?
            """;
            
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToTraining(rs));
            }
            return Optional.empty();
        }
    }

    public List<Training> getAllTrainings() throws SQLException {
        List<Training> trainings = new ArrayList<>();
        String query = """
            SELECT t.*, r.capacity, r.location, r.status as room_status 
            FROM Trainings t
            JOIN Rooms r ON t.room_id = r.id
            """;
            
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                trainings.add(mapResultSetToTraining(rs));
            }
        }
        return trainings;
    }

    public boolean deleteTraining(int trainingId) throws SQLException {
        String query = "DELETE FROM Trainings WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, trainingId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    // =================== Specialized Queries ===================

    public List<Training> getTrainingsByStatus(Status status) throws SQLException {
        List<Training> trainings = new ArrayList<>();
        String query = """
            SELECT t.*, r.capacity, r.location, r.status as room_status 
            FROM Trainings t
            JOIN Rooms r ON t.room_id = r.id
            WHERE t.status = ?
            """;
            
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                trainings.add(mapResultSetToTraining(rs));
            }
        }
        return trainings;
    }

    public List<Training> getUpcomingTrainings() throws SQLException {
        List<Training> trainings = new ArrayList<>();
        String query = """
            SELECT t.*, r.capacity, r.location, r.status as room_status 
            FROM Trainings t
            JOIN Rooms r ON t.room_id = r.id
            WHERE t.start_date > CURRENT_DATE AND t.status IN ('AVAILABLE', 'LIVE')
            ORDER BY t.start_date ASC
            """;
            
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                trainings.add(mapResultSetToTraining(rs));
            }
        }
        return trainings;
    }

    public List<Training> getTrainingsByRoom(int roomId) throws SQLException {
        List<Training> trainings = new ArrayList<>();
        String query = """
            SELECT t.*, r.capacity, r.location, r.status as room_status 
            FROM Trainings t
            JOIN Rooms r ON t.room_id = r.id
            WHERE t.room_id = ?
            ORDER BY t.start_date ASC
            """;
            
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                trainings.add(mapResultSetToTraining(rs));
            }
        }
        return trainings;
    }

    // =================== Helper Methods ===================

    private Training mapResultSetToTraining(ResultSet rs) throws SQLException {
    Training training = new Training();
    training.setId(rs.getInt("id"));
    training.setTitle(rs.getString("title"));
    training.setDescription(rs.getString("description")); // description can be NULL
    
    // Handle NULL dates
    training.setStartDate(rs.getDate("start_date").toLocalDate());
    training.setEndDate(rs.getDate("end_date").toLocalDate());
    
    training.setStatus(Status.valueOf(rs.getString("status")));
    training.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    training.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

    // Handle NULL room_id
    int roomId = rs.getInt("room_id");
    if (!rs.wasNull()) {
        Room room = new Room();
        room.setId(roomId);
        room.setCapacity(rs.getInt("capacity"));
        room.setLocation(rs.getString("location"));
        room.setStatus(Room.Status.valueOf(rs.getString("room_status")));
        training.setRoom(room);
    }

    return training;
}

    // =================== Business Logic Methods ===================

    public boolean isRoomAvailable(int roomId, LocalDate startDate, LocalDate endDate) throws SQLException {
    String query = """
        SELECT NOT EXISTS (
            SELECT 1 FROM Trainings 
            WHERE room_id = ? 
            AND status NOT IN ('CANCELED', 'COMPLETED')
            AND (start_date, end_date) OVERLAPS (?, ?)
        )
        """;
        
    try (PreparedStatement stmt = con.prepareStatement(query)) {
        stmt.setInt(1, roomId);
        stmt.setObject(2, startDate);
        stmt.setObject(3, endDate.plusDays(1)); // Make end date exclusive
        
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getBoolean(1);
    }
}

}