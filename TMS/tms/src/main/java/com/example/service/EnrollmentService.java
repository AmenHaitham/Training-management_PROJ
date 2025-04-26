package com.example.service;

import com.example.model.Enrollment;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Enrollment operations
 */
public interface EnrollmentService {
    
    /**
     * Find an enrollment by ID
     * @param id The enrollment ID
     * @return Optional containing the enrollment if found
     */
    Optional<Enrollment> findById(Long id);
    
    /**
     * Find all enrollments
     * @return List of all enrollments
     */
    List<Enrollment> findAll();
    
    /**
     * Find enrollments by trainee
     * @param traineeId The trainee ID
     * @return List of enrollments for the trainee
     */
    List<Enrollment> findByTrainee(Long traineeId);
    
    /**
     * Find enrollments by training program
     * @param trainingProgramId The training program ID
     * @return List of enrollments for the training program
     */
    List<Enrollment> findByTrainingProgram(Long trainingProgramId);
    
    /**
     * Find enrollments by status
     * @param status The enrollment status
     * @return List of enrollments with the specified status
     */
    List<Enrollment> findByStatus(String status);
    
    /**
     * Enroll a trainee in a training program
     * @param traineeId The trainee ID
     * @param trainingProgramId The training program ID
     * @return The created enrollment
     * @throws IllegalArgumentException if trainee is already enrolled or if training program is not active
     */
    Enrollment enroll(Long traineeId, Long trainingProgramId) throws IllegalArgumentException;
    
    /**
     * Update an enrollment's status
     * @param id The enrollment ID
     * @param status The new status
     * @return The updated enrollment
     * @throws IllegalArgumentException if enrollment doesn't exist
     */
    Enrollment updateStatus(Long id, String status) throws IllegalArgumentException;
    
    /**
     * Complete an enrollment
     * @param id The enrollment ID
     * @return The updated enrollment
     * @throws IllegalArgumentException if enrollment doesn't exist
     */
    Enrollment complete(Long id) throws IllegalArgumentException;
    
    /**
     * Drop (cancel) an enrollment
     * @param id The enrollment ID
     * @return The updated enrollment
     * @throws IllegalArgumentException if enrollment doesn't exist
     */
    Enrollment drop(Long id) throws IllegalArgumentException;
    
    /**
     * Check if a trainee is enrolled in a training program
     * @param traineeId The trainee ID
     * @param trainingProgramId The training program ID
     * @return true if enrolled, false otherwise
     */
    boolean isEnrolled(Long traineeId, Long trainingProgramId);
} 