package com.tms.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.tms.DB.DatabaseConnection;
import com.tms.Model.Course;
import com.tms.Model.Feedback;
import com.tms.Model.Session;
import com.tms.Model.Training;
import com.tms.Model.User;

public class AdminDashboardDAO implements AutoCloseable {
    private Connection connection;

    public AdminDashboardDAO() throws SQLException {
        this.connection = DatabaseConnection.getDatabaseConnection();
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                DatabaseConnection.releaseConnection(connection);
            } catch (Exception e) {
                System.err.println("Error releasing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public int getTotalTrainees() throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE role = 'TRAINEE'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalTrainers() throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE role = 'TRAINER'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalTrainings() throws SQLException {
        String query = "SELECT COUNT(*) FROM Trainings";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalCourses() throws SQLException {
        String query = "SELECT COUNT(*) FROM Courses";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalSessions() throws SQLException {
        String query = "SELECT COUNT(*) FROM Sessions";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalFeedbacks() throws SQLException {
        String query = "SELECT COUNT(*) FROM Feedbacks";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Training> getRecentTrainings(int limit) throws SQLException, ClassNotFoundException {
        TrainingDAO trainingDAO = new TrainingDAO();
        try {
            return trainingDAO.getRecentTrainings(limit);
        } finally {
            if (trainingDAO instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) trainingDAO).close();
                } catch (Exception e) {
                    System.err.println("Error closing TrainingDAO: " + e.getMessage());
                }
            }
        }
    }

    public List<Course> getRecentCourses(int limit) throws SQLException, ClassNotFoundException {
        try (CourseDAO courseDAO = new CourseDAO()) {
            return courseDAO.getRecentCourses(limit);
        }
    }

    public List<Session> getRecentSessions(int limit) throws SQLException, ClassNotFoundException {
        try (SessionDAO sessionDAO = new SessionDAO()) {
            return sessionDAO.getRecentSessions(limit);
        }
    }

    public List<User> getRecentUsers(int limit) throws SQLException, ClassNotFoundException {
        UserDAO userDAO = new UserDAO();
        try {
            return userDAO.getRecentUsers(limit);
        } finally {
            if (userDAO instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) userDAO).close();
                } catch (Exception e) {
                    System.err.println("Error closing UserDAO: " + e.getMessage());
                }
            }
        }
    }

    public List<Feedback> getRecentFeedbacks(int limit) throws SQLException, ClassNotFoundException {
        try (FeedbackDAO feedbackDAO = new FeedbackDAO()) {
            return feedbackDAO.getRecentFeedbacks(limit);
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    
        AdminDashboardDAO dashboardDAO = new AdminDashboardDAO();
        System.out.println("Total Trainees: " + dashboardDAO.getTotalTrainees());
        System.out.println("Total Trainers: " + dashboardDAO.getTotalTrainers());
        System.out.println("Total Trainings: " + dashboardDAO.getTotalTrainings());
        System.out.println("Total Courses: " + dashboardDAO.getTotalCourses());
        System.out.println("Total Sessions: " + dashboardDAO.getTotalSessions());
        System.out.println("Total Feedbacks: " + dashboardDAO.getTotalFeedbacks());

        List<Training> recentTrainings = dashboardDAO.getRecentTrainings(5);
        System.out.println("Recent Trainings:");
        for (Training training : recentTrainings) {
            System.out.println(training);
        }

        List<Feedback> recentFeedbacks = dashboardDAO.getRecentFeedbacks(5);
        System.out.println("Recent Feedbacks:");
        for (Feedback feedback : recentFeedbacks) {
            System.out.println(feedback);
        }

        List<Session> recentSessions = dashboardDAO.getRecentSessions(5);
        System.out.println("Recent Sessions:");
        for (Session session : recentSessions) {
            System.out.println(session);
        }

        List<User> recentUsers = dashboardDAO.getRecentUsers(5);
        System.out.println("Recent Users:");
        for (User user : recentUsers) {
            System.out.println(user);
        }

        List<Course> recentCourses = dashboardDAO.getRecentCourses(5);
        System.out.println("Recent Courses:");
        for (Course course : recentCourses) {
            System.out.println(course);
        }
        
        
    }

}
