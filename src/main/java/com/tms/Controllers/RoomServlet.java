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
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tms.DAO.RoomDAO;
import com.tms.Model.Room;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/rooms/*")
public class RoomServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RoomServlet.class.getName());
    private RoomDAO roomService;
    
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
            roomService = new RoomDAO();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize RoomDAO", e);
            throw new ServletException("Failed to initialize RoomDAO", e);
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
                handleGetAllRooms(response);
            } else {
                handleGetSingleRoom(request, response);
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid room ID format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid room ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while processing request", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }

    private void handleGetAllRooms(HttpServletResponse response) 
            throws SQLException, IOException {
        List<Room> rooms = roomService.getAllRooms();
        response.getWriter().write(gson.toJson(rooms));
    }

    private void handleGetSingleRoom(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, NumberFormatException {
        String[] parts = request.getPathInfo().split("/");
        if (parts.length != 2) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            return;
        }

        int roomId = Integer.parseInt(parts[1]);
        Optional<Room> roomOpt = roomService.getRoomById(roomId);
        
        if (roomOpt.isPresent()) {
            response.getWriter().write(gson.toJson(roomOpt.get()));
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Room not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Room room = parseRoomFromRequest(request);
            validateRoomData(room);
            
            Optional<Room> createdRoomOpt = roomService.createRoom(room);
            if (createdRoomOpt.isEmpty()) {
                throw new SQLException("Failed to create room");
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(createdRoomOpt.get()));
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid room data format");
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validation error", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while creating room", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create room");
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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Room ID required");
            return;
        }

        String[] parts = pathInfo.split("/");
        
        // Handle status update requests (URL pattern: /rooms/{id}/status)
        if (parts.length == 3 && "status".equals(parts[2])) {
            handleStatusUpdate(parts[1], request, response);
            return;
        }
        
        // Handle regular room updates (URL pattern: /rooms/{id})
        if (parts.length == 2) {
            handleRoomUpdate(parts[1], request, response);
            return;
        }
        
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
    } catch (NumberFormatException e) {
        logger.log(Level.WARNING, "Invalid room ID format", e);
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid room ID");
    } catch (JsonIOException | JsonSyntaxException e) {
        logger.log(Level.WARNING, "Invalid JSON format", e);
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid data format");
    } catch (IllegalArgumentException e) {
        logger.log(Level.WARNING, "Validation error", e);
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Database error", e);
        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database operation failed");
    }
}

private void handleStatusUpdate(String roomIdStr, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, SQLException, NumberFormatException {
    int roomId = Integer.parseInt(roomIdStr);
    
    // Parse the status update request
    JsonObject jsonObject = gson.fromJson(request.getReader(), JsonObject.class);
    if (!jsonObject.has("status")) {
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Status field is required");
        return;
    }

    String newStatus = jsonObject.get("status").getAsString();
    
    // Validate the status
    if (!Room.Status.isValid(newStatus)) {
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid status value");
        return;
    }

    // Update the room status in database
    boolean updated = roomService.updateRoomStatus(roomId, Room.Status.valueOf(newStatus));
    if (!updated) {
        sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Room not found");
        return;
    }

    // Return the updated room
    Optional<Room> updatedRoom = roomService.getRoomById(roomId);
    if (updatedRoom.isPresent()) {
        response.getWriter().write(gson.toJson(updatedRoom.get()));
    } else {
        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch updated room");
    }
}

private void handleRoomUpdate(String roomIdStr, HttpServletRequest request, HttpServletResponse response) 
        throws IOException, SQLException, NumberFormatException {
    int roomId = Integer.parseInt(roomIdStr);
    Room room = parseRoomFromRequest(request);
    room.setId(roomId);
    
    validateRoomData(room);
    
    Optional<Room> updatedRoomOpt = roomService.updateRoom(room);
    if (updatedRoomOpt.isEmpty()) {
        sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Room not found");
        return;
    }

    response.getWriter().write(gson.toJson(updatedRoomOpt.get()));
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Room ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length != 2) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                return;
            }

            int roomId = Integer.parseInt(parts[1]);
            boolean deleted = roomService.deleteRoom(roomId);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Room deleted successfully\"}");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Room not found");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid room ID format", e);
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid room ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while deleting room", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete room");
        }
    }

    private Room parseRoomFromRequest(HttpServletRequest request) throws IOException {
        return gson.fromJson(request.getReader(), Room.class);
    }

    private void validateRoomData(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room data is required");
        }
        
        if (room.getLocation() == null || room.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        
        if (room.getStatus() == null) {
            throw new IllegalArgumentException("Valid status is required");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) 
            throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(String.format("{\"error\":\"%s\"}", message));
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://196.221.167.63:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}