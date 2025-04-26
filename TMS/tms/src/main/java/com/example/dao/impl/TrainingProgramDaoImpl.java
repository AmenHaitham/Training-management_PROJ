package com.example.dao.impl;

import com.example.dao.TrainingProgramDao;
import com.example.dao.UserDao;
import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TrainingProgramDao
 */
public class TrainingProgramDaoImpl implements TrainingProgramDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainingProgramDaoImpl.class);
    private final UserDao userDao;

    public TrainingProgramDaoImpl() {
        this.userDao = new UserDaoImpl();
    }

    public TrainingProgramDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    private static final String SELECT_BY_ID = 
            "SELECT id, title, description, status, created_by, start_date, end_date, created_at, updated_at " +
            "FROM training_programs WHERE id = ?";
    private static final String SELECT_ALL = 
            "SELECT id, title, description, status, created_by, start_date, end_date, created_at, updated_at " +
            "FROM training_programs";
    private static final String SELECT_BY_TITLE = 
            "SELECT id, title, description, status, created_by, start_date, end_date, created_at, updated_at " +
            "FROM training_programs WHERE LOWER(title) LIKE LOWER(?)";
    private static final String SELECT_BY_STATUS = 
            "SELECT id, title, description, status, created_by, start_date, end_date, created_at, updated_at " +
            "FROM training_programs WHERE status = ?";
    private static final String SELECT_BY_CREATED_BY = 
            "SELECT id, title, description, status, created_by, start_date, end_date, created_at, updated_at " +
            "FROM training_programs WHERE created_by = ?";
    private static final String SELECT_ACTIVE_PROGRAMS = 
            "SELECT id, title, description, status, created_by, start_date, end_date, created_at, updated_at " +
            "FROM training_programs WHERE status = 'ACTIVE' AND (end_date IS NULL OR end_date >= CURRENT_DATE)";
    private static final String INSERT = 
            "INSERT INTO training_programs (title, description, status, created_by, start_date, end_date, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String UPDATE = 
            "UPDATE training_programs SET title = ?, description = ?, status = ?, " +
            "created_by = ?, start_date = ?, end_date = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE = 
            "DELETE FROM training_programs WHERE id = ?";
    private static final String COUNT = 
            "SELECT COUNT(*) FROM training_programs";
    private static final String EXISTS_BY_ID = 
            "SELECT EXISTS(SELECT 1 FROM training_programs WHERE id = ?)";

    @Override
    public Optional<TrainingProgram> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TrainingProgram program = mapResultSetToTrainingProgram(rs);
                    
                    // Load creator if present
                    if (program.getCreatedBy() != null) {
                        userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
                    }
                    
                    conn.commit();
                    return Optional.of(program);
                }
            }
            conn.commit();
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding training program by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<TrainingProgram> findAll() {
        List<TrainingProgram> programs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                programs.add(mapResultSetToTrainingProgram(rs));
            }
            
            // Load creators for all programs
            for (TrainingProgram program : programs) {
                if (program.getCreatedBy() != null) {
                    userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding all training programs", e);
        }
        
        return programs;
    }

    @Override
    public List<TrainingProgram> findByTitle(String title) {
        List<TrainingProgram> programs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TITLE)) {
            
            stmt.setString(1, "%" + title + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    programs.add(mapResultSetToTrainingProgram(rs));
                }
            }
            
            // Load creators
            for (TrainingProgram program : programs) {
                if (program.getCreatedBy() != null) {
                    userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding training programs by title: " + title, e);
        }
        
        return programs;
    }

    @Override
    public List<TrainingProgram> findByStatus(String status) {
        List<TrainingProgram> programs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STATUS)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    programs.add(mapResultSetToTrainingProgram(rs));
                }
            }
            
            // Load creators
            for (TrainingProgram program : programs) {
                if (program.getCreatedBy() != null) {
                    userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding training programs by status: " + status, e);
        }
        
        return programs;
    }

    @Override
    public List<TrainingProgram> findByCreatedBy(Long userId) {
        List<TrainingProgram> programs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CREATED_BY)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    programs.add(mapResultSetToTrainingProgram(rs));
                }
            }
            
            // Load creator once since it's the same for all
            Optional<User> creator = userDao.findById(userId);
            for (TrainingProgram program : programs) {
                creator.ifPresent(program::setCreator);
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding training programs by created by: " + userId, e);
        }
        
        return programs;
    }

    @Override
    public List<TrainingProgram> findActivePrograms() {
        List<TrainingProgram> programs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVE_PROGRAMS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                programs.add(mapResultSetToTrainingProgram(rs));
            }
            
            // Load creators
            for (TrainingProgram program : programs) {
                if (program.getCreatedBy() != null) {
                    userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding active training programs", e);
        }
        
        return programs;
    }

    @Override
    public TrainingProgram save(TrainingProgram program) {
        if (program.getId() == null) {
            return insert(program);
        } else {
            return update(program);
        }
    }

    private TrainingProgram insert(TrainingProgram program) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, program.getTitle());
            
            if (program.getDescription() != null) {
                stmt.setString(2, program.getDescription());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            
            if (program.getStatus() != null) {
                stmt.setString(3, program.getStatus());
            } else {
                stmt.setString(3, "DRAFT"); // Default status
            }
            
            if (program.getCreatedBy() != null) {
                stmt.setLong(4, program.getCreatedBy());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }
            
            if (program.getStartDate() != null) {
                stmt.setDate(5, Date.valueOf(program.getStartDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            if (program.getEndDate() != null) {
                stmt.setDate(6, Date.valueOf(program.getEndDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    program.setId(rs.getLong(1));
                }
            }
            
            program.setCreatedAt(now);
            program.setUpdatedAt(now);
            
            conn.commit();
            
            // Load creator
            if (program.getCreatedBy() != null) {
                userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
            }
            
        } catch (SQLException e) {
            logger.error("Error inserting training program: " + program.getTitle(), e);
        }
        
        return program;
    }

    private TrainingProgram update(TrainingProgram program) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, program.getTitle());
            
            if (program.getDescription() != null) {
                stmt.setString(2, program.getDescription());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            
            if (program.getStatus() != null) {
                stmt.setString(3, program.getStatus());
            } else {
                stmt.setString(3, "DRAFT"); // Default status
            }
            
            if (program.getCreatedBy() != null) {
                stmt.setLong(4, program.getCreatedBy());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }
            
            if (program.getStartDate() != null) {
                stmt.setDate(5, Date.valueOf(program.getStartDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            if (program.getEndDate() != null) {
                stmt.setDate(6, Date.valueOf(program.getEndDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setLong(8, program.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                program.setUpdatedAt(now);
            }
            
            conn.commit();
            
            // Load creator
            if (program.getCreatedBy() != null) {
                userDao.findById(program.getCreatedBy()).ifPresent(program::setCreator);
            }
            
        } catch (SQLException e) {
            logger.error("Error updating training program: " + program.getId(), e);
        }
        
        return program;
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting training program by ID: " + id, e);
            return false;
        }
    }

    @Override
    public boolean existsById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getBoolean(1);
                    conn.commit();
                    return exists;
                }
            }
            conn.commit();
            return false;
        } catch (SQLException e) {
            logger.error("Error checking if training program exists by ID: " + id, e);
            return false;
        }
    }

    @Override
    public long count() {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                long count = rs.getLong(1);
                conn.commit();
                return count;
            }
            conn.commit();
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting training programs", e);
            return 0;
        }
    }

    /**
     * Maps a ResultSet row to a TrainingProgram object
     * @param rs The ResultSet positioned at the current row
     * @return The mapped TrainingProgram object
     * @throws SQLException if a database access error occurs
     */
    private TrainingProgram mapResultSetToTrainingProgram(ResultSet rs) throws SQLException {
        TrainingProgram program = new TrainingProgram();
        
        program.setId(rs.getLong("id"));
        program.setTitle(rs.getString("title"));
        program.setDescription(rs.getString("description"));
        program.setStatus(rs.getString("status"));
        
        Long createdBy = rs.getLong("created_by");
        if (!rs.wasNull()) {
            program.setCreatedBy(createdBy);
        }
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            program.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            program.setEndDate(endDate.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            program.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            program.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return program;
    }
} 