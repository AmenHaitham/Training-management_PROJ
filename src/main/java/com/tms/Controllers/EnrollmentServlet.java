package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tms.DAO.EnrollmentDAO;
import com.tms.Model.Enrollment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/enrollments/*")
public class EnrollmentServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServlet.class);
    private EnrollmentDAO enrollmentDAO;
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                            new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                            new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .setPrettyPrinting()
            .create();

    @Override
    public void init() throws ServletException {
        try {
            this.enrollmentDAO = new EnrollmentDAO();
        } catch (Exception e) {
            logger.error("Failed to initialize EnrollmentDAO", e);
            throw new ServletException("Failed to initialize EnrollmentDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            String pathInfo = req.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /enrollments - Get all enrollments
                handleGetAllEnrollments(resp);
            } else {
                String[] parts = pathInfo.split("/");
                
                if (parts.length == 2) {
                    // GET /enrollments/{id} - Get by ID
                    int id = Integer.parseInt(parts[1]);
                    handleGetEnrollmentById(id, resp);
                } else if (parts.length == 3) {
                    // GET /enrollments/trainee/{id} - Get by trainee ID
                    if (parts[1].equals("trainee")) {
                        int traineeId = Integer.parseInt(parts[2]);
                        handleGetEnrollmentsByTrainee(traineeId, resp);
                    } 
                    // GET /enrollments/training/{id} - Get by training ID
                    else if (parts[1].equals("training")) {
                        int trainingId = Integer.parseInt(parts[2]);
                        handleGetEnrollmentsByTraining(trainingId, resp);
                    }
                    // GET /enrollments/count/{id} - Get count for training
                    else if (parts[1].equals("count")) {
                        int trainingId = Integer.parseInt(parts[2]);
                        handleGetEnrollmentCount(trainingId, resp);
                    }
                    // GET /enrollments/check?trainee=X&training=Y - Check if enrolled
                    else if (parts[1].equals("check")) {
                        int traineeId = Integer.parseInt(req.getParameter("trainee"));
                        int trainingId = Integer.parseInt(req.getParameter("training"));
                        handleCheckEnrollment(traineeId, trainingId, resp);
                    } else {
                        sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, 
                            "Invalid endpoint: " + pathInfo);
                    }
                } else {
                    sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, 
                        "Invalid request path: " + pathInfo);
                }
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, 
                "Invalid ID format: must be a number");
        } catch (SQLException e) {
            logger.error("Database error", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Database error occurred");
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            Enrollment enrollment = gson.fromJson(req.getReader(), Enrollment.class);
            validateEnrollment(enrollment);
            
            Optional<Integer> createdId = enrollmentDAO.createEnrollment(enrollment);
            if (createdId.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"id\": " + createdId.get() + "}");
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to create enrollment");
            }
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating enrollment", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error creating enrollment");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            String pathInfo = req.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing ID");
                return;
            }
            
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                // DELETE /enrollments/{id} - Delete by enrollment ID
                int id = Integer.parseInt(parts[1]);
                handleDeleteEnrollment(id, resp);
            } else if (parts.length == 3 && parts[1].equals("unenroll")) {
                // DELETE /enrollments/unenroll/{traineeId}/{trainingId}
                int traineeId = Integer.parseInt(parts[2]);
                int trainingId = Integer.parseInt(parts[3]);
                handleUnenroll(traineeId, trainingId, resp);
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, 
                    "Invalid request path: " + pathInfo);
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, 
                "Invalid ID format: must be a number");
        } catch (Exception e) {
            logger.error("Error deleting enrollment", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error deleting enrollment");
        }
    }

    // ============== Request Handlers ==============
    private void handleGetAllEnrollments(HttpServletResponse resp) throws SQLException, IOException {
        List<Enrollment> enrollments = enrollmentDAO.getAllEnrollments();
        gson.toJson(enrollments, resp.getWriter());
    }

    private void handleGetEnrollmentById(int id, HttpServletResponse resp) throws SQLException, IOException {
        Optional<Enrollment> enrollment = enrollmentDAO.getEnrollmentById(id);
        if (enrollment.isPresent()) {
            gson.toJson(enrollment.get(), resp.getWriter());
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, 
                "Enrollment not found with ID: " + id);
        }
    }

    private void handleGetEnrollmentsByTrainee(int traineeId, HttpServletResponse resp) throws SQLException, IOException {
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByTraineeId(traineeId);
        gson.toJson(enrollments, resp.getWriter());
    }

    private void handleGetEnrollmentsByTraining(int trainingId, HttpServletResponse resp) throws SQLException, IOException {
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByTrainingId(trainingId);
        gson.toJson(enrollments, resp.getWriter());
    }

    private void handleGetEnrollmentCount(int trainingId, HttpServletResponse resp) throws SQLException, IOException {
        int count = enrollmentDAO.countEnrollmentsForTraining(trainingId);
        resp.getWriter().write("{\"count\": " + count + "}");
    }

    private void handleCheckEnrollment(int traineeId, int trainingId, HttpServletResponse resp) throws SQLException, IOException {
        boolean isEnrolled = enrollmentDAO.isTraineeEnrolled(traineeId, trainingId);
        resp.getWriter().write("{\"isEnrolled\": " + isEnrolled + "}");
    }

    private void handleDeleteEnrollment(int id, HttpServletResponse resp) throws SQLException, IOException {
        boolean deleted = enrollmentDAO.deleteEnrollment(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, 
                "Enrollment not found with ID: " + id);
        }
    }

    private void handleUnenroll(int traineeId, int trainingId, HttpServletResponse resp) throws SQLException, IOException {
        boolean unenrolled = enrollmentDAO.unenrollTrainee(traineeId, trainingId);
        if (unenrolled) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, 
                "Enrollment not found for trainee " + traineeId + " and training " + trainingId);
        }
    }

    // ============== Utility Methods ==============
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private void validateEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null");
        }
        if (enrollment.getTrainee() == null || enrollment.getTrainee().getId() <= 0) {
            throw new IllegalArgumentException("Invalid trainee");
        }
        if (enrollment.getTraining() == null || enrollment.getTraining().getId() <= 0) {
            throw new IllegalArgumentException("Invalid training");
        }
    }
}