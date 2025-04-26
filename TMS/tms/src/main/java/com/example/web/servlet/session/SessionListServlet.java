package com.example.web.servlet.session;

import com.example.model.Session;
import com.example.service.SessionService;
import com.example.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for displaying and filtering the list of scheduled sessions
 */
@WebServlet("/sessions")
public class SessionListServlet extends HttpServlet {
    
    private SessionService sessionService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        sessionService = ServiceFactory.getSessionService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get filter parameters
        String courseIdStr = request.getParameter("courseId");
        String trainerIdStr = request.getParameter("trainerId");
        
        List<Session> sessions;
        
        // Apply filters based on parameters
        if (courseIdStr != null && !courseIdStr.isEmpty()) {
            try {
                Long courseId = Long.parseLong(courseIdStr);
                sessions = sessionService.findByCourseId(courseId);
                request.setAttribute("courseId", courseId);
            } catch (NumberFormatException e) {
                sessions = sessionService.findAll();
            }
        } else if (trainerIdStr != null && !trainerIdStr.isEmpty()) {
            try {
                Long trainerId = Long.parseLong(trainerIdStr);
                sessions = sessionService.findByTrainerId(trainerId);
                request.setAttribute("trainerId", trainerId);
            } catch (NumberFormatException e) {
                sessions = sessionService.findAll();
            }
        } else {
            sessions = sessionService.findAll();
        }
        
        request.setAttribute("sessions", sessions);
        request.getRequestDispatcher("/WEB-INF/views/session/list.jsp").forward(request, response);
    }
} 