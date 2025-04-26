package com.example.web.servlet.course;

import com.example.model.Course;
import com.example.model.TrainingProgram;
import com.example.model.User;
import com.example.service.CourseService;
import com.example.service.TrainingProgramService;
import com.example.service.UserService;
import com.example.service.impl.CourseServiceImpl;
import com.example.service.impl.TrainingProgramServiceImpl;
import com.example.service.impl.UserServiceImpl;
import com.example.util.ServletUtil;
import com.example.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet controller for course CRUD operations
 */
@WebServlet("/course/*")
public class CourseControllerServlet extends HttpServlet {
    
    private final CourseService courseService;
    private final TrainingProgramService programService;
    private final UserService userService;
    
    public CourseControllerServlet() {
        this.courseService = new CourseServiceImpl();
        this.programService = new TrainingProgramServiceImpl();
        this.userService = new UserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // Redirect to programs list
            response.sendRedirect(request.getContextPath() + "/programs");
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String action = pathParts[1];
        
        switch (action) {
            case "view":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                viewCourse(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "create":
                showCreateForm(request, response);
                break;
                
            case "edit":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                showEditForm(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "delete":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                deleteCourse(Long.parseLong(pathParts[2]), request, response);
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String[] pathParts = pathInfo.split("/");
        
        if (pathParts.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String action = pathParts[1];
        
        switch (action) {
            case "create":
                createCourse(request, response);
                break;
                
            case "edit":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                updateCourse(Long.parseLong(pathParts[2]), request, response);
                break;
                
            case "status":
                if (pathParts.length < 3) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                updateCourseStatus(Long.parseLong(pathParts[2]), request, response);
                break;
                
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void viewCourse(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Course> optionalCourse = courseService.findById(id);
        
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            
            // Fetch related data
            if (course.getAssignedTrainerId() != null) {
                Optional<User> trainer = userService.findById(course.getAssignedTrainerId());
                trainer.ifPresent(course::setAssignedTrainer);
            }
            
            // Set attributes for view
            request.setAttribute("course", course);
            request.getRequestDispatcher("/WEB-INF/views/course/view.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
        }
    }
    
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get program ID from request parameter if available
        String programIdParam = request.getParameter("programId");
        Long programId = null;
        
        if (programIdParam != null && !programIdParam.isEmpty()) {
            try {
                programId = Long.parseLong(programIdParam);
                Optional<TrainingProgram> program = programService.findById(programId);
                if (program.isPresent()) {
                    request.setAttribute("program", program.get());
                }
            } catch (NumberFormatException e) {
                // Invalid program ID, ignore
            }
        }
        
        // Get all training programs for dropdown
        List<TrainingProgram> programs = programService.findAll();
        request.setAttribute("programs", programs);
        
        // Get all trainers for assignment dropdown
        List<User> trainers = userService.findAll().stream()
                .filter(user -> "TRAINER".equals(user.getRole()))
                .collect(Collectors.toList());
        request.setAttribute("trainers", trainers);
        
        request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
    }
    
    private void showEditForm(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Course> optionalCourse = courseService.findById(id);
        
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            
            // Get all training programs for dropdown
            List<TrainingProgram> programs = programService.findAll();
            request.setAttribute("programs", programs);
            
            // Get selected program details
            if (course.getTrainingProgramId() != null) {
                Optional<TrainingProgram> program = programService.findById(course.getTrainingProgramId());
                program.ifPresent(p -> request.setAttribute("program", p));
            }
            
            // Get all trainers for assignment dropdown
            List<User> trainers = userService.findAll().stream()
                    .filter(user -> "TRAINER".equals(user.getRole()))
                    .collect(Collectors.toList());
            request.setAttribute("trainers", trainers);
            
            // Set course attribute
            request.setAttribute("course", course);
            request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
        }
    }
    
    private void createCourse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Create new course
        Course course = new Course();
        
        // Populate course from request
        if (populateCourseFromRequest(course, request, response)) {
            try {
                // Save course
                courseService.create(course);
                
                // Set success message
                request.getSession().setAttribute("successMessage", "Course created successfully!");
                
                // Redirect to program view or course list
                if (course.getTrainingProgramId() != null) {
                    response.sendRedirect(request.getContextPath() + "/program/view/" + course.getTrainingProgramId());
                } else {
                    response.sendRedirect(request.getContextPath() + "/programs");
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", e.getMessage());
                request.setAttribute("course", course);
                
                // Re-load form data
                reloadFormData(request);
                
                request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
            }
        }
    }
    
    private void updateCourse(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Course> optionalCourse = courseService.findById(id);
        
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            
            // Populate course from request
            if (populateCourseFromRequest(course, request, response)) {
                try {
                    // Update course
                    courseService.update(course);
                    
                    // Set success message
                    request.getSession().setAttribute("successMessage", "Course updated successfully!");
                    
                    // Redirect to program view or course list
                    if (course.getTrainingProgramId() != null) {
                        response.sendRedirect(request.getContextPath() + "/program/view/" + course.getTrainingProgramId());
                    } else {
                        response.sendRedirect(request.getContextPath() + "/course/view/" + course.getId());
                    }
                } catch (IllegalArgumentException e) {
                    request.setAttribute("errorMessage", e.getMessage());
                    request.setAttribute("course", course);
                    
                    // Re-load form data
                    reloadFormData(request);
                    
                    request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
        }
    }
    
    private void updateCourseStatus(Long id, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Course> optionalCourse = courseService.findById(id);
        String status = request.getParameter("status");
        
        if (optionalCourse.isPresent() && status != null && !status.isEmpty()) {
            Course course = optionalCourse.get();
            course.setStatus(status);
            
            try {
                // Update course status
                courseService.update(course);
                
                // Set success message
                request.getSession().setAttribute("successMessage", "Course status updated to " + status);
                
                // Check if AJAX request
                if (ServletUtil.isAjaxRequest(request)) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":true,\"message\":\"Status updated successfully\"}");
                } else {
                    // Redirect based on referer or course view
                    String referer = request.getHeader("Referer");
                    if (referer != null && !referer.isEmpty()) {
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/course/view/" + id);
                    }
                }
            } catch (IllegalArgumentException e) {
                if (ServletUtil.isAjaxRequest(request)) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
                } else {
                    request.getSession().setAttribute("errorMessage", e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/course/view/" + id);
                }
            }
        } else {
            if (ServletUtil.isAjaxRequest(request)) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Course not found or invalid status\"}");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found or invalid status");
            }
        }
    }
    
    private void deleteCourse(Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<Course> optionalCourse = courseService.findById(id);
        
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            Long programId = course.getTrainingProgramId();
            
            try {
                // Delete course
                boolean deleted = courseService.deleteById(id);
                
                if (deleted) {
                    request.getSession().setAttribute("successMessage", "Course deleted successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to delete course. It may have associated sessions.");
                }
                
                // Redirect to program view or programs list
                if (programId != null) {
                    response.sendRedirect(request.getContextPath() + "/program/view/" + programId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/programs");
                }
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/programs");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
        }
    }
    
    private boolean populateCourseFromRequest(Course course, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set basic course information
        course.setTitle(request.getParameter("title"));
        course.setDescription(request.getParameter("description"));
        course.setCategory(request.getParameter("category"));
        course.setStatus(request.getParameter("status"));
        
        // Parse training program ID
        String programIdParam = request.getParameter("trainingProgramId");
        if (programIdParam != null && !programIdParam.isEmpty()) {
            try {
                Long programId = Long.parseLong(programIdParam);
                course.setTrainingProgramId(programId);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid training program ID");
                request.setAttribute("course", course);
                reloadFormData(request);
                request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
                return false;
            }
        } else {
            request.setAttribute("errorMessage", "Training program is required");
            request.setAttribute("course", course);
            reloadFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
            return false;
        }
        
        // Parse assigned trainer ID
        String trainerIdParam = request.getParameter("assignedTrainerId");
        if (trainerIdParam != null && !trainerIdParam.isEmpty()) {
            try {
                Long trainerId = Long.parseLong(trainerIdParam);
                course.setAssignedTrainerId(trainerId);
            } catch (NumberFormatException e) {
                // Trainer is optional, ignore if invalid
            }
        }
        
        // Parse duration
        String durationParam = request.getParameter("duration");
        if (durationParam != null && !durationParam.isEmpty()) {
            try {
                Integer duration = Integer.parseInt(durationParam);
                course.setDuration(duration);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid duration. Please enter a valid number of hours.");
                request.setAttribute("course", course);
                reloadFormData(request);
                request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
                return false;
            }
        } else {
            request.setAttribute("errorMessage", "Duration is required");
            request.setAttribute("course", course);
            reloadFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/course/form.jsp").forward(request, response);
            return false;
        }
        
        return true;
    }
    
    private void reloadFormData(HttpServletRequest request) throws ServletException, IOException {
        // Get all training programs for dropdown
        List<TrainingProgram> programs = programService.findAll();
        request.setAttribute("programs", programs);
        
        // Get selected program details if program ID is already set
        String programIdParam = request.getParameter("trainingProgramId");
        if (programIdParam != null && !programIdParam.isEmpty()) {
            try {
                Long programId = Long.parseLong(programIdParam);
                Optional<TrainingProgram> program = programService.findById(programId);
                program.ifPresent(p -> request.setAttribute("program", p));
            } catch (NumberFormatException e) {
                // Invalid program ID, ignore
            }
        }
        
        // Get all trainers for assignment dropdown
        List<User> trainers = userService.findAll().stream()
                .filter(user -> "TRAINER".equals(user.getRole()))
                .collect(Collectors.toList());
        request.setAttribute("trainers", trainers);
    }
} 