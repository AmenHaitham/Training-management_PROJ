package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tms.DAO.AttendanceDAO;
import com.tms.Model.Attendance;
import com.tms.Model.Session;
import com.tms.Model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tms/attendances/*")
public class AttendanceServlet extends HttpServlet {

    private AttendanceDAO attendanceDAO;
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
            attendanceDAO = new AttendanceDAO();
        } catch (SQLException e) {
            throw new ServletException("Failed to initialize AttendanceDAO", e);
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
                // Get all attendances
                List<Attendance> attendanceRecords = attendanceDAO.getAllAttendances();
                sendSuccessResponse(response, attendanceRecords);
            } else if (pathInfo.startsWith("/session/")) {
                // Get attendances by session
                int sessionId = Integer.parseInt(pathInfo.split("/")[2]);
                List<Attendance> records = attendanceDAO.getAttendanceBySession(sessionId);
                sendSuccessResponse(response, records);
            } else if (pathInfo.startsWith("/trainee/")) {
                // Get attendances by trainee
                int traineeId = Integer.parseInt(pathInfo.split("/")[2]);
                List<Attendance> records = attendanceDAO.getAttendanceByTrainee(traineeId);
                sendSuccessResponse(response, records);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid endpoint");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing request: " + e.getMessage());
        }
    }

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    setCORSHeaders(response);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    try {
        // Parse JSON with lenient mode to handle partial objects
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(request.getReader());
        JsonObject json = root.getAsJsonObject();

        // Validate required fields
        if (!json.has("trainee") || !json.get("trainee").getAsJsonObject().has("id") 
                || !json.has("session") || !json.get("session").getAsJsonObject().has("id")
                || !json.has("status")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "Missing required fields: trainee.id, session.id, or status");
            return;
        }

        // Create attendance object
        Attendance attendance = new Attendance();
        
        // Set trainee (just ID)
        User trainee = new User();
        trainee.setId(json.get("trainee").getAsJsonObject().get("id").getAsInt());
        attendance.setTrainee(trainee);
        
        // Set session (just ID)
        Session session = new Session();
        session.setId(json.get("session").getAsJsonObject().get("id").getAsInt());
        attendance.setSession(session);
        
        attendance.setStatus(json.get("status").getAsString());
        attendance.setRecordedAt(new Timestamp(System.currentTimeMillis()));

        // Save to database
        Attendance createdAttendance = attendanceDAO.markAttendance(attendance);
        
        // Return success response
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(gson.toJson(createdAttendance));
        
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
            "Error creating attendance record: " + e.getMessage());
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Attendance ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int attendanceId = Integer.parseInt(parts[1]);
                Attendance attendance = gson.fromJson(request.getReader(), Attendance.class);
                attendance.setId(attendanceId);
                
                Attendance updatedAttendance = attendanceDAO.updatAttendance(attendance);
                response.getWriter().write(gson.toJson(updatedAttendance));
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid attendance ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating attendance record");
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Attendance ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int attendanceId = Integer.parseInt(parts[1]);
                boolean deleted = attendanceDAO.deleteAttendance(attendanceId);
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"message\":\"Attendance record deleted successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Attendance record not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid attendance ID");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting attendance record");
        }
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

     // Helper methods for standardized responses
    private void sendSuccessResponse(HttpServletResponse response, Object data) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("data", data);
        response.getWriter().write(gson.toJson(responseMap));
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) 
            throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", "error");
        errorMap.put("message", message);
        response.getWriter().write(gson.toJson(errorMap));
    }

}