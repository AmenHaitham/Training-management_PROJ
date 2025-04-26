package com.example.web.servlet;

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
 * Servlet for the home page
 */
@WebServlet("")
public class IndexServlet extends HttpServlet {
    
    private final TrainingProgramService trainingProgramService = new TrainingProgramServiceImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get active training programs
        List<TrainingProgram> activePrograms = trainingProgramService.findActivePrograms();
        
        // Set attributes
        request.setAttribute("activePrograms", activePrograms);
        
        // Forward to the JSP page
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
} 