package com.example.web.servlet.enrollment;

import com.example.model.Enrollment;
import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.service.EnrollmentService;
import com.example.service.TrainingProgramService;
import com.example.service.UserService;
import com.example.service.impl.EnrollmentServiceImpl;
import com.example.service.impl.TrainingProgramServiceImpl;
import com.example.service.impl.UserServiceImpl;
import com.example.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet controller for enrollment operations
 */
@WebServlet("/enrollment/*")
public class EnrollmentServlet extends HttpServlet {
    
    private final EnrollmentService enrollmentService;
    private final TrainingProgramService programService;
    private final UserService userService;
    
    public EnrollmentServlet() {
        this.enrollmentService = new EnrollmentServiceImpl();
        this.programService = new TrainingProgramServiceImpl();
        this.userService = new UserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Show all enrollments
            listEnrollments(request, response);
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
                viewEnrollment(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "my":
                // Show current user's enrollments
                listMyEnrollments(request, response);
                break;
                
            case "enroll":
                // Show enrollment form
                showEnrollForm(request, response);
                break;
                
            case "program":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                // Show enrollments for a specific program
                listProgramEnrollments(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "delete":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                deleteEnrollment(Long.parseLong(pathParts[2]), request, response);
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
            case "enroll":
                // Process enrollment submission
                processEnrollment(request, response);
                break;
                
            case "status":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                // Update enrollment status
                updateEnrollmentStatus(Long.parseLong(pathParts[2]), request, response);
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void listEnrollments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        if (!AuthUtil.isAuthenticated() || !AuthUtil.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        // Get status filter
        String statusFilter = request.getParameter("status");
        
        List<Enrollment> enrollments;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            enrollments = enrollmentService.findByStatus(statusFilter);
        } else {
            enrollments = enrollmentService.findAll();
        }
        
        request.setAttribute("enrollments", enrollments);
        request.setAttribute("statusFilter", statusFilter);
        request.getRequestDispatcher("/WEB-INF/views/enrollment/list.jsp").forward(request, response);
    }
    
    private void listMyEnrollments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get current user
        Optional<User> currentUserOpt = AuthUtil.getCurrentUser();
        if (!currentUserOpt.isPresent()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User currentUser = currentUserOpt.get();
        
        List<Enrollment> enrollments = enrollmentService.findByTrainee(currentUser.getId());
        
        request.setAttribute("enrollments", enrollments);
        request.getRequestDispatcher("/WEB-INF/views/enrollment/my-enrollments.jsp").forward(request, response);
    }
    
    private void listProgramEnrollments(Long programId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin or trainer
        if (!AuthUtil.isAuthenticated() || (!AuthUtil.isAdmin() && !AuthUtil.isTrainer())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        // Get the program
        Optional<TrainingProgram> programOpt = programService.findById(programId);
        if (!programOpt.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training program not found");
            return;
        }
        
        TrainingProgram program = programOpt.get();
        
        // Get enrollments for the program
        List<Enrollment> enrollments = enrollmentService.findByTrainingProgram(programId);
        
        request.setAttribute("program", program);
        request.setAttribute("enrollments", enrollments);
        request.getRequestDispatcher("/WEB-INF/views/enrollment/program-enrollments.jsp").forward(request, response);
    }
    
    private void viewEnrollment(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Enrollment> enrollmentOpt = enrollmentService.findById(id);
        
        if (!enrollmentOpt.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Enrollment not found");
            return;
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        
        // Check if user has permission to view this enrollment
        Optional<User> currentUserOpt = AuthUtil.getCurrentUser();
        if (!currentUserOpt.isPresent() || 
            (!AuthUtil.isAdmin() && 
             !AuthUtil.isTrainer() && 
             !currentUserOpt.get().getId().equals(enrollment.getTraineeId()))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        request.setAttribute("enrollment", enrollment);
        request.getRequestDispatcher("/WEB-INF/views/enrollment/view.jsp").forward(request, response);
    }
    
    private void showEnrollForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is a trainee
        Optional<User> currentUserOpt = AuthUtil.getCurrentUser();
        if (!currentUserOpt.isPresent()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User currentUser = currentUserOpt.get();
        
        if (!AuthUtil.isTrainee()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only trainees can enroll in training programs");
            return;
        }
        
        // Get program ID from parameter
        String programIdParam = request.getParameter("programId");
        Long programId = null;
        TrainingProgram program = null;
        
        if (programIdParam != null && !programIdParam.isEmpty()) {
            try {
                programId = Long.parseLong(programIdParam);
                Optional<TrainingProgram> programOpt = programService.findById(programId);
                
                if (programOpt.isPresent()) {
                    program = programOpt.get();
                    
                    // Check if program is active
                    if (!"ACTIVE".equals(program.getStatus())) {
                        request.setAttribute("errorMessage", "This training program is not currently accepting enrollments");
                    }
                    
                    // Check if user is already enrolled
                    if (enrollmentService.isEnrolled(currentUser.getId(), programId)) {
                        request.setAttribute("errorMessage", "You are already enrolled in this training program");
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid program ID
            }
        }
        
        // If no specific program was requested or found, get all active programs
        if (program == null) {
            List<TrainingProgram> activePrograms = programService.findByStatus("ACTIVE");
            request.setAttribute("programs", activePrograms);
        } else {
            request.setAttribute("program", program);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/enrollment/form.jsp").forward(request, response);
    }
    
    private void processEnrollment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is a trainee
        Optional<User> currentUserOpt = AuthUtil.getCurrentUser();
        if (!currentUserOpt.isPresent()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User currentUser = currentUserOpt.get();
        
        if (!AuthUtil.isTrainee()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only trainees can enroll in training programs");
            return;
        }
        
        // Get program ID
        String programIdParam = request.getParameter("programId");
        if (programIdParam == null || programIdParam.isEmpty()) {
            request.setAttribute("errorMessage", "Program ID is required");
            showEnrollForm(request, response);
            return;
        }
        
        Long programId;
        try {
            programId = Long.parseLong(programIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid program ID");
            showEnrollForm(request, response);
            return;
        }
        
        try {
            // Attempt to enroll the user
            Enrollment enrollment = enrollmentService.enroll(currentUser.getId(), programId);
            
            // Set success message and redirect
            request.getSession().setAttribute("successMessage", "You have successfully enrolled in the training program!");
            response.sendRedirect(request.getContextPath() + "/enrollment/my");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            showEnrollForm(request, response);
        }
    }
    
    private void updateEnrollmentStatus(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin or trainer
        if (!AuthUtil.isAuthenticated() || (!AuthUtil.isAdmin() && !AuthUtil.isTrainer())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        // Get enrollment
        Optional<Enrollment> enrollmentOpt = enrollmentService.findById(id);
        if (!enrollmentOpt.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Enrollment not found");
            return;
        }
        
        // Get new status
        String newStatus = request.getParameter("status");
        if (newStatus == null || newStatus.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Status is required");
            return;
        }
        
        try {
            Enrollment enrollment;
            
            // Handle special statuses
            if ("COMPLETED".equals(newStatus)) {
                enrollment = enrollmentService.complete(id);
            } else if ("DROPPED".equals(newStatus)) {
                enrollment = enrollmentService.drop(id);
            } else {
                // Generic status update
                enrollment = enrollmentService.updateStatus(id, newStatus);
            }
            
            // Determine redirect based on HTTP referer
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                response.sendRedirect(referer);
            } else if (enrollment.getTrainingProgramId() != null) {
                // Redirect to program enrollments
                response.sendRedirect(request.getContextPath() + "/enrollment/program/" + enrollment.getTrainingProgramId());
            } else {
                // Redirect to all enrollments
                response.sendRedirect(request.getContextPath() + "/enrollment/");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            viewEnrollment(id, request, response);
        }
    }
    
    private void deleteEnrollment(Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Check if user is admin
        if (!AuthUtil.isAuthenticated() || !AuthUtil.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        // Get enrollment for program ID before deletion
        Optional<Enrollment> enrollmentOpt = enrollmentService.findById(id);
        Long programId = null;
        
        if (enrollmentOpt.isPresent()) {
            programId = enrollmentOpt.get().getTrainingProgramId();
        }
        
        // Try to delete the enrollment
        boolean deleted = enrollmentService.drop(id) != null;
        
        // Set message based on result
        if (deleted) {
            request.getSession().setAttribute("successMessage", "Enrollment deleted successfully");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to delete enrollment");
        }
        
        // Determine redirect
        if (programId != null) {
            // Redirect to program enrollments
            response.sendRedirect(request.getContextPath() + "/enrollment/program/" + programId);
        } else {
            // Redirect to all enrollments
            response.sendRedirect(request.getContextPath() + "/enrollment/");
        }
    }
} 