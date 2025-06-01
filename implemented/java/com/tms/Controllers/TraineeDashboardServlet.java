package com.tms.Controllers;

import com.tms.DAO.TraineeDashboardDAO;
import com.tms.Model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.tms.DB.DatabaseConnection;

@WebServlet("/tms/trainee-dashboard/*")
public class TraineeDashboardServlet extends HttpServlet {
    private TraineeDashboardDAO traineeDashboardDAO;
    private final Gson gson = new GsonBuilder()
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
        DatabaseConnection dataSource = new DatabaseConnection();
        this.traineeDashboardDAO = new TraineeDashboardDAO(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int traineeId = getTraineeIdFromRequest(request); // Implement your auth logic
            
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                // Main dashboard endpoint
                DashboardStats stats = traineeDashboardDAO.getDashboardStats(traineeId);
                response.getWriter().print(gson.toJson(stats));
            } else if (pathInfo.equals("/user")) {
                // Get user profile
                User user = traineeDashboardDAO.getUserById(traineeId);
                response.getWriter().print(gson.toJson(user));
            } else if (pathInfo.equals("/upcoming-sessions")) {
                // Get upcoming sessions
                String limitParam = request.getParameter("limit");
                int limit = limitParam != null ? Integer.parseInt(limitParam) : 5;
                List<Session> sessions = traineeDashboardDAO.getUpcomingSessions(traineeId, limit);
                response.getWriter().print(gson.toJson(sessions));
            } else if (pathInfo.equals("/current-trainings")) {
                // Get current trainings with progress
                List<TrainingProgress> trainings = traineeDashboardDAO.getCurrentTrainings(traineeId);
                response.getWriter().print(gson.toJson(trainings));
            } else if (pathInfo.equals("/attendance")) {
                // Get attendance stats
                AttendanceStats stats = traineeDashboardDAO.getAttendanceStats(traineeId);
                response.getWriter().print(gson.toJson(stats));
            } else if (pathInfo.equals("/recent-materials")) {
                // Get recent materials
                String limitParam = request.getParameter("limit");
                int limit = limitParam != null ? Integer.parseInt(limitParam) : 5;
                List<Material> materials = traineeDashboardDAO.getRecentMaterials(traineeId, limit);
                response.getWriter().print(gson.toJson(materials));
            } else if (pathInfo.equals("/certificates")) {
                // Get certificates
                List<Certificate> certificates = traineeDashboardDAO.getCertificates(traineeId);
                response.getWriter().print(gson.toJson(certificates));
            } else if (pathInfo.startsWith("/download-material/")) {
                // Download material file
                int materialId = Integer.parseInt(pathInfo.substring("/download-material/".length()));
                byte[] fileData = traineeDashboardDAO.getMaterialFile(materialId);
                if (fileData != null) {
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename=\"material_" + materialId + "\"");
                    response.getOutputStream().write(fileData);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print(gson.toJson(Map.of("error", "Material not found")));
                }
            } else if (pathInfo.startsWith("/download-certificate/")) {
                // Download certificate file
                int certificateId = Integer.parseInt(pathInfo.substring("/download-certificate/".length()));
                byte[] fileData = traineeDashboardDAO.getCertificateFile(certificateId);
                if (fileData != null) {
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename=\"certificate_" + certificateId + "\"");
                    response.getOutputStream().write(fileData);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print(gson.toJson(Map.of("error", "Certificate not found")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print(gson.toJson(Map.of("error", "Resource not found")));
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(gson.toJson(Map.of("error", "Database error: " + e.getMessage())));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(gson.toJson(Map.of("error", "Invalid ID format")));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(gson.toJson(Map.of("error", "Internal server error: " + e.getMessage())));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
    
    private int getTraineeIdFromRequest(HttpServletRequest request) {
        // Implement your authentication logic here
        // This could extract the user ID from:
        // - Session
        // - JWT token
        // - Other authentication mechanism
        
        // For now, returning a hardcoded value - replace with your actual auth logic
        return 3;
    }
}