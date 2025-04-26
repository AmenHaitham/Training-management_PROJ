package com.example.web.servlet.auth;

import com.example.model.User;
import com.example.service.UserService;
import com.example.service.impl.UserServiceImpl;
import com.example.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet responsible for handling user login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private final UserService userService = new UserServiceImpl();
    
    /**
     * Handles GET requests to display the login form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            // User is already logged in, redirect to home page
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        // Forward to login page
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }
    
    /**
     * Handles POST requests to process login form submission
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validate input
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Authenticate user
        boolean authenticated = AuthUtil.authenticate(username, password);
        
        if (authenticated) {
            // Get authenticated user
            Optional<User> user = AuthUtil.getCurrentUser();
            if (user.isPresent()) {
                // Store user in session
                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", user.get());
                
                // Redirect based on role
                String role = user.get().getRole();
                if ("ADMIN".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else if ("TRAINER".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/trainer/dashboard");
                } else if ("TRAINEE".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/trainee/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/");
                }
            } else {
                // Something went wrong with authentication
                request.setAttribute("errorMessage", "Authentication error");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
        } else {
            // Authentication failed
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
} 