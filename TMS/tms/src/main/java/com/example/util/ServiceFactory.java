package com.example.util;

import com.example.service.*;
import com.example.service.impl.*;

/**
 * Factory for creating service instances
 */
public class ServiceFactory {
    // Singleton instances
    private static UserService userService;
    private static TrainingProgramService trainingProgramService;
    private static CourseService courseService;
    private static SessionService sessionService;
    private static EnrollmentService enrollmentService;
    private static MaterialService materialService;
    private static AttendanceService attendanceService;
    
    /**
     * Get UserService instance
     *
     * @return UserService instance
     */
    public static synchronized UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceImpl();
        }
        return userService;
    }
    
    /**
     * Get TrainingProgramService instance
     *
     * @return TrainingProgramService instance
     */
    public static synchronized TrainingProgramService getTrainingProgramService() {
        if (trainingProgramService == null) {
            trainingProgramService = new TrainingProgramServiceImpl();
        }
        return trainingProgramService;
    }
    
    /**
     * Get CourseService instance
     *
     * @return CourseService instance
     */
    public static synchronized CourseService getCourseService() {
        if (courseService == null) {
            courseService = new CourseServiceImpl();
        }
        return courseService;
    }
    
    /**
     * Get SessionService instance
     *
     * @return SessionService instance
     */
    public static synchronized SessionService getSessionService() {
        if (sessionService == null) {
            sessionService = new SessionServiceImpl();
        }
        return sessionService;
    }
    
    /**
     * Get EnrollmentService instance
     *
     * @return EnrollmentService instance
     */
    public static synchronized EnrollmentService getEnrollmentService() {
        if (enrollmentService == null) {
            enrollmentService = new EnrollmentServiceImpl();
        }
        return enrollmentService;
    }
    
    /**
     * Get MaterialService instance
     *
     * @return MaterialService instance
     */
    public static synchronized MaterialService getMaterialService() {
        if (materialService == null) {
            materialService = new MaterialServiceImpl();
        }
        return materialService;
    }
    
    /**
     * Get AttendanceService instance
     *
     * @return AttendanceService instance
     */
    public static synchronized AttendanceService getAttendanceService() {
        if (attendanceService == null) {
            attendanceService = new AttendanceServiceImpl();
        }
        return attendanceService;
    }
    
    // Private constructor to prevent instantiation
    private ServiceFactory() {
    }
} 