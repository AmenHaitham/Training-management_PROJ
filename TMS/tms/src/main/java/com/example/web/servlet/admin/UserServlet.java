package com.example.web.servlet.admin;

import com.example.model.User;
import com.example.service.UserService;
import com.example.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "UserServlet", urlPatterns = {
    "/admin/users", 
    "/admin/users/add", 
    "/admin/users/edit/*", 
    "/admin/users/update",
    "/admin/users/delete/*", 
    "/admin/users/status/*"
})
public class UserServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        if (userService == null) {
            throw new ServletException("UserService is not available");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        // Check if user has admin access
        if (!AuthUtil.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return;
        }

        if (path.equals("/admin/users") && pathInfo == null) {
            // List users page
            listUsers(request, response);
        } else if (path.equals("/admin/users/add")) {
            // Add user form
            showAddForm(request, response);
        } else if (path.equals("/admin/users/edit") && pathInfo != null) {
            // Edit user form
            showEditForm(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        // Check if user has admin access
        if (!AuthUtil.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (path.equals("/admin/users/add")) {
            // Add a new user
            addUser(request, response);
        } else if (path.equals("/admin/users/update")) {
            // Update an existing user
            updateUser(request, response);
        } else if (path.equals("/admin/users/delete") && pathInfo != null) {
            // Delete a user
            deleteUser(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Parse pagination parameters
        int page = getIntParameter(request, "page", 1);
        int pageSize = getIntParameter(request, "size", DEFAULT_PAGE_SIZE);
        
        // Get filter parameters
        String searchQuery = request.getParameter("search");
        String roleFilter = request.getParameter("role");
        
        // Get all users and filter manually
        List<User> allUsers = userService.findAll();
        
        // Apply filters
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> {
                // Apply search filter if provided
                if (searchQuery != null && !searchQuery.isEmpty()) {
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    if (!fullName.toLowerCase().contains(searchQuery.toLowerCase()) &&
                        !user.getUsername().toLowerCase().contains(searchQuery.toLowerCase()) &&
                        !user.getEmail().toLowerCase().contains(searchQuery.toLowerCase())) {
                        return false;
                    }
                }
                
                // Apply role filter if provided
                if (roleFilter != null && !roleFilter.isEmpty()) {
                    if (!user.getRole().equals(roleFilter)) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        // Calculate total users and pages
        int totalUsers = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        
        // Adjust page if out of bounds
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        // Paginate results manually
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredUsers.size());
        
        List<User> pagedUsers = filteredUsers;
        if (fromIndex < toIndex) {
            pagedUsers = filteredUsers.subList(fromIndex, toIndex);
        } else {
            pagedUsers = new ArrayList<>();
        }
        
        // Set attributes for the view
        request.setAttribute("users", pagedUsers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalUsers", totalUsers);
        
        // Set filter attributes for maintaining state
        request.setAttribute("search", searchQuery);
        request.setAttribute("roleFilter", roleFilter);
        
        // Forward to the list view
        request.getRequestDispatcher("/WEB-INF/views/admin/userList.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Create an empty user object for the form
        User user = new User();
        request.setAttribute("user", user);
        
        // Generate a random password for new users
        request.setAttribute("generatePassword", generateRandomPassword());
        
        // Forward to the form view
        request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Extract user ID from path
            String pathInfo = request.getPathInfo();
            String[] pathParts = pathInfo.split("/");
            Long userId = Long.parseLong(pathParts[1]);
            
            // Get the user
            Optional<User> optUser = userService.findById(userId);
            
            if (!optUser.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            
            // Set user as an attribute
            request.setAttribute("user", optUser.get());
            
            // Forward to the form view
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> validationErrors = validateUserForm(request);
        
        if (!validationErrors.isEmpty()) {
            // Set validation errors and form data
            request.setAttribute("errors", validationErrors);
            populateUserFromRequest(request);
            request.setAttribute("messageType", "danger");
            request.setAttribute("message", "Please correct the errors below.");
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
            return;
        }
        
        try {
            // Create a new user from the form data
            User user = new User();
            user.setFirstName(request.getParameter("firstName"));
            user.setLastName(request.getParameter("lastName"));
            user.setUsername(request.getParameter("username"));
            user.setEmail(request.getParameter("email"));
            user.setRole(request.getParameter("role"));
            
            // Set password
            String password = request.getParameter("password");
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
                userService.update(user);
            } else {
                String randomPassword = generateRandomPassword();
                user.setPassword(randomPassword);
                userService.update(user);
            }
            
            // Redirect to the user list with a success message
            request.getSession().setAttribute("message", "User created successfully.");
            request.getSession().setAttribute("messageType", "success");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            // Handle service exceptions
            request.setAttribute("messageType", "danger");
            request.setAttribute("message", "Error creating user: " + e.getMessage());
            populateUserFromRequest(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get user ID from request
            Long userId = Long.parseLong(request.getParameter("id"));
            Optional<User> optUser = userService.findById(userId);
            
            if (!optUser.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            
            User user = optUser.get();
            
            // Update user fields
            user.setFirstName(request.getParameter("firstName"));
            user.setLastName(request.getParameter("lastName"));
            user.setEmail(request.getParameter("email"));
            user.setRole(request.getParameter("role"));
            
            // Check if password should be updated
            String password = request.getParameter("password");
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            
            userService.update(user);
            
            // Redirect to user list with success message
            request.getSession().setAttribute("message", "User updated successfully.");
            request.getSession().setAttribute("messageType", "success");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            // Handle service exceptions
            request.setAttribute("messageType", "danger");
            request.setAttribute("message", "Error updating user: " + e.getMessage());
            populateUserFromRequest(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Extract user ID from path
            String pathInfo = request.getPathInfo();
            String[] pathParts = pathInfo.split("/");
            Long userId = Long.parseLong(pathParts[1]);
            
            // Delete the user
            userService.deleteById(userId);
            
            // Send success response for AJAX requests
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":true,\"message\":\"User deleted successfully.\"}");
        } catch (Exception e) {
            // Send error response for AJAX requests
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Error deleting user: " + e.getMessage() + "\"}");
        }
    }

    private Map<String, String> validateUserForm(HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate required fields
        if (isBlank(request.getParameter("firstName"))) {
            errors.put("firstName", "First name is required");
        }
        
        if (isBlank(request.getParameter("lastName"))) {
            errors.put("lastName", "Last name is required");
        }
        
        if (isBlank(request.getParameter("username"))) {
            errors.put("username", "Username is required");
        } else {
            // Check username uniqueness for new users
            String username = request.getParameter("username");
            String idParam = request.getParameter("id");
            
            if (idParam == null) {
                // New user - check if username exists
                if (userService.findByUsername(username).isPresent()) {
                    errors.put("username", "Username already exists");
                }
            }
        }
        
        if (isBlank(request.getParameter("email"))) {
            errors.put("email", "Email is required");
        } else if (!isValidEmail(request.getParameter("email"))) {
            errors.put("email", "Invalid email format");
        }
        
        if (isBlank(request.getParameter("role"))) {
            errors.put("role", "Role is required");
        }
        
        // Validate password for new users
        if (request.getParameter("id") == null) {
            if (isBlank(request.getParameter("password"))) {
                errors.put("password", "Password is required");
            } else if (request.getParameter("password").length() < 8) {
                errors.put("password", "Password must be at least 8 characters");
            }
            
            if (!request.getParameter("password").equals(request.getParameter("confirmPassword"))) {
                errors.put("confirmPassword", "Passwords do not match");
            }
        } else {
            // Existing user with password change
            if (!isBlank(request.getParameter("password"))) {
                if (request.getParameter("password").length() < 8) {
                    errors.put("password", "Password must be at least 8 characters");
                }
            }
        }
        
        return errors;
    }

    private void populateUserFromRequest(HttpServletRequest request) {
        User user = new User();
        
        // Set ID if editing
        if (request.getParameter("id") != null) {
            try {
                user.setId(Long.parseLong(request.getParameter("id")));
            } catch (NumberFormatException e) {
                // Ignore invalid ID
            }
        }
        
        // Set user fields from request parameters
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        user.setRole(request.getParameter("role"));
        
        // Set user as an attribute
        request.setAttribute("user", user);
        
        // Re-generate password if adding a new user
        if (user.getId() == null) {
            request.setAttribute("generatePassword", request.getParameter("password"));
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        // Generate a password of length 12
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String paramValue = request.getParameter(paramName);
        if (paramValue != null && !paramValue.isEmpty()) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
} 