package com.tms.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Course;
import com.tms.Model.User;

public class CourseDAO implements AutoCloseable {
    private final Connection connection;

    public CourseDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    // =================== CRUD Operations ===================

    public Optional<Course> createCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (title, description, trainer_id) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, course.getTitle());
            stmt.setString(2, course.getDescription());
            stmt.setInt(3, course.getTrainer().getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt("id");
                    course.setId(newId);
                    return Optional.of(course);
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

    public Optional<Course> updateCourse(Course course) throws SQLException {
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
                statement.setObject(3, course.getTrainer().getId(), Types.INTEGER);
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(5, course.getId());

            int rowsAffected = statement.executeUpdate();

            // If update was successful, return the updated course
            if (rowsAffected > 0) {
                return Optional.of(course);
            } else {
                // Course with given ID doesn't exist
                return Optional.empty();
            }
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getDatabaseConnection();
            conn.setAutoCommit(false);

            // Now delete the course
            try (PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM courses WHERE id = ?")) {
                stmt2.setInt(1, courseId);
                int affectedRows = stmt2.executeUpdate();
                conn.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        CourseDAO courseDAO = new CourseDAO();
        boolean result  = courseDAO.deleteCourse(2);
        System.out.println(result);
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

    public List<Course> getRecentCourses(int limit) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = """
            SELECT c.*, u.first_name, u.last_name, u.email, u.role
            FROM Courses c
            LEFT JOIN Users u ON c.trainer_id = u.id
            ORDER BY c.created_at DESC
            LIMIT ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courses.add(mapResultSetToCourse(resultSet));
                }
            }
        }
        return courses;
    }

    // =================== Helper Methods ===================

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setTitle(rs.getString("title"));
        course.setDescription(rs.getString("description"));
        course.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        course.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        User trainer = new User();
        trainer.setId(rs.getInt("trainer_id"));
        trainer.setFirstName(rs.getString("first_name"));
        trainer.setLastName(rs.getString("last_name"));
        trainer.setEmail(rs.getString("email"));
        trainer.setRole(User.Role.valueOf(rs.getString("role")));  // Ensure this is safe
        
        course.setTrainer(trainer);

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
