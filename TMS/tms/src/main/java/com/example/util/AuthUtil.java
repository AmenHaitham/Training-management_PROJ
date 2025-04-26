package com.example.util;

import com.example.model.User;
import com.example.service.UserService;
import com.example.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Utility class for authentication and authorization
 */
public class AuthUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthUtil.class);
    private static final UserService userService = new UserServiceImpl();
    
    // User sessions would typically be managed by a session mechanism
    // For this simple implementation, we'll store the currently authenticated user in a ThreadLocal
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    
    private AuthUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Authenticate a user
     * @param username The username
     * @param password The password
     * @return true if authentication successful, false otherwise
     */
    public static boolean authenticate(String username, String password) {
        logger.debug("Authenticating user: {}", username);
        
        Optional<User> user = userService.authenticate(username, password);
        if (user.isPresent()) {
            currentUser.set(user.get());
            logger.debug("Authentication successful for user: {}", username);
            return true;
        } else {
            currentUser.remove();
            logger.debug("Authentication failed for user: {}", username);
            return false;
        }
    }
    
    /**
     * Get the currently authenticated user
     * @return Optional containing the authenticated user if present
     */
    public static Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser.get());
    }
    
    /**
     * Check if the current user has the specified role
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        Optional<User> user = getCurrentUser();
        return user.isPresent() && role.equals(user.get().getRole());
    }
    
    /**
     * Check if the current user is an admin
     * @return true if the user is an admin, false otherwise
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Check if the current user is a trainer
     * @return true if the user is a trainer, false otherwise
     */
    public static boolean isTrainer() {
        return hasRole("TRAINER");
    }
    
    /**
     * Check if the current user is a trainee
     * @return true if the user is a trainee, false otherwise
     */
    public static boolean isTrainee() {
        return hasRole("TRAINEE");
    }
    
    /**
     * Check if the current user is authenticated
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getCurrentUser().isPresent();
    }
    
    /**
     * Log out the current user
     */
    public static void logout() {
        logger.debug("Logging out user");
        currentUser.remove();
    }
} 