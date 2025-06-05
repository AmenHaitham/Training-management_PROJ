package com.tms.Controllers;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tms.Model.Session;
import com.tms.Model.TrainingCourse;
import com.tms.DAO.SessionDAO;
import com.tms.DAO.TrainingCourseDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.List;

@WebServlet("/api/sessions/*")
public class SessionServlet extends HttpServlet {
    private SessionDAO sessionService;
    private TrainingCourseDAO trainingCourseService;
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
    public void init() {
        try {
            sessionService = new SessionDAO();
            trainingCourseService = new TrainingCourseDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                List<Session> sessions = sessionService.getAllSessions();
                response.getWriter().write(gson.toJson(sessions));
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int sessionId = Integer.parseInt(parts[1]);
                    Session session = sessionService.getSessionById(sessionId).get();
                    if (session != null) {
                        response.getWriter().write(gson.toJson(session));
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
    Session sessionData = parseSessionData(request);
    System.out.println("Parsed session: " + gson.toJson(sessionData));

    TrainingCourse trainingCourse = trainingCourseService
        .getTrainingCourseById(sessionData.getTrainingCourse().getId())
        .orElse(null);

    if (trainingCourse == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training course ID");
        return;
    }

    if (sessionData.getStartTime().after(sessionData.getEndTime())) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "End time must be after start time");
        return;
    }

    Session createdSession = sessionService.createSession(sessionData).get();
    response.setStatus(HttpServletResponse.SC_CREATED);
    response.getWriter().write(gson.toJson(createdSession));

} catch (Exception e) {
    e.printStackTrace();
    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating session: " + e.getMessage());
}

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int sessionId = Integer.parseInt(parts[1]);
                Session sessionData = parseSessionData(request);
                sessionData.setId(sessionId);

                TrainingCourse trainingCourse = trainingCourseService.getTrainingCourseById(sessionData.getTrainingCourse().getId()).get();
                if (trainingCourse == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training course ID");
                    return;
                }

                if (sessionData.getStartTime().after(sessionData.getEndTime())) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "End time must be after start time");
                    return;
                }

                Session updatedSession = sessionService.updateSession(sessionData).get();
                response.getWriter().write(gson.toJson(updatedSession));
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating session");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int sessionId = Integer.parseInt(parts[1]);
                boolean deleted = sessionService.deleteSession(sessionId);
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"message\": \"Session deleted successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting session");
        }
    }

    private Session parseSessionData(HttpServletRequest request) throws IOException {
        try {
            return gson.fromJson(request.getReader(), Session.class);
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid JSON input", e);
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://196.221.167.63:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
