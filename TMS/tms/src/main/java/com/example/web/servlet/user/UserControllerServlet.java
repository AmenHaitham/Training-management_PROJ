package com.example.web.servlet.user;

import com.example.model.User;
import com.example.service.UserService;
import com.example.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Servlet for handling user CRUD operations
 */
@WebServlet("/user/*")
public class UserControllerServlet extends HttpServlet {
    private final UserService userService;
    
    public UserControllerServlet() {
        this.userService = new UserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirect to list page
            response.sendRedirect(request.getContextPath() + "/users");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String action = pathParts[1];
        
        switch (action) {
            case "view":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                viewUser(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "create":
                showCreateForm(request, response);
                break;
                
            case "edit":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                showEditForm(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "delete":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                deleteUser(Long.parseLong(pathParts[2]), request, response);
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String action = pathParts[1];
        
        switch (action) {
            case "create":
                createUser(request, response);
                break;
                
            case "edit":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                updateUser(Long.parseLong(pathParts[2]), request, response);
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void viewUser(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<User> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            request.setAttribute("user", optionalUser.get());
            request.getRequestDispatcher("/WEB-INF/views/user/view.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/user/form.jsp").forward(request, response);
    }
    
    private void showEditForm(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<User> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            request.setAttribute("user", optionalUser.get());
            request.getRequestDispatcher("/WEB-INF/views/user/form.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }
    
    private void createUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        populateUserFromRequest(user, request);
        
        try {
            userService.register(user);
            request.getSession().setAttribute("successMessage", "User created successfully!");
            response.sendRedirect(request.getContextPath() + "/users");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/user/form.jsp").forward(request, response);
        }
    }
    
    private void updateUser(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<User> optionalUser = userService.findById(id);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            populateUserFromRequest(user, request);
            
            try {
                userService.update(user);
                request.getSession().setAttribute("successMessage", "User updated successfully!");
                response.sendRedirect(request.getContextPath() + "/users");
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", e.getMessage());
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/user/form.jsp").forward(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }
    
    private void deleteUser(Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean deleted = userService.deleteById(id);
        
        if (deleted) {
            request.getSession().setAttribute("successMessage", "User deleted successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to delete user. User not found.");
        }
        
        response.sendRedirect(request.getContextPath() + "/users");
    }
    
    private void populateUserFromRequest(User user, HttpServletRequest request) {
        user.setUsername(request.getParameter("username"));
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
        
        // Only set password if provided (for updates)
        String password = request.getParameter("password");
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password);
        }
        
        user.setRole(request.getParameter("role"));
        
        // Parse and set birth date
        String birthDateStr = request.getParameter("birthDate");
        if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
                user.setBirthDate(birthDate);
            } catch (DateTimeParseException e) {
                // Ignore invalid date
            }
        }
        
        // Set current timestamp for created/updated at fields if not already set
        LocalDateTime now = LocalDateTime.now();
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(now);
        }
        user.setUpdatedAt(now);
    }
} 