package com.tms.DB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void main(String[] args) {
        try {
            // Get database connection
            Connection connection = DatabaseConnection.getDatabaseConnection();
            
            // Read and execute SQL script
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(DatabaseInitializer.class.getResourceAsStream("/schema.sql")));
                 Statement stmt = connection.createStatement()) {
                
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                        continue;
                    }
                    sql.append(line);
                    if (line.trim().endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql.setLength(0);
                    }
                }
            }
            
            System.out.println("Database schema created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 