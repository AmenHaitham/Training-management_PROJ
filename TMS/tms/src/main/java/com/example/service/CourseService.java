package com.example.service;

import com.example.model.Course;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Course entities
 */
public interface CourseService {
    
    /**
     * Find a course by its ID
     *
     * @param id the course ID
     * @return an Optional containing the course if found
     */
    Optional<Course> findById(Long id);
    
    /**
     * Get all courses
     *
     * @return list of all courses
     */
    List<Course> findAll();
    
    /**
     * Find courses belonging to a specific training program
     *
     * @param programId the program ID
     * @return list of courses for the program
     */
    List<Course> findByProgramId(Long programId);
    
    /**
     * Find courses assigned to a specific trainer
     *
     * @param trainerId the trainer ID
     * @return list of courses for the trainer
     */
    List<Course> findByTrainerId(Long trainerId);
    
    /**
     * Find courses by status
     *
     * @param status the course status
     * @return list of courses with the given status
     */
    List<Course> findByStatus(String status);
    
    /**
     * Find courses by training program ID
     *
     * @param programId the training program ID
     * @return list of courses for the program
     */
    List<Course> findByTrainingProgram(Long programId);
    
    /**
     * Find courses by trainer ID
     *
     * @param trainerId the trainer ID
     * @return list of courses for the trainer
     */
    List<Course> findByTrainer(Long trainerId);
    
    /**
     * Find courses by title
     *
     * @param title the course title
     * @return list of courses with matching title
     */
    List<Course> findByTitle(String title);
    
    /**
     * Find courses by category
     *
     * @param category the course category
     * @return list of courses in the category
     */
    List<Course> findByCategory(String category);
    
    /**
     * Create a new course
     *
     * @param course the course to create
     * @return the created course with generated ID
     */
    Course create(Course course);
    
    /**
     * Save a new course
     *
     * @param course the course to save
     * @return the saved course with generated ID
     */
    Course save(Course course);
    
    /**
     * Update an existing course
     *
     * @param course the course to update
     * @return the updated course
     */
    Course update(Course course);
    
    /**
     * Delete a course by ID
     *
     * @param id the course ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Delete a course by ID
     *
     * @param id the course ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(Long id);
    
    /**
     * Count courses in a program
     *
     * @param programId the program ID
     * @return the number of courses in the program
     */
    int countByProgramId(Long programId);
} 