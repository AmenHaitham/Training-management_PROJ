package com.example.util;

import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password operations
 */
public class PasswordUtil {

    private static final int BCRYPT_ROUNDS = 12;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String VALID_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    
    private PasswordUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Hashes a password using BCrypt
     * 
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Checks if a plain text password matches a hashed password
     * 
     * @param plainPassword the plain text password
     * @param hashedPassword the hashed password
     * @return true if the password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    /**
     * Generates a random password of the specified length
     * 
     * @param length the length of the password
     * @return the generated password
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8; // Enforce minimum length
        }
        
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(VALID_PASSWORD_CHARS.length());
            password.append(VALID_PASSWORD_CHARS.charAt(randomIndex));
        }
        
        return password.toString();
    }
    
    /**
     * Validates a password according to security policies
     * 
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        
        return hasLowerCase && hasUpperCase && hasDigit && hasSpecial;
    }
} 