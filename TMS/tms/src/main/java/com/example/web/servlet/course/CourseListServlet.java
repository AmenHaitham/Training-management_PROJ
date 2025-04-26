package com.example.web.servlet.course;

import com.example.model.Course;
import com.example.service.CourseService;
import com.example.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet that handles listing courses with filters
 */
@WebServlet("/courses")
public class CourseListServlet extends HttpServlet {

    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        super.init();
        courseService = ServiceFactory.getCourseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get filter parameters
        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String status = request.getParameter("status");
        String programIdStr = request.getParameter("programId");
        String trainerIdStr = request.getParameter("trainerId");
        
        List<Course> courses;
        
        // Apply filters based on parameters
        if (programIdStr != null && !programIdStr.isEmpty()) {
            try {
                Long programId = Long.parseLong(programIdStr);
                courses = courseService.findByTrainingProgram(programId);
                request.setAttribute("programId", programId);
            } catch (NumberFormatException e) {
                courses = courseService.findAll();
            }
        } else if (trainerIdStr != null && !trainerIdStr.isEmpty()) {
            try {
                Long trainerId = Long.parseLong(trainerIdStr);
                courses = courseService.findByTrainer(trainerId);
                request.setAttribute("trainerId", trainerId);
            } catch (NumberFormatException e) {
                courses = courseService.findAll();
            }
        } else if (title != null && !title.isEmpty()) {
            courses = courseService.findByTitle(title);
            request.setAttribute("title", title);
        } else if (category != null && !category.isEmpty()) {
            courses = courseService.findByCategory(category);
            request.setAttribute("category", category);
        } else if (status != null && !status.isEmpty()) {
            courses = courseService.findByStatus(status);
            request.setAttribute("status", status);
        } else {
            courses = courseService.findAll();
        }
        
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/WEB-INF/views/course/list.jsp").forward(request, response);
    }
} 