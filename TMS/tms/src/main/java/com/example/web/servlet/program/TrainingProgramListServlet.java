package com.example.web.servlet.program;

import com.example.model.TrainingProgram;
import com.example.service.TrainingProgramService;
import com.example.service.impl.TrainingProgramServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for listing training programs
 */
@WebServlet("/training-programs")
public class TrainingProgramListServlet extends HttpServlet {
    
    private final TrainingProgramService trainingProgramService = new TrainingProgramServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get filter parameters
        String status = request.getParameter("status");
        String title = request.getParameter("title");
        
        List<TrainingProgram> programs;
        
        // Apply filters
        if (status != null && !status.isEmpty()) {
            programs = trainingProgramService.findByStatus(status);
        } else if (title != null && !title.isEmpty()) {
            programs = trainingProgramService.findByTitle(title);
        } else {
            programs = trainingProgramService.findAll();
        }
        
        // Set attributes
        request.setAttribute("programs", programs);
        
        // Forward to the JSP page
        request.getRequestDispatcher("/WEB-INF/views/program/list.jsp").forward(request, response);
    }
} 