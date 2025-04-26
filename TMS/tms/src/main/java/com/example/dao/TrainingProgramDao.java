package com.example.dao;

import com.example.model.TrainingProgram;

import java.util.List;

/**
 * Data Access Object for TrainingProgram entity
 */
public interface TrainingProgramDao extends GenericDao<TrainingProgram, Long> {
    
    /**
     * Find training programs by title
     * @param title Title to search for
     * @return List of matching training programs
     */
    List<TrainingProgram> findByTitle(String title);
    
    /**
     * Find training programs by status
     * @param status Status to search for
     * @return List of matching training programs
     */
    List<TrainingProgram> findByStatus(String status);
    
    /**
     * Find training programs created by a specific user
     * @param userId ID of the creator user
     * @return List of training programs created by the user
     */
    List<TrainingProgram> findByCreatedBy(Long userId);
    
    /**
     * Find active training programs available for enrollment
     * @return List of active training programs
     */
    List<TrainingProgram> findActivePrograms();
} 