package com.example.web.servlet;

import com.example.model.User;
import com.example.service.EnrollmentService;
import com.example.service.TrainingProgramService;
import com.example.service.UserService;
import com.example.service.impl.EnrollmentServiceImpl;
import com.example.service.impl.TrainingProgramServiceImpl;
import com.example.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for user profile page
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    
    private final UserService userService = new UserServiceImpl();
    private final TrainingProgramService trainingProgramService = new TrainingProgramServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current user from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            // User not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        String role = currentUser.getRole();
        
        // Set attributes based on role
        if ("TRAINER".equals(role)) {
            // Get programs associated with this trainer (assuming this method exists)
            // If findByCreator doesn't exist, we'll use findAll and filter for now
            request.setAttribute("createdPrograms", trainingProgramService.findAll().stream()
                    .filter(program -> program.getCreatedBy() != null && 
                             program.getCreatedBy().equals(currentUser.getId()))
                    .collect(java.util.stream.Collectors.toList()));
            
            // Additional trainer-specific information can be added here
        } else if ("TRAINEE".equals(role)) {
            // Get enrollments for this trainee
            request.setAttribute("enrollments", enrollmentService.findByTrainee(currentUser.getId()));
            
            // Additional trainee-specific information can be added here
        }
        
        // Set user in request
        request.setAttribute("user", currentUser);
        
        // Forward to appropriate profile page based on role
        String jspPath;
        
        if ("ADMIN".equals(role)) {
            jspPath = "/WEB-INF/views/profile/adminProfile.jsp";
        } else if ("TRAINER".equals(role)) {
            jspPath = "/WEB-INF/views/profile/trainerProfile.jsp";
        } else {
            jspPath = "/WEB-INF/views/profile/traineeProfile.jsp";
        }
        
        request.getRequestDispatcher(jspPath).forward(request, response);
    }
} 