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
import java.util.List;
import java.util.Optional;

/**
 * Servlet for displaying session creation/edit form
 */
@WebServlet("/session/form")
public class SessionFormServlet extends HttpServlet {

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
        String courseIdStr = request.getParameter("courseId");
        
        // Load trainers for assignment
        List<User> trainers = userService.findByRole("TRAINER");
        request.setAttribute("trainers", trainers);
        
        // Editing existing session
        if (sessionIdStr != null && !sessionIdStr.isEmpty()) {
            try {
                Long sessionId = Long.parseLong(sessionIdStr);
                Session session = sessionService.findById(sessionId);
                
                if (session != null) {
                    request.setAttribute("session", session);
                    request.setAttribute("editing", true);
                    
                    // Get related course
                    Optional<Course> course = courseService.findById(session.getCourseId());
                    if (course.isPresent()) {
                        request.setAttribute("course", course.get());
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid session ID");
                return;
            }
        } 
        // Creating a new session for a specific course
        else if (courseIdStr != null && !courseIdStr.isEmpty()) {
            try {
                Long courseId = Long.parseLong(courseIdStr);
                Optional<Course> course = courseService.findById(courseId);
                
                if (course.isPresent()) {
                    request.setAttribute("course", course.get());
                    
                    // Create empty session with course ID set
                    Session newSession = new Session();
                    newSession.setCourseId(courseId);
                    newSession.setSessionStatus("SCHEDULED");
                    
                    // If course has an assigned trainer, pre-select them
                    if (course.get().getAssignedTrainerId() != null) {
                        newSession.setTrainerId(course.get().getAssignedTrainerId());
                    }
                    
                    request.setAttribute("session", newSession);
                    request.setAttribute("editing", false);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
                return;
            }
        } else {
            // Creating a standalone session requires selecting a course first
            List<Course> activeCourses = courseService.findByStatus("ACTIVE");
            request.setAttribute("courses", activeCourses);
            request.setAttribute("selectCourse", true);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/session/form.jsp").forward(request, response);
    }
} 