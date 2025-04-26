package com.example.web.servlet.admin;

import com.example.model.TrainingProgram;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet for the Admin Dashboard
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    
    private final UserService userService = new UserServiceImpl();
    private final TrainingProgramService trainingProgramService = new TrainingProgramServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get dashboard statistics
        
        // User statistics
        List<User> allUsers = userService.findAll();
        int totalUsers = allUsers.size();
        
        // Count users by role
        int adminCount = 0;
        int trainerCount = 0;
        int traineeCount = 0;
        
        for (User user : allUsers) {
            switch (user.getRole()) {
                case "ADMIN":
                    adminCount++;
                    break;
                case "TRAINER":
                    trainerCount++;
                    break;
                case "TRAINEE":
                    traineeCount++;
                    break;
            }
        }
        
        // Training program statistics
        List<TrainingProgram> allPrograms = trainingProgramService.findAll();
        int totalPrograms = allPrograms.size();
        
        // Count programs by status
        int activePrograms = 0;
        int draftPrograms = 0;
        int completedPrograms = 0;
        int cancelledPrograms = 0;
        
        for (TrainingProgram program : allPrograms) {
            switch (program.getStatus()) {
                case "ACTIVE":
                    activePrograms++;
                    break;
                case "DRAFT":
                    draftPrograms++;
                    break;
                case "COMPLETED":
                    completedPrograms++;
                    break;
                case "CANCELLED":
                    cancelledPrograms++;
                    break;
            }
        }
        
        // Enrollment statistics
        int totalEnrollments = enrollmentService.findAll().size();
        
        // Count enrollments by status
        int pendingEnrollments = enrollmentService.findByStatus("PENDING").size();
        int approvedEnrollments = enrollmentService.findByStatus("APPROVED").size();
        int completedEnrollments = enrollmentService.findByStatus("COMPLETED").size();
        int droppedEnrollments = enrollmentService.findByStatus("DROPPED").size();
        
        // Recent users (5 most recent) - Assuming users have an ID that increases with newer records
        List<User> recentUsers = allUsers.stream()
                .sorted(Comparator.comparing(User::getId).reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        // Recent training programs (5 most recent) - Assuming programs have an ID that increases with newer records
        List<TrainingProgram> recentPrograms = allPrograms.stream()
                .sorted(Comparator.comparing(TrainingProgram::getId).reversed())
                .limit(5)
                .collect(Collectors.toList());
        
        // Set attributes
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("adminCount", adminCount);
        request.setAttribute("trainerCount", trainerCount);
        request.setAttribute("traineeCount", traineeCount);
        
        request.setAttribute("totalPrograms", totalPrograms);
        request.setAttribute("activePrograms", activePrograms);
        request.setAttribute("draftPrograms", draftPrograms);
        request.setAttribute("completedPrograms", completedPrograms);
        request.setAttribute("cancelledPrograms", cancelledPrograms);
        
        request.setAttribute("totalEnrollments", totalEnrollments);
        request.setAttribute("pendingEnrollments", pendingEnrollments);
        request.setAttribute("approvedEnrollments", approvedEnrollments);
        request.setAttribute("completedEnrollments", completedEnrollments);
        request.setAttribute("droppedEnrollments", droppedEnrollments);
        
        request.setAttribute("recentUsers", recentUsers);
        request.setAttribute("recentPrograms", recentPrograms);
        
        // Forward to the dashboard JSP
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
} 