package com.example.dao;

import com.example.model.Session;

import java.sql.Date;
import java.util.List;

/**
 * Data Access Object interface for Session entity
 */
public interface SessionDao {
    
    /**
     * Find a session by its ID
     * 
     * @param id the session ID
     * @return the session or null if not found
     */
    Session findById(Long id);
    
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
     * Find all sessions
     * 
     * @return list of all sessions
     */
    List<Session> findAll();
    
    /**
     * Find sessions for a specific course
     * 
     * @param courseId the course ID
     * @return list of sessions
     */
    List<Session> findByCourseId(Long courseId);
    
    /**
     * Find sessions led by a specific trainer
     * 
     * @param trainerId the trainer ID
     * @return list of sessions
     */
    List<Session> findByTrainerId(Long trainerId);
    
    /**
     * Find sessions for a user after a specific date
     * 
     * @param userId the user ID
     * @param date the date
     * @return list of sessions
     */
    List<Session> findByUserIdAndDateAfter(Long userId, Date date);
    
    /**
     * Find sessions for a user before a specific date
     * 
     * @param userId the user ID
     * @param date the date
     * @return list of sessions
     */
    List<Session> findByUserIdAndDateBefore(Long userId, Date date);
    
    /**
     * Find sessions by status
     * 
     * @param status the session status
     * @return list of sessions with the specified status
     */
    List<Session> findByStatus(String status);
    
    /**
     * Count sessions for a specific course
     * 
     * @param courseId the course ID
     * @return number of sessions
     */
    int countByCourseId(Long courseId);
} 