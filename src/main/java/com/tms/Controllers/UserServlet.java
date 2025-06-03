package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import com.tms.DAO.UserDAO;
import com.tms.Model.User;
import com.tms.Model.User.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private static final String STATUS_PATH = "status";
    private static final String ROLE_PATH = "role";

    private UserDAO userDAO;
    
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
            this.userDAO = new UserDAO();
        } catch (SQLException e) {
            logger.error("Database connection failed", e);
            throw new ServletException("Failed to initialize UserDAO", e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            String[] pathParts = parsePath(req.getPathInfo());
            
            if (pathParts.length == 0) {
                // GET /api/users - Get all users
                handleGetAllUsers(resp);
            } else if (pathParts.length == 1) {
                // GET /api/users/{id} - Get user by ID
                handleGetUserById(pathParts[0], resp);
            } else if (pathParts.length == 2 && ROLE_PATH.equals(pathParts[0])) {
                // GET /api/users/role/{role} - Get users by role
                handleGetUsersByRole(pathParts[1], resp);
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }
        } catch (Exception e) {
            handleException(e, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            User user = gson.fromJson(req.getReader(), User.class);
            if (!isValidUser(user)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user data");
                return;
            }

            User createdUser = userDAO.signUp(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setHeader("Location", req.getRequestURL() + "/" + createdUser.getId());
            gson.toJson(createdUser, resp.getWriter());
        } catch (JsonIOException | JsonSyntaxException e) {
            logger.error("JSON parsing error", e);
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        } catch (IllegalStateException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            handleException(e, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            String[] pathParts = parsePath(req.getPathInfo());
            
            if (pathParts.length == 1) {
                // PUT /api/users/{id} - Update user
                handleUpdateUser(pathParts[0], req, resp);
            } else if (pathParts.length == 2 && STATUS_PATH.equals(pathParts[1])) {
                // PUT /api/users/{id}/status - Update user status
                handleUpdateUserStatus(pathParts[0], req, resp);
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }
        } catch (Exception e) {
            handleException(e, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        prepareJsonResponse(resp);
        
        try {
            String[] pathParts = parsePath(req.getPathInfo());
            
            if (pathParts.length == 1) {
                // DELETE /api/users/{id} - Delete user
                handleDeleteUser(pathParts[0], resp);
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }
        } catch (Exception e) {
            handleException(e, resp);
        }
    }

    // ================ Request Handlers ================
    private void handleGetAllUsers(HttpServletResponse resp) throws SQLException, IOException {
        List<User> users = userDAO.getAllUsers();
        gson.toJson(users, resp.getWriter());
    }

    private void handleGetUserById(String userIdStr, HttpServletResponse resp) 
            throws SQLException, IOException, NumberFormatException {
        int userId = Integer.parseInt(userIdStr);
        Optional<User> user = userDAO.getUserById(userId);
        
        if (user.isPresent()) {
            gson.toJson(user.get(), resp.getWriter());
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }

    private void handleGetUsersByRole(String roleStr, HttpServletResponse resp) 
            throws SQLException, IOException {
        try {
            Role role = Role.valueOf(roleStr.toUpperCase());
            List<User> users = userDAO.getUsersByRole(role);
            gson.toJson(users, resp.getWriter());
        } catch (IllegalArgumentException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid role specified");
        }
    }

    private void handleUpdateUser(String userIdStr, HttpServletRequest req, HttpServletResponse resp) 
            throws IOException, SQLException, NumberFormatException {
        int userId = Integer.parseInt(userIdStr);
        User user = gson.fromJson(req.getReader(), User.class);
        
        if (user == null) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid user data");
            return;
        }
        
        user.setId(userId);
        User updatedUser = userDAO.updateUser(user);
        gson.toJson(updatedUser, resp.getWriter());
    }

    private void handleUpdateUserStatus(String userIdStr, HttpServletRequest req, HttpServletResponse resp) 
            throws IOException, SQLException, NumberFormatException {
        int userId = Integer.parseInt(userIdStr);
        User user = gson.fromJson(req.getReader(), User.class);
        
        if (user == null) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid status data");
            return;
        }
        
        User updatedUser = userDAO.updatUserStatus(userId, user.isActive());
        gson.toJson(updatedUser, resp.getWriter());
    }

    private void handleDeleteUser(String userIdStr, HttpServletResponse resp) 
            throws SQLException, IOException, NumberFormatException {
        int userId = Integer.parseInt(userIdStr);
        boolean deleted = userDAO.deleteUser(userId);
        
        if (deleted) {
            gson.toJson(new ApiResponse("User deleted successfully"), resp.getWriter());
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }

    // ================ Utility Methods ================
    private String[] parsePath(String pathInfo) {
        return pathInfo == null ? new String[0] : 
            Arrays.stream(pathInfo.split("/"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void setCORSHeaders(HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    response.setHeader("Access-Control-Max-Age", "3600");
}

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        gson.toJson(new ApiResponse(message), response.getWriter());
    }

    private void handleException(Exception e, HttpServletResponse resp) throws IOException {
        logger.error("Servlet error", e);
        
        if (e instanceof NumberFormatException) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } else if (e instanceof SQLException) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } else {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private boolean isValidUser(User user) {
        return user != null && 
               user.getFirstName() != null && !user.getFirstName().trim().isEmpty() &&
               user.getLastName() != null && !user.getLastName().trim().isEmpty() &&
               user.getEmail() != null && !user.getEmail().trim().isEmpty() &&
               user.getRole() != null;
    }

    // ================ Inner Classes ================
    private static class ApiResponse {
        private final String message;
        private final long timestamp;
        
        public ApiResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters for JSON serialization
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}