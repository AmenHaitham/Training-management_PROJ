package com.example.dao.impl;

import com.example.dao.EnrollmentDao;
import com.example.dao.TrainingProgramDao;
import com.example.dao.UserDao;
import com.example.model.Enrollment;
import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of EnrollmentDao
 */
public class EnrollmentDaoImpl implements EnrollmentDao {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentDaoImpl.class);
    private final UserDao userDao;
    private final TrainingProgramDao trainingProgramDao;

    public EnrollmentDaoImpl() {
        this.userDao = new UserDaoImpl();
        this.trainingProgramDao = new TrainingProgramDaoImpl();
    }

    public EnrollmentDaoImpl(UserDao userDao, TrainingProgramDao trainingProgramDao) {
        this.userDao = userDao;
        this.trainingProgramDao = trainingProgramDao;
    }

    private static final String SELECT_BY_ID = 
            "SELECT id, trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at " +
            "FROM enrollments WHERE id = ?";
    private static final String SELECT_ALL = 
            "SELECT id, trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at " +
            "FROM enrollments";
    private static final String SELECT_BY_TRAINEE = 
            "SELECT id, trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at " +
            "FROM enrollments WHERE trainee_id = ?";
    private static final String SELECT_BY_TRAINING_PROGRAM = 
            "SELECT id, trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at " +
            "FROM enrollments WHERE training_program_id = ?";
    private static final String SELECT_BY_STATUS = 
            "SELECT id, trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at " +
            "FROM enrollments WHERE status = ?";
    private static final String SELECT_BY_TRAINEE_AND_PROGRAM = 
            "SELECT id, trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at " +
            "FROM enrollments WHERE trainee_id = ? AND training_program_id = ?";
    private static final String INSERT = 
            "INSERT INTO enrollments (trainee_id, training_program_id, status, enrolled_at, completed_at, notes, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String UPDATE = 
            "UPDATE enrollments SET trainee_id = ?, training_program_id = ?, status = ?, " +
            "enrolled_at = ?, completed_at = ?, notes = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE = 
            "DELETE FROM enrollments WHERE id = ?";
    private static final String COUNT = 
            "SELECT COUNT(*) FROM enrollments";
    private static final String EXISTS_BY_ID = 
            "SELECT EXISTS(SELECT 1 FROM enrollments WHERE id = ?)";
    private static final String EXISTS_ENROLLMENT = 
            "SELECT EXISTS(SELECT 1 FROM enrollments WHERE trainee_id = ? AND training_program_id = ?)";

    @Override
    public Optional<Enrollment> findById(Long id) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enrollment enrollment = mapResultSetToEnrollment(rs);
                    
                    // Load relationships
                    loadRelationships(enrollment);
                    
                    conn.commit();
                    return Optional.of(enrollment);
                }
            }
            conn.commit();
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding enrollment by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
            
            // Load relationships for all enrollments
            for (Enrollment enrollment : enrollments) {
                loadRelationships(enrollment);
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding all enrollments", e);
        }
        
        return enrollments;
    }

    @Override
    public List<Enrollment> findByTrainee(Long traineeId) {
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TRAINEE)) {
            
            stmt.setLong(1, traineeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
            
            // Load trainee once for all enrollments
            Optional<User> trainee = userDao.findById(traineeId);
            
            // Load training programs for each enrollment
            for (Enrollment enrollment : enrollments) {
                trainee.ifPresent(enrollment::setTrainee);
                
                if (enrollment.getTrainingProgramId() != null) {
                    trainingProgramDao.findById(enrollment.getTrainingProgramId())
                            .ifPresent(enrollment::setTrainingProgram);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding enrollments by trainee: " + traineeId, e);
        }
        
        return enrollments;
    }

    @Override
    public List<Enrollment> findByTrainingProgram(Long trainingProgramId) {
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TRAINING_PROGRAM)) {
            
            stmt.setLong(1, trainingProgramId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
            
            // Load training program once for all enrollments
            Optional<TrainingProgram> program = trainingProgramDao.findById(trainingProgramId);
            
            // Load trainees for each enrollment
            for (Enrollment enrollment : enrollments) {
                program.ifPresent(enrollment::setTrainingProgram);
                
                if (enrollment.getTraineeId() != null) {
                    userDao.findById(enrollment.getTraineeId())
                            .ifPresent(enrollment::setTrainee);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding enrollments by training program: " + trainingProgramId, e);
        }
        
        return enrollments;
    }

    @Override
    public List<Enrollment> findByStatus(String status) {
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STATUS)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
            
            // Load relationships for all enrollments
            for (Enrollment enrollment : enrollments) {
                loadRelationships(enrollment);
            }
            
            conn.commit();
        } catch (SQLException e) {
            logger.error("Error finding enrollments by status: " + status, e);
        }
        
        return enrollments;
    }

    @Override
    public Optional<Enrollment> findByTraineeAndTrainingProgram(Long traineeId, Long trainingProgramId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TRAINEE_AND_PROGRAM)) {
            
            stmt.setLong(1, traineeId);
            stmt.setLong(2, trainingProgramId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enrollment enrollment = mapResultSetToEnrollment(rs);
                    
                    // Load relationships
                    loadRelationships(enrollment);
                    
                    conn.commit();
                    return Optional.of(enrollment);
                }
            }
            conn.commit();
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding enrollment by trainee and program: " + traineeId + ", " + trainingProgramId, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isEnrolled(Long traineeId, Long trainingProgramId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_ENROLLMENT)) {
            
            stmt.setLong(1, traineeId);
            stmt.setLong(2, trainingProgramId);
            
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
            logger.error("Error checking if trainee is enrolled: " + traineeId + ", " + trainingProgramId, e);
            return false;
        }
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            return insert(enrollment);
        } else {
            return update(enrollment);
        }
    }

    private Enrollment insert(Enrollment enrollment) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setLong(1, enrollment.getTraineeId());
            stmt.setLong(2, enrollment.getTrainingProgramId());
            stmt.setString(3, enrollment.getStatus());
            
            if (enrollment.getEnrolledAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(enrollment.getEnrolledAt()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(now));
            }
            
            if (enrollment.getCompletedAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(enrollment.getCompletedAt()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            if (enrollment.getNotes() != null) {
                stmt.setString(6, enrollment.getNotes());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    enrollment.setId(rs.getLong(1));
                }
            }
            
            enrollment.setCreatedAt(now);
            enrollment.setUpdatedAt(now);
            
            conn.commit();
            
            // Load relationships
            loadRelationships(enrollment);
            
        } catch (SQLException e) {
            logger.error("Error inserting enrollment: " + enrollment.getTraineeId() + ", " + enrollment.getTrainingProgramId(), e);
        }
        
        return enrollment;
    }

    private Enrollment update(Enrollment enrollment) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setLong(1, enrollment.getTraineeId());
            stmt.setLong(2, enrollment.getTrainingProgramId());
            stmt.setString(3, enrollment.getStatus());
            
            if (enrollment.getEnrolledAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(enrollment.getEnrolledAt()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            if (enrollment.getCompletedAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(enrollment.getCompletedAt()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            if (enrollment.getNotes() != null) {
                stmt.setString(6, enrollment.getNotes());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }
            
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setLong(8, enrollment.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                enrollment.setUpdatedAt(now);
            }
            
            conn.commit();
            
            // Load relationships
            loadRelationships(enrollment);
            
        } catch (SQLException e) {
            logger.error("Error updating enrollment: " + enrollment.getId(), e);
        }
        
        return enrollment;
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
            logger.error("Error deleting enrollment by ID: " + id, e);
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
            logger.error("Error checking if enrollment exists by ID: " + id, e);
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
            logger.error("Error counting enrollments", e);
            return 0;
        }
    }

    /**
     * Maps a ResultSet row to an Enrollment object
     * @param rs The ResultSet positioned at the current row
     * @return The mapped Enrollment object
     * @throws SQLException if a database access error occurs
     */
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        
        enrollment.setId(rs.getLong("id"));
        enrollment.setTraineeId(rs.getLong("trainee_id"));
        enrollment.setTrainingProgramId(rs.getLong("training_program_id"));
        enrollment.setStatus(rs.getString("status"));
        
        Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
        if (enrolledAt != null) {
            enrollment.setEnrolledAt(enrolledAt.toLocalDateTime());
        }
        
        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            enrollment.setCompletedAt(completedAt.toLocalDateTime());
        }
        
        enrollment.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            enrollment.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            enrollment.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return enrollment;
    }
    
    /**
     * Loads relationships for an enrollment
     * @param enrollment The enrollment to load relationships for
     */
    private void loadRelationships(Enrollment enrollment) {
        // Load trainee
        if (enrollment.getTraineeId() != null) {
            userDao.findById(enrollment.getTraineeId())
                    .ifPresent(enrollment::setTrainee);
        }
        
        // Load training program
        if (enrollment.getTrainingProgramId() != null) {
            trainingProgramDao.findById(enrollment.getTrainingProgramId())
                    .ifPresent(enrollment::setTrainingProgram);
        }
    }
} 