package com.example.service;

import com.example.model.Session;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing sessions
 */
public interface SessionService {
    
    /**
     * Find a session by its ID
     *
     * @param id the session ID
     * @return the session if found, null otherwise
     */
    Session findById(Long id);
    
    /**
     * Find all sessions
     *
     * @return list of all sessions
     */
    List<Session> findAll();
    
    /**
     * Find sessions by course ID
     *
     * @param courseId the course ID
     * @return list of sessions for the specified course
     */
    List<Session> findByCourseId(Long courseId);
    
    /**
     * Find sessions by trainer ID
     *
     * @param trainerId the trainer ID
     * @return list of sessions assigned to the specified trainer
     */
    List<Session> findByTrainerId(Long trainerId);
    
    /**
     * Find sessions by status
     *
     * @param status the session status
     * @return list of sessions with the specified status
     */
    List<Session> findByStatus(String status);
    
    /**
     * Find upcoming sessions for a user
     *
     * @param userId the user ID
     * @return list of upcoming sessions for the user
     */
    List<Session> findUpcomingForUser(Long userId);
    
    /**
     * Find completed sessions for a user
     *
     * @param userId the user ID
     * @return list of completed sessions for the user
     */
    List<Session> findCompletedForUser(Long userId);
    
    /**
     * Save a new session
     *
     * @param session the session to save
     * @return the saved session with ID
     */
    Session save(Session session);
    
    /**
     * Update an existing session
     *
     * @param session the session to update
     * @return the updated session
     */
    Session update(Session session);
    
    /**
     * Delete a session by its ID
     *
     * @param id the session ID
     * @return true if deleted, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Count sessions by course ID
     *
     * @param courseId the course ID
     * @return number of sessions for the specified course
     */
    int countByCourseId(Long courseId);
    
    /**
     * Get enrollments for a session
     *
     * @param sessionId the session ID
     * @return a list of enrollment data
     */
    List<Map<String, Object>> getSessionEnrollments(Long sessionId);
    
    /**
     * Get attendance records for a session
     *
     * @param sessionId the session ID
     * @return a map of enrollment IDs to attendance status
     */
    Map<Long, Boolean> getSessionAttendance(Long sessionId);
    
    /**
     * Save attendance records for a session
     *
     * @param sessionId the session ID
     * @param attendanceData map of enrollment IDs to attendance status
     * @return true if save successful, false otherwise
     */
    boolean saveAttendance(Long sessionId, Map<Long, Boolean> attendanceData);
} 