package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.tms.DAO.CourseDAO;
import com.tms.DAO.UserDAO;
import com.tms.Model.Course;
import com.tms.Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/courses/*")
public class CourseServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(CourseServlet.class.getName());
    private CourseDAO courseService;
    private UserDAO userService;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .setPrettyPrinting()
            .create();

    @Override
    public void init() throws ServletException {
        try {
            userService = new UserDAO();
            courseService = new CourseDAO();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Failed to initialize DAOs", e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllCourses(response);
            } else {
                handleGetSingleCourse(request, response);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid course ID format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while fetching courses", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }

    private void handleGetAllCourses(HttpServletResponse response) throws SQLException, IOException {
        List<Course> courses = courseService.getAllCourses();
        response.getWriter().write(gson.toJson(courses));
    }

    private void handleGetSingleCourse(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, NumberFormatException {
        String[] parts = request.getPathInfo().split("/");
        if (parts.length != 2) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        int courseId = Integer.parseInt(parts[1]);
        Optional<Course> courseOpt = courseService.getCourseById(courseId);
        
        if (courseOpt.isPresent()) {
            response.getWriter().write(gson.toJson(courseOpt.get()));
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Course not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Course courseData = parseCourseFromRequest(request);
            validateCourseData(courseData);
            
            // Verify trainer exists and has correct role
            validateTrainer(courseData.getTrainer().getId());
            
            Optional<Course> createdCourseOpt = courseService.createCourse(courseData);
            if (createdCourseOpt.isEmpty()) {
                throw new SQLException("Failed to create course");
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(createdCourseOpt.get()));
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid course data format");
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while creating course", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create course");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Course ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }

            int courseId = Integer.parseInt(parts[1]);
            Course courseData = parseCourseFromRequest(request);
            courseData.setId(courseId);
            
            validateCourseData(courseData);
            validateTrainer(courseData.getTrainer().getId());
            
            Optional<Course> updatedCourseOpt = courseService.updateCourse(courseData);
            if (updatedCourseOpt.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Course not found");
                return;
            }

            response.getWriter().write(gson.toJson(updatedCourseOpt.get()));
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid course ID format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid course data format");
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while updating course", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update course");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Course ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }

            int courseId = Integer.parseInt(parts[1]);
            boolean deleted = courseService.deleteCourse(courseId);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Course deleted successfully\"}");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Course not found");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid course ID format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while deleting course", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete course");
        }
    }

    private Course parseCourseFromRequest(HttpServletRequest request) throws IOException {
        return gson.fromJson(request.getReader(), Course.class);
    }

    private void validateCourseData(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course data is required");
        }
        
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        
        if (course.getTrainer() == null || course.getTrainer().getId() == 0) {
            throw new IllegalArgumentException("Trainer is required");
        }
    }

    private void validateTrainer(int trainerId) throws SQLException, IllegalArgumentException {
        Optional<User> trainerOpt = userService.getUserById(trainerId);
        if (trainerOpt.isEmpty() || trainerOpt.get().getRole() != User.Role.TRAINER) {
            throw new IllegalArgumentException("Invalid trainer ID or user is not a trainer");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) 
            throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(String.format("{\"error\":\"%s\"}", message));
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}