package com.example.service;

import com.example.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for User operations
 */
public interface UserService {
    
    /**
     * Find a user by ID
     * @param id The user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(Long id);
    
    /**
     * Find all users
     * @return List of all users
     */
    List<User> findAll();
    
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
     * Register a new user
     * @param user The user to register
     * @return The registered user with ID populated
     * @throws IllegalArgumentException if username or email already exists
     */
    User register(User user) throws IllegalArgumentException;
    
    /**
     * Update an existing user
     * @param user The user to update
     * @return The updated user
     * @throws IllegalArgumentException if user doesn't exist
     */
    User update(User user) throws IllegalArgumentException;
    
    /**
     * Delete a user by ID
     * @param id The user ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(Long id);
    
    /**
     * Authenticate a user
     * @param username The username
     * @param password The password (not hashed)
     * @return Optional containing the user if authentication successful
     */
    Optional<User> authenticate(String username, String password);
} 