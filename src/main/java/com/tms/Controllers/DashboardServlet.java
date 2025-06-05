package com.tms.Controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tms.DAO.AdminDashboardDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/dashboard/*")
public class DashboardServlet extends HttpServlet {
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
    @SuppressWarnings("ConvertToStringSwitch")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (AdminDashboardDAO dashboardDAO = new AdminDashboardDAO()) {
            setCORSHeaders(request, response);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                Map<String, Integer> stats = new HashMap<>();

                int totalTrainees = dashboardDAO.getTotalTrainees();
                stats.put("trainees", totalTrainees);

                int totalTrainers = dashboardDAO.getTotalTrainers();
                stats.put("trainers", totalTrainers);

                int totalTrainings = dashboardDAO.getTotalTrainings();
                stats.put("trainings", totalTrainings);

                int totalCourses = dashboardDAO.getTotalCourses();
                stats.put("courses", totalCourses);

                int totalSessions = dashboardDAO.getTotalSessions();
                stats.put("sessions", totalSessions);

                int totalFeedbacks = dashboardDAO.getTotalFeedbacks();
                stats.put("feedbacks", totalFeedbacks);

                response.getWriter().print(gson.toJson(stats));
            } else if (pathInfo.equals("/recent")) {
                Map<String, Object> recentActivities = new HashMap<>();
                recentActivities.put("trainings", dashboardDAO.getRecentTrainings(5));
                recentActivities.put("courses", dashboardDAO.getRecentCourses(5));
                recentActivities.put("sessions", dashboardDAO.getRecentSessions(5));
                recentActivities.put("users", dashboardDAO.getRecentUsers(5));
                recentActivities.put("feedbacks", dashboardDAO.getRecentFeedbacks(5));

                response.getWriter().print(gson.toJson(recentActivities));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().print(gson.toJson(Map.of("error", "Resource not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print(gson.toJson(Map.of("error", "Internal server error: " + e.getMessage())));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCORSHeaders(HttpServletRequest request, HttpServletResponse response) {
        // Allow requests from any origin
        response.setHeader("Access-Control-Allow-Origin", "http://196.221.167.63:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}
