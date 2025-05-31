package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tms.DAO.FeedbackDAO;
import com.tms.Model.Feedback;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tms/feedbacks/*")
public class FeedbackServlet extends HttpServlet {

    private FeedbackDAO feedbackDAO;

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
            feedbackDAO = new FeedbackDAO();
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize FeedbackDAO", e);
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
                // Handle query parameters for filtering
                String sessionId = request.getParameter("sessionId");
                String rating = request.getParameter("rating");
                
                List<Feedback> feedbacks;
                if (sessionId != null && !sessionId.isEmpty()) {
                    feedbacks = feedbackDAO.getFeedbacksBySessionId(Integer.parseInt(sessionId));
                } else if (rating != null && !rating.isEmpty()) {
                    feedbacks = feedbackDAO.getFeedbacksByRating(Integer.parseInt(rating));
                } else {
                    feedbacks = feedbackDAO.getAllFeedbacks();
                }
                response.getWriter().write(gson.toJson(feedbacks));
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int feedbackId = Integer.parseInt(parts[1]);
                    Optional<Feedback> feedback = feedbackDAO.getFeedbackById(feedbackId);
                    if (feedback.isPresent()) {
                        response.getWriter().write(gson.toJson(feedback.get()));
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feedback not found");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Feedback ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int feedbackId = Integer.parseInt(parts[1]);
                boolean deleted = feedbackDAO.deleteFeedback(feedbackId);
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"success\":true,\"message\":\"Feedback deleted successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feedback not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid feedback ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    public void destroy() {
        try {
            if (feedbackDAO != null) {
                feedbackDAO.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}