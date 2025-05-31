package com.tms.Model.DAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Course;
import com.tms.Model.User;

public class CourseDAO implements AutoCloseable {
    private final Connection connection;

    public CourseDAO() throws SQLException, ClassNotFoundException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Integer> createCourse(Course course) throws SQLException {
        validateCourse(course);

        String query = """
            INSERT INTO Courses (title, description, trainer_id, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, course.getTitle());
            statement.setString(2, course.getDescription());
            if (course.getTrainer() != null) {
                statement.setObject(3, course.getTrainer().getId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return Optional.empty();
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Optional.of(generatedKeys.getInt(1));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Course> getCourseById(int id) throws SQLException {
        String query = """
            SELECT c.*, u.first_name, u.last_name, u.email, u.role
            FROM Courses c
            LEFT JOIN Users u ON c.trainer_id = u.id
            WHERE c.id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToCourse(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = """
            SELECT c.*, u.first_name, u.last_name, u.email, u.role
            FROM Courses c
            LEFT JOIN Users u ON c.trainer_id = u.id
            """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                courses.add(mapResultSetToCourse(resultSet));
            }
        }
        return courses;
    }

    public boolean updateCourse(Course course) throws SQLException {
        validateCourse(course);

        String query = """
            UPDATE Courses SET 
                title = ?, 
                description = ?, 
                trainer_id = ?, 
                updated_at = ?
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, course.getTitle());
            statement.setString(2, course.getDescription());
            if (course.getTrainer() != null) {
                statement.setObject(3, course.getTrainer().getId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(5, course.getId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String query = "DELETE FROM Courses WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            return statement.executeUpdate() > 0;
        }
    }

    // =================== Specialized Queries ===================

    public List<Course> getCoursesByTrainer(int trainerId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = """
            SELECT c.*, u.first_name, u.last_name, u.email, u.role
            FROM Courses c
            LEFT JOIN Users u ON c.trainer_id = u.id
            WHERE c.trainer_id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, trainerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(mapResultSetToCourse(resultSet));
                }
            }
        }
        return courses;
    }

    public List<Course> searchCoursesByTitle(String searchTerm) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = """
            SELECT c.*, u.first_name, u.last_name, u.email, u.role
            FROM Courses c
            LEFT JOIN Users u ON c.trainer_id = u.id
            WHERE c.title ILIKE ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + searchTerm + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(mapResultSetToCourse(resultSet));
                }
            }
        }
        return courses;
    }

    // =================== Helper Methods ===================

    private Course mapResultSetToCourse(ResultSet resultSet) throws SQLException {
        Course course = new Course();
        course.setId(resultSet.getInt("id"));
        course.setTitle(resultSet.getString("title"));
        course.setDescription(resultSet.getString("description"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            course.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        int trainerId = resultSet.getInt("trainer_id");
        if (!resultSet.wasNull()) {
            User trainer = new User();
            trainer.setId(trainerId);
            trainer.setFirstName(resultSet.getString("first_name"));
            trainer.setLastName(resultSet.getString("last_name"));
            trainer.setEmail(resultSet.getString("email"));

            String roleStr = resultSet.getString("role");
            if (roleStr != null) {
                trainer.setRole(User.Role.valueOf(roleStr));
            }

            course.setTrainer(trainer);
        }

        return course;
    }

    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty");
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // =================== Business Logic Methods ===================

    public boolean courseExists(int courseId) throws SQLException {
        String query = "SELECT 1 FROM Courses WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean isTrainerAssignedToCourse(int trainerId, int courseId) throws SQLException {
        String query = "SELECT 1 FROM Courses WHERE id = ? AND trainer_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            statement.setInt(2, trainerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
