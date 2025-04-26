package com.example.web.servlet.program;

import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.service.TrainingProgramService;
import com.example.service.impl.TrainingProgramServiceImpl;
import com.example.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Servlet controller for training program CRUD operations
 */
@WebServlet("/program/*")
public class TrainingProgramControllerServlet extends HttpServlet {
    
    private final TrainingProgramService trainingProgramService;
    
    public TrainingProgramControllerServlet() {
        this.trainingProgramService = new TrainingProgramServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirect to list page
            response.sendRedirect(request.getContextPath() + "/programs");
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
                viewProgram(Long.parseLong(pathParts[2]), request, response);
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
                deleteProgram(Long.parseLong(pathParts[2]), request, response);
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
                createProgram(request, response);
                break;
                
            case "edit":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                updateProgram(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "status":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                updateProgramStatus(Long.parseLong(pathParts[2]), request, response);
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void viewProgram(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<TrainingProgram> optionalProgram = trainingProgramService.findById(id);
        
        if (optionalProgram.isPresent()) {
            request.setAttribute("program", optionalProgram.get());
            request.getRequestDispatcher("/WEB-INF/views/program/view.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training program not found");
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
    }
    
    private void showEditForm(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<TrainingProgram> optionalProgram = trainingProgramService.findById(id);
        
        if (optionalProgram.isPresent()) {
            request.setAttribute("program", optionalProgram.get());
            request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training program not found");
        }
    }
    
    private void createProgram(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        TrainingProgram program = new TrainingProgram();
        program.setCreatedBy(currentUser.getId());
        
        if (populateProgramFromRequest(program, request, response)) {
            try {
                trainingProgramService.create(program);
                request.getSession().setAttribute("successMessage", "Training program created successfully!");
                response.sendRedirect(request.getContextPath() + "/programs");
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", e.getMessage());
                request.setAttribute("program", program);
                request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
            }
        }
    }
    
    private void updateProgram(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<TrainingProgram> optionalProgram = trainingProgramService.findById(id);
        
        if (optionalProgram.isPresent()) {
            TrainingProgram program = optionalProgram.get();
            
            if (populateProgramFromRequest(program, request, response)) {
                try {
                    trainingProgramService.update(program);
                    request.getSession().setAttribute("successMessage", "Training program updated successfully!");
                    response.sendRedirect(request.getContextPath() + "/programs");
                } catch (IllegalArgumentException e) {
                    request.setAttribute("errorMessage", e.getMessage());
                    request.setAttribute("program", program);
                    request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training program not found");
        }
    }
    
    private void updateProgramStatus(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<TrainingProgram> optionalProgram = trainingProgramService.findById(id);
        String status = request.getParameter("status");
        
        if (optionalProgram.isPresent() && status != null && !status.isEmpty()) {
            TrainingProgram program = optionalProgram.get();
            program.setStatus(status);
            
            try {
                trainingProgramService.update(program);
                request.getSession().setAttribute("successMessage", "Training program status updated to " + status);
                
                // Check if this is an AJAX request
                if (ServletUtil.isAjaxRequest(request)) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":true,\"message\":\"Status updated successfully\"}");
                } else {
                    response.sendRedirect(request.getContextPath() + "/programs");
                }
            } catch (IllegalArgumentException e) {
                if (ServletUtil.isAjaxRequest(request)) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
                } else {
                    request.getSession().setAttribute("errorMessage", e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/programs");
                }
            }
        } else {
            if (ServletUtil.isAjaxRequest(request)) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Program not found or invalid status\"}");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training program not found or invalid status");
            }
        }
    }
    
    private void deleteProgram(Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            boolean deleted = trainingProgramService.deleteById(id);
            
            if (deleted) {
                request.getSession().setAttribute("successMessage", "Training program deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete training program. It may have associated courses or enrollments.");
            }
            
            response.sendRedirect(request.getContextPath() + "/programs");
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/programs");
        }
    }
    
    private boolean populateProgramFromRequest(TrainingProgram program, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        program.setTitle(request.getParameter("title"));
        program.setDescription(request.getParameter("description"));
        program.setStatus(request.getParameter("status"));
        
        // Parse start date
        String startDateStr = request.getParameter("startDate");
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Start date is required");
            request.setAttribute("program", program);
            request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
            return false;
        }
        
        // Parse end date
        String endDateStr = request.getParameter("endDate");
        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "End date is required");
            request.setAttribute("program", program);
            request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
            return false;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);
            
            // Validate dates
            if (endDate.isBefore(startDate)) {
                request.setAttribute("errorMessage", "End date cannot be before start date");
                request.setAttribute("program", program);
                request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
                return false;
            }
            
            program.setStartDate(startDate);
            program.setEndDate(endDate);
            
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid date format. Please use yyyy-MM-dd format.");
            request.setAttribute("program", program);
            request.getRequestDispatcher("/WEB-INF/views/program/form.jsp").forward(request, response);
            return false;
        }
        
        return true;
    }
} 