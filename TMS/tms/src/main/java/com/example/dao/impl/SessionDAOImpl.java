package com.example.dao.impl;

import com.example.dao.SessionDao;
import com.example.model.Session;
import com.example.util.DbConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of SessionDao interface for database operations related to sessions
 */
public class SessionDAOImpl implements SessionDao {
    private static final Logger logger = Logger.getLogger(SessionDAOImpl.class.getName());
    private final Connection connection;

    public SessionDAOImpl() {
        Connection conn = null;
        try {
            conn = DbConnectionUtil.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error establishing database connection", e);
        }
        this.connection = conn;
    }

    @Override
    public Session findById(Long id) {
        String query = "SELECT * FROM sessions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToSession(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding session by ID: " + id, e);
        }
        return null;
    }

    @Override
    public Session save(Session session) {
        String query = "INSERT INTO sessions (course_id, title, description, session_date, start_time, end_time, " +
                "trainer_id, location, materials, session_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setSessionParameters(statement, session);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        session.setId(generatedKeys.getLong(1));
                        return session;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving session: " + session, e);
        }
        return null;
    }

    @Override
    public Session update(Session session) {
        String query = "UPDATE sessions SET course_id = ?, title = ?, description = ?, session_date = ?, " +
                "start_time = ?, end_time = ?, trainer_id = ?, location = ?, materials = ?, session_status = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            setSessionParameters(statement, session);
            statement.setLong(11, session.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return session;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating session: " + session, e);
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String query = "DELETE FROM sessions WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting session with ID: " + id, e);
        }
        return false;
    }

    @Override
    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                sessions.add(mapResultSetToSession(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all sessions", e);
        }
        return sessions;
    }

    @Override
    public List<Session> findByStatus(String status) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions WHERE session_status = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessions.add(mapResultSetToSession(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding sessions by status: " + status, e);
        }
        return sessions;
    }

    @Override
    public List<Session> findByCourseId(Long courseId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions WHERE course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessions.add(mapResultSetToSession(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding sessions by course ID: " + courseId, e);
        }
        return sessions;
    }

    @Override
    public List<Session> findByTrainerId(Long trainerId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions WHERE trainer_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, trainerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessions.add(mapResultSetToSession(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding sessions by trainer ID: " + trainerId, e);
        }
        return sessions;
    }

    @Override
    public List<Session> findByUserIdAndDateAfter(Long userId, Date date) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT s.* FROM sessions s " +
                "JOIN enrollments e ON s.course_id = e.course_id " +
                "WHERE e.user_id = ? AND s.session_date >= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setDate(2, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessions.add(mapResultSetToSession(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding sessions by user ID and date after: " + userId, e);
        }
        return sessions;
    }

    @Override
    public List<Session> findByUserIdAndDateBefore(Long userId, Date date) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT s.* FROM sessions s " +
                "JOIN enrollments e ON s.course_id = e.course_id " +
                "WHERE e.user_id = ? AND s.session_date <= ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, userId);
            statement.setDate(2, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sessions.add(mapResultSetToSession(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding sessions by user ID and date before: " + userId, e);
        }
        return sessions;
    }

    @Override
    public int countByCourseId(Long courseId) {
        String query = "SELECT COUNT(*) FROM sessions WHERE course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting sessions by course ID: " + courseId, e);
        }
        return 0;
    }

    private void setSessionParameters(PreparedStatement statement, Session session) throws SQLException {
        statement.setLong(1, session.getCourseId());
        statement.setString(2, session.getTitle());
        statement.setString(3, session.getDescription());
        statement.setDate(4, session.getSessionDate());
        statement.setTime(5, session.getStartTime());
        statement.setTime(6, session.getEndTime());
        statement.setLong(7, session.getTrainerId());
        statement.setString(8, session.getLocation());
        statement.setString(9, session.getMaterials());
        statement.setString(10, session.getSessionStatus());
    }

    private Session mapResultSetToSession(ResultSet resultSet) throws SQLException {
        Session session = new Session();
        session.setId(resultSet.getLong("id"));
        session.setCourseId(resultSet.getLong("course_id"));
        session.setTitle(resultSet.getString("title"));
        session.setDescription(resultSet.getString("description"));
        session.setSessionDate(resultSet.getDate("session_date"));
        session.setStartTime(resultSet.getTime("start_time"));
        session.setEndTime(resultSet.getTime("end_time"));
        session.setTrainerId(resultSet.getLong("trainer_id"));
        session.setLocation(resultSet.getString("location"));
        session.setMaterials(resultSet.getString("materials"));
        session.setSessionStatus(resultSet.getString("session_status"));
        return session;
    }
} 