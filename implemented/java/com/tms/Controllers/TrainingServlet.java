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
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.tms.DAO.RoomDAO;
import com.tms.DAO.TrainingDAO;
import com.tms.Model.Room;
import com.tms.Model.Training;
import com.tms.Model.Training.Status;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tms/trainings/*")
public class TrainingServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(TrainingServlet.class.getName());
    private TrainingDAO trainingService;
    private RoomDAO roomService;

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
            LocalDate.parse(json.getAsString()))
        .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> 
            LocalDateTime.parse(json.getAsString()))
        .registerTypeAdapter(LocalDate.class, (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
        .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .setPrettyPrinting()
        .create();

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            trainingService = new TrainingDAO();
            roomService = new RoomDAO();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize DAOs", e);
            throw new ServletException("Failed to initialize DAOs", e);
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
                List<Training> trainings = trainingService.getAllTrainings();
                response.getWriter().write(gson.toJson(trainings));
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int trainingId = Integer.parseInt(parts[1]);
                    Optional<Training> trainingOpt = trainingService.getTrainingById(trainingId);
                    if (trainingOpt.isPresent()) {
                        response.getWriter().write(gson.toJson(trainingOpt.get()));
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training not found");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid training ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while fetching trainings", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Training trainingData = parseTrainingData(request);
            
            // Validation
            if (trainingData == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Training data is required");
                return;
            }
            
            if (trainingData.getTitle() == null || trainingData.getTitle().trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title is required");
                return;
            }
            
            if (trainingData.getRoom() == null || trainingData.getRoom().getId() <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valid room ID is required");
                return;
            }

            // Verify room exists
            Optional<Room> roomOpt = roomService.getRoomById(trainingData.getRoom().getId());
            if (roomOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Room not found");
                return;
            }
            trainingData.setRoom(roomOpt.get());

            // Validate dates
            if (trainingData.getStartDate() == null || trainingData.getEndDate() == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Both start and end dates are required");
                return;
            }
            
            if (trainingData.getStartDate().isAfter(trainingData.getEndDate())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start date must be before end date");
                return;
            }

            // Set default status if not provided
            if (trainingData.getStatus() == null) {
                trainingData.setStatus(Status.AVAILABLE);
            }

            // Create training
            Optional<Training> createdTrainingOpt = trainingService.createTraining(trainingData);
            if (createdTrainingOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create training");
                return;
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(createdTrainingOpt.get()));
            
        } catch (JsonSyntaxException e) {
            logger.log(Level.WARNING, "Invalid JSON format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training data format");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while creating training", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unexpected error creating training", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error");
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Training ID required");
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length == 2) {
            int trainingId = Integer.parseInt(parts[1]);
            Training trainingData = parseTrainingData(request);
            trainingData.setId(trainingId);

            // CASE 1: Status-only update
            if (trainingData.getStatus() != null &&
                trainingData.getTitle() == null &&
                trainingData.getRoom() == null &&
                trainingData.getStartDate() == null &&
                trainingData.getEndDate() == null) {

                boolean success = trainingService.updateTrainingStatus(trainingId, trainingData.getStatus());
                if (success) {
                    response.getWriter().write("{\"message\": \"Training status updated successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training not found");
                }
                return;
            }

            // CASE 2: Full update (existing logic)
            if (trainingData.getRoom() == null || trainingData.getRoom().getId() <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valid room ID is required");
                return;
            }

            Optional<Room> roomOpt = roomService.getRoomById(trainingData.getRoom().getId());
            if (roomOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Room not found");
                return;
            }
            trainingData.setRoom(roomOpt.get());

            if (trainingData.getStartDate() != null && trainingData.getEndDate() != null
                    && trainingData.getStartDate().isAfter(trainingData.getEndDate())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Start date must be before end date");
                return;
            }

            Training updatedTraining = trainingService.updateTraining(trainingData);
            response.getWriter().write(gson.toJson(updatedTraining));

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    } catch (JsonSyntaxException e) {
        logger.log(Level.WARNING, "Invalid JSON format", e);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed JSON");
    } catch (NumberFormatException e) {
        logger.log(Level.WARNING, "Invalid training ID format", e);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training ID");
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Database error while updating training", e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Training ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int trainingId = Integer.parseInt(parts[1]);
                boolean deleted = trainingService.deleteTraining(trainingId);
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"message\":\"Training deleted successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid training ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training ID");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error while deleting training", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        }
    }

    private Training parseTrainingData(HttpServletRequest request) throws IOException {
        return gson.fromJson(request.getReader(), Training.class);
    }

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
    }
}