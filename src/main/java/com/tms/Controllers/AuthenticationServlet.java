package com.tms.Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.tms.DAO.UserDAO;
import com.tms.Model.User;
import com.tms.Model.User.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/auth/*")
public class AuthenticationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .setPrettyPrinting()
            .create();
    private final Type mapType = new TypeToken<Map<String, String>>() {}.getType();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (UserDAO userDAO = new UserDAO()) {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(Map.of("error", "Invalid endpoint")));
                return;
            }

            switch (pathInfo) {
                case "/login" -> handleLogin(request, response, userDAO);
                case "/signup" -> handleSignup(request, response, userDAO);
                case "/request-reset" -> handlePasswordResetRequest(request, response, userDAO);
                case "/verify-code" -> handleVerifyCode(request, response, userDAO);
                case "/reset-password" -> handlePasswordReset(request, response, userDAO);
                default -> {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(gson.toJson(Map.of("error", "Endpoint not found")));
                }
            }
        } catch (Exception e) {
            logger.error("Authentication error", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of("error", "Internal server error: " + e.getMessage())));
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) 
        throws IOException {
    response.setContentType("application/json");
    try {
        // Read the entire request body as String
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String json = sb.toString();

        // Parse JSON into JsonObject
        com.google.gson.JsonObject jsonObject = gson.fromJson(json, com.google.gson.JsonObject.class);

        if (jsonObject == null 
            || !jsonObject.has("email") 
            || !jsonObject.has("password")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Email and password are required")));
            return;
        }

        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();

        // Validate user credentials
        User user = userDAO.signIn(email, password);
        if (user != null && user.isActive()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole().name());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "Login successful");
            responseData.put("user", user);
            responseData.put("sessionId", session.getId());

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(responseData));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(Map.of("error", "Invalid credentials or account not active")));
        }

    } catch (com.google.gson.JsonSyntaxException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(gson.toJson(Map.of("error", "Invalid JSON format")));
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(gson.toJson(Map.of("error", "Internal server error")));
    }
}


    private void handleSignup(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) 
            throws IOException {
        try {
            User user = gson.fromJson(request.getReader(), User.class);
            if (user == null || user.getEmail() == null || user.getPassword() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(Map.of("error", "Invalid user data")));
                return;
            }

            if (user.getRole() == null) {
                user.setRole(Role.TRAINEE);
            }

            User registeredUser = userDAO.signUp(user);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", registeredUser);

            if (registeredUser.getRole() == Role.TRAINER || registeredUser.getRole() == Role.ADMIN) {
                responseData.put("message", "Registration successful. Your account requires admin activation.");
                responseData.put("requiresActivation", true);
            } else {
                responseData.put("message", "Registration successful");
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(responseData));
        } catch (IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        } catch (JsonSyntaxException | JsonIOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Invalid request format")));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of("error", "Registration failed: " + e.getMessage())));
        }
    }

    private void handlePasswordResetRequest(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) 
            throws IOException {
        try {
            Map<String, String> requestData = gson.fromJson(request.getReader(), mapType);
            String email = requestData.get("email");

            if (email == null || email.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(Map.of("error", "Email is required")));
                return;
            }

            boolean codeSent = userDAO.sendVerificationCode(email);
            if (codeSent) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(Map.of("message", "Verification code sent to email")));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(Map.of("error", "Email not found")));
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Invalid request format")));
        }
    }

    private void handleVerifyCode(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) 
        throws IOException {
    response.setContentType("application/json");
    
    try {
        Map<String, String> requestData = gson.fromJson(request.getReader(), mapType);

        if (requestData == null || !requestData.containsKey("code")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Missing 'code' in request body")));
            return;
        }

        String code = requestData.get("code");
        if (code == null || code.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Verification code is required")));
            return;
        }

        boolean isValid = userDAO.verifyCode(code.trim());
        if (isValid) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(Map.of("message", "Verification successful")));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(Map.of("error", "Invalid or expired verification code")));
        }

    } catch (JsonSyntaxException | JsonIOException e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(gson.toJson(Map.of("error", "Invalid JSON format")));
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(gson.toJson(Map.of("error", "An unexpected error occurred")));
    }
}


    private void handlePasswordReset(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO) 
            throws IOException {
        try {
            Map<String, String> requestData = gson.fromJson(request.getReader(), mapType);
            String email = requestData.get("email");
            String newPassword = requestData.get("newPassword");
            String code = requestData.get("code");

            if (email == null || newPassword == null || code == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(Map.of("error", "Email, new password and verification code are required")));
                return;
            }

            boolean passwordReset = userDAO.resetPassword(email, newPassword, code);
            if (passwordReset) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(Map.of("message", "Password reset successful")));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(Map.of("error", "Password reset failed. Invalid code or email")));
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Invalid request format")));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(Map.of("error", "Invalid endpoint")));
            return;
        }

        if (pathInfo.equals("/logout")) {
            handleLogout(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(Map.of("error", "Endpoint not found")));
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(Map.of("message", "Logout successful")));
    }
}
