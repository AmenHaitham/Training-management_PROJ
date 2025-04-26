package com.example.service.impl;

import com.example.dao.UserDao;
import com.example.dao.impl.UserDaoImpl;
import com.example.model.User;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService
 */
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserDao userDao;
    
    /**
     * Constructor
     */
    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }
    
    /**
     * Constructor with dependency injection
     * @param userDao The UserDao implementation to use
     */
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        logger.debug("Finding all users");
        return userDao.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userDao.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userDao.findByEmail(email);
    }

    @Override
    public List<User> findByRole(String role) {
        logger.debug("Finding users by role: {}", role);
        return userDao.findByRole(role);
    }

    @Override
    public User register(User user) throws IllegalArgumentException {
        logger.debug("Registering new user with username: {}", user.getUsername());
        
        // Validate username and email uniqueness
        if (userDao.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        
        if (userDao.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        
        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("TRAINEE");
        }
        
        // Save the user
        return userDao.save(user);
    }

    @Override
    public User update(User user) throws IllegalArgumentException {
        logger.debug("Updating user with ID: {}", user.getId());
        
        // Check if user exists
        if (!userDao.existsById(user.getId())) {
            throw new IllegalArgumentException("User does not exist with ID: " + user.getId());
        }
        
        // Check username uniqueness if changed
        Optional<User> existingUser = userDao.findById(user.getId());
        if (existingUser.isPresent()) {
            User current = existingUser.get();
            
            // Check username uniqueness if changed
            if (!current.getUsername().equals(user.getUsername()) && 
                userDao.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + user.getUsername());
            }
            
            // Check email uniqueness if changed
            if (!current.getEmail().equals(user.getEmail()) && 
                userDao.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + user.getEmail());
            }
            
            // Keep existing password if not provided
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(current.getPassword());
            }
        }
        
        // Update the user
        return userDao.save(user);
    }

    @Override
    public boolean deleteById(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        return userDao.deleteById(id);
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        logger.debug("Authenticating user: {}", username);
        return userDao.authenticate(username, password);
    }
} 