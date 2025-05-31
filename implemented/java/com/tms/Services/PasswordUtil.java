package com.tms.Services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for secure password handling using PBKDF2 with HMAC-SHA512.
 * Implements current security best practices including:
 * - Password hashing with salt
 * - Key stretching
 * - Configurable work factors
 * - Constant-time comparison
 */
public class PasswordUtil {
    
    // Security parameters - can be adjusted based on performance/security needs
    private static final int ITERATIONS = 120_000;  // Number of iterations
    private static final int KEY_LENGTH = 256;      // Key length in bits
    private static final int SALT_LENGTH = 32;      // Salt length in bytes
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    
    // SecureRandom instance for salt generation
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * Hashes a password with a randomly generated salt using PBKDF2.
     * 
     * @param password The plaintext password to hash
     * @return A string containing the algorithm, iterations, salt and hash separated by ':'
     */
    public String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Generate a random salt
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        
        // Hash the password
        byte[] hash = pbkdf2(password.toCharArray(), salt);
        
        // Format: algorithm:iterations:salt:hash
        return String.format("%s:%d:%s:%s",
            ALGORITHM,
            ITERATIONS,
            Base64.getEncoder().encodeToString(salt),
            Base64.getEncoder().encodeToString(hash));
    }
    
    /**
     * Verifies a password against a stored hash.
     * 
     * @param password The plaintext password to verify
     * @param storedHash The stored hash string in format "algorithm:iterations:salt:hash"
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String password, String storedHash) {
        if (password == null || password.isEmpty() || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        
        // Parse the stored hash components
        String[] parts = storedHash.split(":");
        if (parts.length != 4) {
            return false;
        }
        
        try {
            String algorithm = parts[0];
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            
            // Verify the algorithm matches
            if (!algorithm.equals(ALGORITHM)) {
                return false;
            }
            
            // Compute the hash of the provided password
            byte[] actualHash = pbkdf2(password.toCharArray(), salt, iterations);
            
            // Compare the hashes in constant time
            return constantTimeEquals(actualHash, expectedHash);
        } catch (NumberFormatException e) {
            // Log this error in production
            return false;
        }
    }
    
    /**
     * Computes the PBKDF2 hash of a password with salt.
     */
    private static byte[] pbkdf2(char[] password, byte[] salt) {
        return pbkdf2(password, salt, ITERATIONS);
    }
    
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        } finally {
            // Clear the password array
            Arrays.fill(password, '\0');
        }
    }
    
    /**
     * Compares two byte arrays in constant time to prevent timing attacks.
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
    
    /**
     * Generates a random password of specified length.
     * 
     * @param length The length of the password to generate
     * @param includeSpecialChars Whether to include special characters
     * @return The generated password
     */
    public static String generateRandomPassword(int length, boolean includeSpecialChars) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }
        
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        String validChars = letters + numbers;
        if (includeSpecialChars) {
            validChars += special;
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(validChars.length());
            sb.append(validChars.charAt(index));
        }
        
        return sb.toString();
    }
}