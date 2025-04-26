package com.example.service;

import com.example.model.TrainingProgram;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Training Program operations
 */
public interface TrainingProgramService {
    
    /**
     * Find a training program by ID
     * @param id The training program ID
     * @return Optional containing the training program if found
     */
    Optional<TrainingProgram> findById(Long id);
    
    /**
     * Find all training programs
     * @return List of all training programs
     */
    List<TrainingProgram> findAll();
    
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
    
    /**
     * Create a new training program
     * @param program The training program to create
     * @return The created training program with ID populated
     */
    TrainingProgram create(TrainingProgram program);
    
    /**
     * Update an existing training program
     * @param program The training program to update
     * @return The updated training program
     * @throws IllegalArgumentException if training program doesn't exist
     */
    TrainingProgram update(TrainingProgram program) throws IllegalArgumentException;
    
    /**
     * Delete a training program by ID
     * @param id The training program ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(Long id);
    
    /**
     * Activate a training program
     * @param id The training program ID
     * @return The updated training program
     * @throws IllegalArgumentException if training program doesn't exist
     */
    TrainingProgram activate(Long id) throws IllegalArgumentException;
    
    /**
     * Complete a training program
     * @param id The training program ID
     * @return The updated training program
     * @throws IllegalArgumentException if training program doesn't exist
     */
    TrainingProgram complete(Long id) throws IllegalArgumentException;
    
    /**
     * Cancel a training program
     * @param id The training program ID
     * @return The updated training program
     * @throws IllegalArgumentException if training program doesn't exist
     */
    TrainingProgram cancel(Long id) throws IllegalArgumentException;
} 