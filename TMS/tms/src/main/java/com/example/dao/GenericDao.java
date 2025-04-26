package com.example.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object Interface
 * @param <T> The entity type
 * @param <ID> The primary key type
 */
public interface GenericDao<T, ID> {
    
    /**
     * Find an entity by its primary key
     * @param id The primary key
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Get all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Save an entity (create or update)
     * @param entity The entity to save
     * @return The saved entity with ID populated
     */
    T save(T entity);
    
    /**
     * Delete an entity by its primary key
     * @param id The primary key
     * @return true if deleted, false if not found
     */
    boolean deleteById(ID id);
    
    /**
     * Check if an entity exists by its primary key
     * @param id The primary key
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Count all entities
     * @return The total count
     */
    long count();
} 