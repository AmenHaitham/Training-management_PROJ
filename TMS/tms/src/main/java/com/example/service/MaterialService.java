package com.example.service;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing training materials
 */
public interface MaterialService {

    /**
     * Find a material by ID
     * 
     * @param id the material ID
     * @return an Optional containing the material if found
     */
    Optional<Object> findById(Long id);
    
    /**
     * Find materials by course ID
     * 
     * @param courseId the course ID
     * @return list of materials for the course
     */
    List<Object> findByCourseId(Long courseId);
    
    /**
     * Save a new material
     * 
     * @param material the material to save
     * @return the saved material with generated ID
     */
    Object save(Object material);
    
    /**
     * Update an existing material
     * 
     * @param material the material to update
     * @return the updated material
     */
    Object update(Object material);
    
    /**
     * Delete a material by ID
     * 
     * @param id the material ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(Long id);
} 