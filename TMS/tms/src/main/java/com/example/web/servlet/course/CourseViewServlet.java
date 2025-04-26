package com.example.web.servlet.course;

import com.example.model.Course;
import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.TrainingProgramService;
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
 * Servlet for viewing course details
 */
@WebServlet("/course/view")
public class CourseViewServlet extends HttpServlet {

    private CourseService courseService;
    private TrainingProgramService trainingProgramService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        courseService = ServiceFactory.getCourseService();
        trainingProgramService = ServiceFactory.getTrainingProgramService();
        userService = ServiceFactory.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String courseIdStr = request.getParameter("id");
        
        if (courseIdStr == null || courseIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required");
            return;
        }
        
        try {
            Long courseId = Long.parseLong(courseIdStr);
            Optional<Course> courseOpt = courseService.findById(courseId);
            
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                request.setAttribute("course", course);
                
                // Load associated training program
                trainingProgramService.findById(course.getTrainingProgramId()).ifPresent(
                    program -> request.setAttribute("program", program)
                );
                
                // Load assigned trainer if applicable
                if (course.getAssignedTrainerId() != null) {
                    userService.findById(course.getAssignedTrainerId()).ifPresent(
                        trainer -> request.setAttribute("trainer", trainer)
                    );
                }
                
                // Forward to course view page
                request.getRequestDispatcher("/WEB-INF/views/course/view.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
        }
    }
} 