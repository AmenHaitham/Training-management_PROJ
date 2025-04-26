package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Utility class for database initialization
 */
public class DbInitUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(DbInitUtil.class);
    
    private DbInitUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initialize the database
     */
    public static void initDatabase() {
        logger.info("Initializing database...");
        
        try {
            // Execute schema
            executeScript("db_schema.sql");
            
            logger.info("Database initialization completed successfully");
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Execute an SQL script
     * @param resourcePath Path to the SQL script in resources
     * @throws IOException If the script cannot be read
     * @throws SQLException If the script execution fails
     */
    public static void executeScript(String resourcePath) throws IOException, SQLException {
        logger.info("Executing SQL script: {}", resourcePath);
        
        String script = readResourceFile(resourcePath);
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(script);
            conn.commit();
            
            logger.info("SQL script executed: {}", resourcePath);
        }
    }
    
    /**
     * Read a resource file as a string
     * @param resourcePath Path to the resource file
     * @return Contents of the resource file
     * @throws IOException If the file cannot be read
     */
    private static String readResourceFile(String resourcePath) throws IOException {
        try (InputStream is = DbInitUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
} 