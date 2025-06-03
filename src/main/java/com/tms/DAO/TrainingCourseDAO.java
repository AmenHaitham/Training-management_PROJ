package com.tms.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Course;
import com.tms.Model.Training;
import com.tms.Model.TrainingCourse;
import com.tms.Model.TrainingCourse.Status;

public class TrainingCourseDAO implements AutoCloseable {
    private final Connection connection;

    public TrainingCourseDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    public static void main(String[] args) {
        List <TrainingCourse> trainingCourses = new ArrayList<>();

        try (TrainingCourseDAO trainingCourseDAO = new TrainingCourseDAO()) {
            trainingCourses = trainingCourseDAO.getAllTrainingCourses();
            System.out.println(trainingCourses);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // =================== CRUD Operations ===================
    public Optional<TrainingCourse> createTrainingCourse(TrainingCourse trainingCourse) throws SQLException {
        validateTrainingCourse(trainingCourse);
        
        String query = """
            INSERT INTO Training_Courses (
                training_id, course_id, total_sessions, current_sessions,
                completed_sessions, cancelled_sessions, start_date, end_date,
                status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, trainingCourse.getTraining().getId());
            statement.setInt(2, trainingCourse.getCourse().getId());
            statement.setInt(3, trainingCourse.getTotalSessions());
            statement.setInt(4, trainingCourse.getCurrentSessions());
            statement.setInt(5, trainingCourse.getCompletedSessions());
            statement.setInt(6, trainingCourse.getCancelledSessions());
            statement.setDate(7, Date.valueOf(trainingCourse.getStartDate()));
            statement.setDate(8, Date.valueOf(trainingCourse.getEndDate()));
            statement.setString(9, trainingCourse.getStatus().name());

            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                return Optional.empty();
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Optional.of(mapResultSetToTrainingCourse(generatedKeys));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<TrainingCourse> getTrainingCourseById(int id) throws SQLException {
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            WHERE tc.id = ?
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToTrainingCourse(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<TrainingCourse> getAllTrainingCourses() throws SQLException {
        List<TrainingCourse> trainingCourses = new ArrayList<>();
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            """;
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                trainingCourses.add(mapResultSetToTrainingCourse(resultSet));
            }
        }
        return trainingCourses;
    }

    public Optional<TrainingCourse> updateTrainingCourse(TrainingCourse trainingCourse) throws SQLException {
    validateTrainingCourse(trainingCourse);
    
    String query = """
        UPDATE Training_Courses SET 
            training_id = ?, 
            course_id = ?, 
            total_sessions = ?, 
            current_sessions = ?, 
            completed_sessions = ?, 
            cancelled_sessions = ?, 
            start_date = ?, 
            end_date = ?, 
            status = ?
        WHERE id = ?
        """;
    
    try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, trainingCourse.getTraining().getId());
        statement.setInt(2, trainingCourse.getCourse().getId());
        statement.setInt(3, trainingCourse.getTotalSessions());
        statement.setInt(4, trainingCourse.getCurrentSessions());
        statement.setInt(5, trainingCourse.getCompletedSessions());
        statement.setInt(6, trainingCourse.getCancelledSessions());
        statement.setDate(7, Date.valueOf(trainingCourse.getStartDate()));
        statement.setDate(8, Date.valueOf(trainingCourse.getEndDate()));
        statement.setString(9, trainingCourse.getStatus().name());
        statement.setInt(10, trainingCourse.getId());
        
        int rowsUpdated = statement.executeUpdate();
        
