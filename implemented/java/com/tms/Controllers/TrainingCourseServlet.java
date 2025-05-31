package com.tms.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tms.DAO.CourseDAO;
import com.tms.DAO.TrainingCourseDAO;
import com.tms.DAO.TrainingDAO;
import com.tms.Model.Course;
import com.tms.Model.Training;
import com.tms.Model.TrainingCourse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/tms/training-courses/*")
public class TrainingCourseServlet extends HttpServlet {
    private TrainingCourseDAO trainingCourseService ;
    private TrainingDAO trainingService;
    private CourseDAO courseService ;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                    -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .setPrettyPrinting()
            .create();
    
            @Override
    public void init() {
        try {
            trainingCourseService = new TrainingCourseDAO();
            trainingService = new TrainingDAO();
        courseService = new CourseDAO();
        } catch (SQLException e) {
            // handle exception (consider logging or rethrowing as ServletException)
        }
        
    }

@Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
                setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all training courses
                List<TrainingCourse> trainingCourses = trainingCourseService.getAllTrainingCourses();
                response.getWriter().write(gson.toJson(trainingCourses));
            } else {
                // Get single training course by ID
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    int trainingCourseId = Integer.parseInt(parts[1]);
                    TrainingCourse trainingCourse = trainingCourseService.getTrainingCourseById(trainingCourseId).get();
                    if (trainingCourse != null) {
                        response.getWriter().write(gson.toJson(trainingCourse));
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training course not found");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training course ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
                setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Parse request body
            TrainingCourse trainingCourseData = parseTrainingCourseData(request);
            
            // Validate training exists
            Training training = trainingService.getTrainingById(trainingCourseData.getTraining().getId()).get();
            if (training == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training ID");
                return;
            }
            
            // Validate course exists
            Course course = courseService.getCourseById(trainingCourseData.getCourse().getId()).get();
            if (course == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
                return;
            }
            
            // Validate dates
            if (trainingCourseData.getStartDate().isAfter(trainingCourseData.getEndDate())) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "End date must be after start date");
                return;
            }
            
            // Validate sessions
            if (trainingCourseData.getTotalSessions() <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Total sessions must be greater than 0");
                return;
            }
            
            TrainingCourse createdTrainingCourse = trainingCourseService.createTrainingCourse(trainingCourseData).get();
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(gson.toJson(createdTrainingCourse));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating training course");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
                setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Training course ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                // Update training course
                int trainingCourseId = Integer.parseInt(parts[1]);
                TrainingCourse trainingCourseData = parseTrainingCourseData(request);
                trainingCourseData.setId(trainingCourseId);
                
                // Validate training exists
                Training training = trainingService.getTrainingById(trainingCourseData.getTraining().getId()).get();
                if (training == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training ID");
                    return;
                }
                
                // Validate course exists
                Course course = courseService.getCourseById(trainingCourseData.getCourse().getId()).get();
                if (course == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
                    return;
                }
                
                // Validate dates
                if (trainingCourseData.getStartDate().isAfter(trainingCourseData.getEndDate())) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "End date must be after start date");
                    return;
                }
                
                // Validate sessions
                if (trainingCourseData.getTotalSessions() <= 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Total sessions must be greater than 0");
                    return;
                }
                
                TrainingCourse updatedTrainingCourse = trainingCourseService.updateTrainingCourse(trainingCourseData).get();
                response.getWriter().write(gson.toJson(updatedTrainingCourse));
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training course ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating training course");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
                setCORSHeaders(response);
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Training course ID required");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                int trainingCourseId = Integer.parseInt(parts[1]);
                boolean deleted = trainingCourseService.deleteTrainingCourse(trainingCourseId);
                if (deleted) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"message\":\"Training course deleted successfully\"}");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Training course not found");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid training course ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting training course");
        }
    }

    private TrainingCourse parseTrainingCourseData(HttpServletRequest request) throws IOException {
        // Using Gson to parse JSON request body
        TrainingCourse trainingCourse = gson.fromJson(request.getReader(), TrainingCourse.class);
        
        // For form data, you would use request.getParameter() instead
        // TrainingCourse trainingCourse = new TrainingCourse();
        // Training training = new Training();
        // training.setId(Integer.parseInt(request.getParameter("trainingId")));
        // trainingCourse.setTraining(training);
        // Course course = new Course();
        // course.setId(Integer.parseInt(request.getParameter("courseId")));
        // trainingCourse.setCourse(course);
        // trainingCourse.setTotalSessions(Integer.parseInt(request.getParameter("totalSessions")));
        // trainingCourse.setStatus(TrainingCourse.Status.fromString(request.getParameter("status")));
        // trainingCourse.setStartDate(LocalDate.parse(request.getParameter("startDate"), dateFormatter));
        // trainingCourse.setEndDate(LocalDate.parse(request.getParameter("endDate"), dateFormatter));
        
        return trainingCourse;
    }
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}