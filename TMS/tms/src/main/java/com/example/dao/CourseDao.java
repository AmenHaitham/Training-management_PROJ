package com.example.dao;

import com.example.model.Course;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Course entity
 */
public interface CourseDao {
    
    /**
     * Find a course by its ID
     *
     * @param id the course ID
     * @return Optional containing the course if found, empty otherwise
     */
    Optional<Course> findById(Long id);
    
    /**
     * Save a new course
     *
     * @param course the course to save
     * @return the saved course with ID
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
     * Delete a course by its ID
     *
     * @param id the course ID
     * @return true if deleted, false otherwise
     */
    boolean delete(Long id);
    
    /**
     * Find all courses
     *
     * @return list of all courses
     */
    List<Course> findAll();
    
    /**
     * Find courses by program ID
     *
     * @param programId the program ID
     * @return list of courses for the specified program
     */
    List<Course> findByProgramId(Long programId);
    
    /**
     * Find courses by trainer ID
     *
     * @param trainerId the trainer ID
     * @return list of courses assigned to the specified trainer
     */
    List<Course> findByTrainerId(Long trainerId);
    
    /**
     * Find courses by status
     *
     * @param status the course status
     * @return list of courses with the specified status
     */
    List<Course> findByStatus(String status);
    
    /**
     * Count courses by program ID
     *
     * @param programId the program ID
     * @return number of courses for the specified program
     */
    int countByProgramId(Long programId);
} 