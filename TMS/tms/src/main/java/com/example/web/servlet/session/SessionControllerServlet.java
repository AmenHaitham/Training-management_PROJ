package com.example.web.servlet.session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.model.Session;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.SessionService;
import com.example.util.ServiceFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet controller for session CRUD operations
 */
@WebServlet("/session/*")
public class SessionControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SessionService sessionService;
    private CourseService courseService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        sessionService = ServiceFactory.getSessionService();
        courseService = ServiceFactory.getCourseService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Default - redirect to courses
            response.sendRedirect(request.getContextPath() + "/course/list");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        String action = pathParts[1];
        
        try {
            switch (action) {
                case "view":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    viewSession(request, response, Long.parseLong(pathParts[2]));
                    break;
                case "create":
                    createSessionForm(request, response);
                    break;
                case "edit":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    editSessionForm(request, response, Long.parseLong(pathParts[2]));
                    break;
                case "delete":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    handleDeleteRequest(request, response, Long.parseLong(pathParts[2]));
                    break;
                case "attendance":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    manageAttendance(request, response, Long.parseLong(pathParts[2]));
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action is required");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        String action = pathParts[1];
        
        try {
            switch (action) {
                case "create":
                    createSession(request, response);
                    break;
                case "update":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    updateSession(Long.parseLong(pathParts[2]), request, response);
                    break;
                case "delete":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    deleteSession(Long.parseLong(pathParts[2]), request, response);
                    break;
                case "status":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    updateSessionStatus(Long.parseLong(pathParts[2]), request, response);
                    break;
                case "saveAttendance":
                    if (pathParts.length < 3) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
                        return;
                    }
                    saveAttendance(request, response, Long.parseLong(pathParts[2]));
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private void viewSession(HttpServletRequest request, HttpServletResponse response, long sessionId) 
            throws ServletException, IOException {
        Session session = sessionService.findById(sessionId);
        
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }
        
        request.setAttribute("session", session);
        request.getRequestDispatcher("/WEB-INF/views/session/view.jsp").forward(request, response);
    }
    
    private void createSessionForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if courseId is provided
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            Long courseId = Long.parseLong(courseIdParam);
            request.setAttribute("course", courseService.findById(courseId).orElse(null));
        }
        
        request.setAttribute("session", new Session());
        request.getRequestDispatcher("/WEB-INF/views/session/form.jsp").forward(request, response);
    }
    
    private void editSessionForm(HttpServletRequest request, HttpServletResponse response, long sessionId) 
            throws ServletException, IOException {
        Session session = sessionService.findById(sessionId);
        
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }
        
        request.setAttribute("session", session);
        request.setAttribute("course", courseService.findById(session.getCourseId()).orElse(null));
        request.getRequestDispatcher("/WEB-INF/views/session/form.jsp").forward(request, response);
    }
    
    private void createSession(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Create a new session from request parameters
            Session session = new Session();
            populateSessionFromRequest(session, request);
            
            // Save the session
            Session savedSession = sessionService.save(session);
            
            if (savedSession != null) {
                // Set success message
                request.getSession().setAttribute("successMessage", "Session created successfully!");
                
                // Redirect to session view
                response.sendRedirect(request.getContextPath() + "/session/view?id=" + savedSession.getId());
            } else {
                // Set error message
                request.getSession().setAttribute("errorMessage", "Failed to create session");
                
                // Redirect back to form
                response.sendRedirect(request.getContextPath() + "/session/form?courseId=" + session.getCourseId());
            }
        } catch (Exception e) {
            // Set error message
            request.getSession().setAttribute("errorMessage", "Error creating session: " + e.getMessage());
            
            // Redirect back to form
            response.sendRedirect(request.getContextPath() + "/session/form");
        }
    }
    
    private void updateSession(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get existing session
            Session session = sessionService.findById(id);
            
            if (session == null) {
                // Set error message
                request.getSession().setAttribute("errorMessage", "Session not found");
                
                // Redirect to sessions list
                response.sendRedirect(request.getContextPath() + "/sessions");
                return;
            }
            
            // Update session from request parameters
            populateSessionFromRequest(session, request);
            
            // Save the updated session
            Session updatedSession = sessionService.update(session);
            
            if (updatedSession != null) {
                // Set success message
                request.getSession().setAttribute("successMessage", "Session updated successfully!");
                
                // Redirect to session view
                response.sendRedirect(request.getContextPath() + "/session/view?id=" + updatedSession.getId());
            } else {
                // Set error message
                request.getSession().setAttribute("errorMessage", "Failed to update session");
                
                // Redirect back to form
                response.sendRedirect(request.getContextPath() + "/session/form?id=" + id);
            }
        } catch (Exception e) {
            // Set error message
            request.getSession().setAttribute("errorMessage", "Error updating session: " + e.getMessage());
            
            // Redirect back to form
            response.sendRedirect(request.getContextPath() + "/session/form?id=" + id);
        }
    }
    
    // Method called from doPost
    private void deleteSession(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Delete the session
            boolean deleted = sessionService.delete(id);
            
            if (deleted) {
                // Set success message
                request.getSession().setAttribute("successMessage", "Session deleted successfully!");
            } else {
                // Set error message
                request.getSession().setAttribute("errorMessage", "Failed to delete session");
            }
        } catch (Exception e) {
            // Set error message
            request.getSession().setAttribute("errorMessage", "Error deleting session: " + e.getMessage());
        }
        
        // Redirect to sessions list
        response.sendRedirect(request.getContextPath() + "/sessions");
    }
    
    // Method called from doGet
    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response, long sessionId) 
            throws ServletException, IOException {
        try {
            boolean deleted = sessionService.delete(sessionId);
            if (deleted) {
                request.getSession().setAttribute("successMessage", "Session deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete session");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error deleting session: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/sessions");
    }
    
    private void updateSessionStatus(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get existing session
            Session session = sessionService.findById(id);
            
            if (session == null) {
                // Set error message
                request.getSession().setAttribute("errorMessage", "Session not found");
                
                // Redirect to sessions list
                response.sendRedirect(request.getContextPath() + "/sessions");
                return;
            }
            
            // Update session status
            String status = request.getParameter("status");
            if (status != null && !status.isEmpty()) {
                session.setSessionStatus(status);
                
                // Save the updated session
                Session updatedSession = sessionService.update(session);
                
                if (updatedSession != null) {
                    // Set success message
                    request.getSession().setAttribute("successMessage", "Session status updated to " + status);
                } else {
                    // Set error message
                    request.getSession().setAttribute("errorMessage", "Failed to update session status");
                }
            } else {
                // Set error message
                request.getSession().setAttribute("errorMessage", "Status parameter is required");
            }
        } catch (Exception e) {
            // Set error message
            request.getSession().setAttribute("errorMessage", "Error updating session status: " + e.getMessage());
        }
        
        // Redirect to session view
        response.sendRedirect(request.getContextPath() + "/session/view?id=" + id);
    }
    
    private void manageAttendance(HttpServletRequest request, HttpServletResponse response, long sessionId) 
            throws ServletException, IOException {
        // Get the current user from the session
        HttpSession httpSession = request.getSession();
        User currentUser = (User) httpSession.getAttribute("currentUser");
        
        // Validate user permissions
        if (currentUser == null || (!currentUser.getRole().equals("ADMIN") && !currentUser.getRole().equals("TRAINER"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to manage attendance");
            return;
        }
        
        // Get session details
        Session session = sessionService.findById(sessionId);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }
        
        // Get attendees
        request.setAttribute("session", session);
        // These methods need to be implemented in SessionService
        // For now, we'll set empty attributes
        request.setAttribute("enrollments", new HashMap<>());
        request.setAttribute("attendance", new HashMap<>());
        
        request.getRequestDispatcher("/WEB-INF/views/session/attendance.jsp").forward(request, response);
    }
    
    private void saveAttendance(HttpServletRequest request, HttpServletResponse response, long sessionId) 
            throws ServletException, IOException {
        // Get the current user from the session
        HttpSession httpSession = request.getSession();
        User currentUser = (User) httpSession.getAttribute("currentUser");
        
        // Validate user permissions
        if (currentUser == null || (!currentUser.getRole().equals("ADMIN") && !currentUser.getRole().equals("TRAINER"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to save attendance");
            return;
        }
        
        // Get the session
        Session session = sessionService.findById(sessionId);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }
        
        // Process attendance data
        Map<Long, Boolean> attendanceData = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        
        for (String paramName : parameterMap.keySet()) {
            if (paramName.startsWith("attendance_")) {
                String enrollmentIdStr = paramName.substring("attendance_".length());
                Long enrollmentId = Long.parseLong(enrollmentIdStr);
                boolean present = "true".equalsIgnoreCase(request.getParameter(paramName));
                attendanceData.put(enrollmentId, present);
            }
        }
        
        // Save attendance method needs to be implemented in SessionService
        // For now, we'll just redirect back
        
        // Redirect back to the attendance page
        response.sendRedirect(request.getContextPath() + "/session/attendance/" + sessionId);
    }
    
    private void populateSessionFromRequest(Session session, HttpServletRequest request) throws ServletException {
        // Get parameters
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String sessionDate = request.getParameter("sessionDate");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String courseId = request.getParameter("courseId");
        String trainerId = request.getParameter("trainerId");
        String location = request.getParameter("location");
        String materials = request.getParameter("materials");
        String sessionStatus = request.getParameter("sessionStatus");
        
        // Validate required fields
        if (title == null || title.isEmpty() || 
            sessionDate == null || sessionDate.isEmpty() || 
            startTime == null || startTime.isEmpty() || 
            endTime == null || endTime.isEmpty() || 
            courseId == null || courseId.isEmpty()) {
            throw new ServletException("Required fields missing");
        }
        
        // Set fields
        session.setTitle(title);
        session.setDescription(description);
        
        try {
            // Parse and set date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(sessionDate);
            session.setSessionDate(new Date(parsedDate.getTime()));
            
            // Parse and set times
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            java.util.Date parsedStartTime = timeFormat.parse(startTime);
            java.util.Date parsedEndTime = timeFormat.parse(endTime);
            
            session.setStartTime(new Time(parsedStartTime.getTime()));
            session.setEndTime(new Time(parsedEndTime.getTime()));
        } catch (ParseException e) {
            throw new ServletException("Invalid date or time format", e);
        }
        
        // Set numeric fields
        try {
            session.setCourseId(Long.parseLong(courseId));
            
            if (trainerId != null && !trainerId.isEmpty()) {
                session.setTrainerId(Long.parseLong(trainerId));
            }
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid numeric format", e);
        }
        
        // Set other fields
        session.setLocation(location);
        session.setMaterials(materials);
        
        if (sessionStatus != null && !sessionStatus.isEmpty()) {
            session.setSessionStatus(sessionStatus);
        } else {
            session.setSessionStatus("SCHEDULED"); // Default status
        }
    }
} 