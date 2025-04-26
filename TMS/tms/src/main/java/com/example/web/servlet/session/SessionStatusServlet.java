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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Servlet to handle session status changes
 */
@WebServlet("/session/status/*")
public class SessionStatusServlet extends HttpServlet {
    
    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        sessionService = ServiceFactory.getSessionService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract session ID from the URL path
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
            return;
        }
        
        // Remove leading slash and parse to Long
        String sessionIdStr = pathInfo.substring(1);
        Long sessionId;
        try {
            sessionId = Long.parseLong(sessionIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID format");
            return;
        }
        
        // Get the new status from request parameters
        String newStatus = request.getParameter("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Status parameter is required");
            return;
        }
        
        // Validate status value
        if (!isValidStatus(newStatus)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status value");
            return;
        }
        
        // Find the session by ID
        Session session = sessionService.findById(sessionId);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }
        
        // Check if the status transition is valid
        if (!isValidStatusTransition(session.getSessionStatus(), newStatus)) {
            request.getSession().setAttribute("errorMessage", "Invalid status transition from " + 
                session.getSessionStatus() + " to " + newStatus);
            response.sendRedirect(request.getContextPath() + "/session/view?id=" + sessionId);
            return;
        }
        
        // Update the session status
        session.setSessionStatus(newStatus);
        try {
            sessionService.update(session);
            request.getSession().setAttribute("successMessage", "Session status updated to " + newStatus);
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Failed to update session status: " + e.getMessage());
        }
        
        // Redirect back to the session view page
        response.sendRedirect(request.getContextPath() + "/session/view?id=" + sessionId);
    }
    
    /**
     * Checks if the given status value is valid
     */
    private boolean isValidStatus(String status) {
        Set<String> validStatuses = new HashSet<>(Arrays.asList(
            "SCHEDULED", "ONGOING", "COMPLETED", "CANCELLED"));
        return validStatuses.contains(status);
    }
    
    /**
     * Checks if the status transition is valid
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return true; // No change
        }
        
        switch (currentStatus) {
            case "SCHEDULED":
                // Scheduled can transition to any other state
                return true;
            case "ONGOING":
                // Ongoing can only transition to Completed or Cancelled
                return newStatus.equals("COMPLETED") || newStatus.equals("CANCELLED");
            case "COMPLETED":
            case "CANCELLED":
                // Completed and Cancelled are terminal states
                return false;
            default:
                return false;
        }
    }
} 