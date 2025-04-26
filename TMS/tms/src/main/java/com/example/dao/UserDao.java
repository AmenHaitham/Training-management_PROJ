package com.example.dao;

import com.example.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entity
 */
public interface UserDao extends GenericDao<User, Long> {
    
    /**
     * Find a user by username
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     * @param email The email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     * @param role The role to search for
     * @return List of users with the specified role
     */
    List<User> findByRole(String role);
    
    /**
     * Check if a username already exists
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email already exists
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Authenticate a user
     * @param username The username
     * @param password The password (not hashed)
     * @return Optional containing the user if authentication successful
     */
    Optional<User> authenticate(String username, String password);
} 