package com.example.service;

import java.util.Map;

/**
 * Service interface for managing session attendance
 */
public interface AttendanceService {

    /**
     * Record attendance for a session
     * 
     * @param sessionId the session ID
     * @param enrollmentId the enrollment ID
     * @param present whether the trainee was present
     * @return true if attendance was recorded successfully
     */
    boolean recordAttendance(Long sessionId, Long enrollmentId, boolean present);
    
    /**
     * Get attendance for a session
     * 
     * @param sessionId the session ID
     * @return a map of enrollment IDs to attendance status (true for present, false for absent)
     */
    Map<Long, Boolean> getSessionAttendance(Long sessionId);
    
    /**
     * Get attendance for a trainee in a course
     * 
     * @param courseId the course ID
     * @param traineeId the trainee ID
     * @return a map of session IDs to attendance status
     */
    Map<Long, Boolean> getTraineeAttendance(Long courseId, Long traineeId);
    
    /**
     * Get attendance percentage for a trainee in a course
     * 
     * @param courseId the course ID
     * @param traineeId the trainee ID
     * @return attendance percentage (0-100)
     */
    double getAttendancePercentage(Long courseId, Long traineeId);
} 