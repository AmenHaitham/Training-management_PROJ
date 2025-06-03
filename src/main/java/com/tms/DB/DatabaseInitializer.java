package com.tms.DB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    public static void initialize() {
        try {
            System.out.println("Initializing database schema...");
            Connection conn = DatabaseConnection.getDatabaseConnection();
            
            // Read the SQL script
            String sqlScript;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(DatabaseInitializer.class.getResourceAsStream("/db/init.sql")))) {
                sqlScript = reader.lines().collect(Collectors.joining("\n"));
            }
            
            // Split the script into individual statements
            String[] statements = sqlScript.split(";");
            
            // Execute each statement
            try (Statement stmt = conn.createStatement()) {
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement);
                    }
                }
            }
            
            System.out.println("Database schema initialized successfully");
            DatabaseConnection.releaseConnection(conn);
        } catch (Exception e) {
            System.err.println("Failed to initialize database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 