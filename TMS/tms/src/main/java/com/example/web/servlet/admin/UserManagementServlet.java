package com.example.web.servlet.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.User;
import com.example.service.UserService;
import com.example.util.PasswordUtil;
import com.example.util.ServletUtil;

@WebServlet(urlPatterns = {
    "/admin/users", 
    "/admin/users/add", 
    "/admin/users/edit", 
    "/admin/users/view/*", 
    "/admin/users/delete/*"
})
public class UserManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(UserManagementServlet.class);
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        if (userService == null) {
            throw new ServletException("UserService not available");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        String path = servletPath + (pathInfo != null ? pathInfo : "");
        
        try {
            if (path.equals("/admin/users")) {
                listUsers(request, response);
            } else if (path.equals("/admin/users/add")) {
                showAddUserForm(request, response);
            } else if (path.startsWith("/admin/users/view/")) {
                viewUser(request, response);
            } else if (path.startsWith("/admin/users/edit/")) {
                showEditUserForm(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in UserManagementServlet doGet", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        String path = servletPath + (pathInfo != null ? pathInfo : "");
        
        try {
            if (path.equals("/admin/users/add")) {
                addUser(request, response);
            } else if (path.equals("/admin/users/edit")) {
                updateUser(request, response);
            } else if (path.startsWith("/admin/users/delete/")) {
                deleteUser(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in UserManagementServlet doPost", e);
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
    
    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get filter parameters
        String nameFilter = request.getParameter("name");
        String roleFilter = request.getParameter("role");
        
        List<User> allUsers = userService.findAll();
        List<User> filteredUsers = new ArrayList<>();
        
        // Apply filters manually
        for (User user : allUsers) {
            boolean match = true;
            
            if (nameFilter != null && !nameFilter.isEmpty()) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                if (!fullName.toLowerCase().contains(nameFilter.toLowerCase())) {
                    match = false;
                }
            }
            
            if (match && roleFilter != null && !roleFilter.isEmpty()) {
                if (!user.getRole().equals(roleFilter)) {
                    match = false;
                }
            }
            
            if (match) {
                filteredUsers.add(user);
            }
        }
        
        request.setAttribute("users", filteredUsers);
        request.setAttribute("nameFilter", nameFilter);
        request.setAttribute("roleFilter", roleFilter);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/userList.jsp").forward(request, response);
    }
    
    private void showAddUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Generate a random password for the new user
        String generatedPassword = generateRandomPassword();
        request.setAttribute("generatedPassword", generatedPassword);
        
        request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
    }
    
    private void viewUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String idStr = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
        
        try {
            Long userId = Long.parseLong(idStr);
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isPresent()) {
                request.setAttribute("user", userOpt.get());
                request.getRequestDispatcher("/WEB-INF/views/admin/userDetails.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "User not found with ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }
    
    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String idStr = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
        
        try {
            Long userId = Long.parseLong(idStr);
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isPresent()) {
                request.setAttribute("user", userOpt.get());
                request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "User not found with ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }
    
    private void addUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        // Validate required fields
        if (firstName == null || lastName == null || username == null || 
            email == null || password == null || role == null ||
            firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || 
            email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            
            request.setAttribute("errorMessage", "All fields are required");
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
            return;
        }
        
        // Check if username already exists
        if (userService.findByUsername(username).isPresent()) {
            request.setAttribute("errorMessage", "Username already exists");
            request.getRequestDispatcher("/WEB-INF/views/admin/userForm.jsp").forward(request, response);
            return;
        }
        
        // Create and save the new user
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(PasswordUtil.hashPassword(password));
        newUser.setRole(role);
        
        userService.register(newUser);
        
        // Redirect with success message
        HttpSession session = request.getSession();
        session.setAttribute("successMessage", "User created successfully");
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
    
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }
        
        try {
            Long userId = Long.parseLong(idStr);
            Optional<User> userOpt = userService.findById(userId);
            
            if (!userOpt.isPresent()) {
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "User not found with ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
            
            User user = userOpt.get();
            
            // Update user fields
            user.setFirstName(request.getParameter("firstName"));
            user.setLastName(request.getParameter("lastName"));
            user.setEmail(request.getParameter("email"));
            user.setRole(request.getParameter("role"));
            
            // Update password if provided
            String password = request.getParameter("password");
            if (password != null && !password.isEmpty()) {
                user.setPassword(PasswordUtil.hashPassword(password));
            }
            
            userService.update(user);
            
            // Redirect with success message
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "User updated successfully");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }
    
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String idStr = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
        
        try {
            Long userId = Long.parseLong(idStr);
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isPresent()) {
                userService.deleteById(userId);
                
                // Send success response for AJAX requests
                if (ServletUtil.isAjaxRequest(request)) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":true,\"message\":\"User deleted successfully\"}");
                } else {
                    // Redirect with success message
                    HttpSession session = request.getSession();
                    session.setAttribute("successMessage", "User deleted successfully");
                    response.sendRedirect(request.getContextPath() + "/admin/users");
                }
            } else {
                if (ServletUtil.isAjaxRequest(request)) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"User not found\"}");
                } else {
                    HttpSession session = request.getSession();
                    session.setAttribute("errorMessage", "User not found with ID: " + userId);
                    response.sendRedirect(request.getContextPath() + "/admin/users");
                }
            }
        } catch (NumberFormatException e) {
            if (ServletUtil.isAjaxRequest(request)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Invalid user ID\"}");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
            }
        }
    }
    
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
} 