        if (rowsUpdated > 0) {
            return Optional.of(trainingCourse); // Return the updated object
        } else {
            return Optional.empty(); // No row matched the ID
        }
    }
}


    public boolean deleteTrainingCourse(int id) throws SQLException {
        String query = "DELETE FROM Training_Courses WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    // =================== Specialized Queries ===================

    public List<TrainingCourse> getTrainingCoursesByTrainingId(int trainingId) throws SQLException {
        List<TrainingCourse> trainingCourses = new ArrayList<>();
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            WHERE tc.training_id = ?
            ORDER BY tc.start_date
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, trainingId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trainingCourses.add(mapResultSetToTrainingCourse(resultSet));
                }
            }
        }
        return trainingCourses;
    }

    public List<TrainingCourse> getTrainingCoursesByCourseId(int courseId) throws SQLException {
        List<TrainingCourse> trainingCourses = new ArrayList<>();
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            WHERE tc.course_id = ?
            ORDER BY tc.start_date
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trainingCourses.add(mapResultSetToTrainingCourse(resultSet));
                }
            }
        }
        return trainingCourses;
    }

    public List<TrainingCourse> getActiveTrainingCourses() throws SQLException {
        List<TrainingCourse> trainingCourses = new ArrayList<>();
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            WHERE tc.status IN ('COMING', 'LIVE')
            ORDER BY tc.start_date
            """;
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                trainingCourses.add(mapResultSetToTrainingCourse(resultSet));
            }
        }
        return trainingCourses;
    }

    public List<TrainingCourse> getTrainingCoursesByStatus(Status status) throws SQLException {
        List<TrainingCourse> trainingCourses = new ArrayList<>();
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            WHERE tc.status = ?
            ORDER BY tc.start_date
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trainingCourses.add(mapResultSetToTrainingCourse(resultSet));
                }
            }
        }
        return trainingCourses;
    }

    public List<TrainingCourse> getTrainingCoursesBetweenDates(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<TrainingCourse> trainingCourses = new ArrayList<>();
        String query = """
            SELECT tc.*, t.title as training_title, c.title as course_title
            FROM Training_Courses tc
            JOIN Trainings t ON tc.training_id = t.id
            JOIN Courses c ON tc.course_id = c.id
            WHERE (tc.start_date, tc.end_date) OVERLAPS (?, ?)
            ORDER BY tc.start_date
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    trainingCourses.add(mapResultSetToTrainingCourse(resultSet));
                }
            }
        }
        return trainingCourses;
    }

    // =================== Helper Methods ===================

    private TrainingCourse mapResultSetToTrainingCourse(ResultSet resultSet) throws SQLException {
        TrainingCourse trainingCourse = new TrainingCourse();
        trainingCourse.setId(resultSet.getInt("id"));
        
        // Create minimal training object
        Training training = new Training();
        training.setId(resultSet.getInt("training_id"));
        training.setTitle(resultSet.getString("training_title"));
        trainingCourse.setTraining(training);
        
        // Create minimal course object
        Course course = new Course();
        course.setId(resultSet.getInt("course_id"));
        course.setTitle(resultSet.getString("course_title"));
        trainingCourse.setCourse(course);
        
        trainingCourse.setTotalSessions(resultSet.getInt("total_sessions"));
        trainingCourse.setCurrentSessions(resultSet.getInt("current_sessions"));
        trainingCourse.setCompletedSessions(resultSet.getInt("completed_sessions"));
        trainingCourse.setCancelledSessions(resultSet.getInt("cancelled_sessions"));
        trainingCourse.setStartDate(resultSet.getDate("start_date").toLocalDate());
        trainingCourse.setEndDate(resultSet.getDate("end_date").toLocalDate());
        trainingCourse.setStatus(Status.valueOf(resultSet.getString("status")));
        
        return trainingCourse;
    }

    private void validateTrainingCourse(TrainingCourse trainingCourse) {
        if (trainingCourse == null) {
            throw new IllegalArgumentException("TrainingCourse cannot be null");
        }
        if (trainingCourse.getTraining() == null || trainingCourse.getCourse() == null) {
            throw new IllegalArgumentException("Training and Course must be specified");
        }
        if (trainingCourse.getEndDate().isBefore(trainingCourse.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (trainingCourse.getTotalSessions() <= 0) {
            throw new IllegalArgumentException("Total sessions must be positive");
        }
        if (trainingCourse.getCompletedSessions() + trainingCourse.getCancelledSessions() > trainingCourse.getTotalSessions()) {
            throw new IllegalArgumentException("Sum of completed and cancelled sessions cannot exceed total sessions");
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                DatabaseConnection.releaseConnection(connection);
            } catch (Exception e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            }
        }
    }

    // =================== Business Logic Methods ===================

    public boolean updateSessionCounts(int trainingCourseId, int completed, int cancelled) throws SQLException {
        String query = """
            UPDATE Training_Courses SET 
                completed_sessions = ?,
                cancelled_sessions = ?,
                current_sessions = total_sessions - ? - ?,
                status = CASE 
                    WHEN total_sessions = ? + ? THEN 'COMPLETED'
                    WHEN total_sessions = ? THEN 'CANCELLED'
                    WHEN end_date < CURRENT_DATE THEN 'COMPLETED'
                    WHEN start_date > CURRENT_DATE THEN 'COMING'
                    ELSE 'LIVE'
                END
            WHERE id = ?
            AND ? BETWEEN 0 AND total_sessions
            AND ? BETWEEN 0 AND total_sessions
            AND ? + ? <= total_sessions
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, completed);
            statement.setInt(2, cancelled);
            statement.setInt(3, completed);
            statement.setInt(4, cancelled);
            statement.setInt(5, completed);
            statement.setInt(6, cancelled);
            statement.setInt(7, cancelled);
            statement.setInt(8, trainingCourseId);
            statement.setInt(9, completed);
            statement.setInt(10, cancelled);
            statement.setInt(11, completed);
            statement.setInt(12, cancelled);
            
            return statement.executeUpdate() > 0;
        }
    }

    public boolean isCourseScheduledInTraining(int trainingId, int courseId) throws SQLException {
        String query = """
            SELECT 1 FROM Training_Courses 
            WHERE training_id = ? AND course_id = ?
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, trainingId);
            statement.setInt(2, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean hasOverlappingSchedule(int trainingId, LocalDate startDate, LocalDate endDate, Integer excludeId) throws SQLException {
        String query = """
            SELECT 1 FROM Training_Courses 
            WHERE training_id = ? 
            AND (start_date, end_date) OVERLAPS (?, ?)
            AND (? IS NULL OR id != ?)
            """;
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, trainingId);
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));
            if (excludeId != null) {
                statement.setInt(4, excludeId);
                statement.setInt(5, excludeId);
            } else {
                statement.setNull(4, Types.INTEGER);
                statement.setNull(5, Types.INTEGER);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}