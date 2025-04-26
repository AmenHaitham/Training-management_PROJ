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
import java.util.List;
import java.util.Optional;

/**
 * Servlet for displaying course creation/edit form
 */
@WebServlet("/course/form")
public class CourseFormServlet extends HttpServlet {

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
        String programIdStr = request.getParameter("programId");
        
        // Load trainers for assignment
        List<User> trainers = userService.findByRole("TRAINER");
        request.setAttribute("trainers", trainers);
        
        // Editing existing course
        if (courseIdStr != null && !courseIdStr.isEmpty()) {
            try {
                Long courseId = Long.parseLong(courseIdStr);
                Optional<Course> courseOpt = courseService.findById(courseId);
                
                if (courseOpt.isPresent()) {
                    request.setAttribute("course", courseOpt.get());
                    request.setAttribute("editing", true);
                    
                    // Get program for breadcrumbs
                    Long programId = courseOpt.get().getTrainingProgramId();
                    trainingProgramService.findById(programId).ifPresent(
                        program -> request.setAttribute("program", program)
                    );
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
                return;
            }
        } 
        // Creating a new course within a program
        else if (programIdStr != null && !programIdStr.isEmpty()) {
            try {
                Long programId = Long.parseLong(programIdStr);
                Optional<TrainingProgram> programOpt = trainingProgramService.findById(programId);
                
                if (programOpt.isPresent()) {
                    TrainingProgram program = programOpt.get();
                    request.setAttribute("program", program);
                    
                    // Create empty course with program ID set
                    Course newCourse = new Course();
                    newCourse.setTrainingProgramId(programId);
                    newCourse.setStatus("DRAFT");
                    
                    request.setAttribute("course", newCourse);
                    request.setAttribute("editing", false);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training program not found");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid program ID");
                return;
            }
        } else {
            // Creating a standalone course requires selecting a program first
            List<TrainingProgram> programs = trainingProgramService.findAll();
            request.setAttribute("programs", programs);
            request.setAttribute("selectProgram", true);
        }
        
        request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
    }
} 