package com.example.dao;

import com.example.model.Enrollment;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Enrollment entity
 */
public interface EnrollmentDao extends GenericDao<Enrollment, Long> {
    
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
     * Find a trainee's enrollment in a specific training program
     * @param traineeId The trainee ID
     * @param trainingProgramId The training program ID
     * @return Optional containing the enrollment if found
     */
    Optional<Enrollment> findByTraineeAndTrainingProgram(Long traineeId, Long trainingProgramId);
    
    /**
     * Check if a trainee is enrolled in a training program
     * @param traineeId The trainee ID
     * @param trainingProgramId The training program ID
     * @return true if enrolled, false otherwise
     */
    boolean isEnrolled(Long traineeId, Long trainingProgramId);
} 