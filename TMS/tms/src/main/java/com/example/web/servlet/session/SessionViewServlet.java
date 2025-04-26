package com.example.web.servlet.session;

import com.example.model.Course;
import com.example.model.Session;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.SessionService;
import com.example.service.UserService;
import com.example.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet for viewing session details
 */
@WebServlet("/session/view")
public class SessionViewServlet extends HttpServlet {

    private SessionService sessionService;
    private CourseService courseService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        sessionService = ServiceFactory.getSessionService();
        courseService = ServiceFactory.getCourseService();
        userService = ServiceFactory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionIdStr = request.getParameter("id");
        
        if (sessionIdStr == null || sessionIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is required");
            return;
        }
        
        try {
            Long sessionId = Long.parseLong(sessionIdStr);
            Session session = sessionService.findById(sessionId);
            
            if (session != null) {
                request.setAttribute("session", session);
                
                // Load associated course
                Optional<Course> course = courseService.findById(session.getCourseId());
                if (course.isPresent()) {
                    request.setAttribute("course", course.get());
                }
                
                // Load trainer information if assigned
                if (session.getTrainerId() != null) {
                    Optional<User> trainer = userService.findById(session.getTrainerId());
                    if (trainer.isPresent()) {
                        request.setAttribute("trainer", trainer.get());
                    }
                }
                
                // Forward to session view page
                request.getRequestDispatcher("/WEB-INF/views/session/view.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
        }
    }
} 