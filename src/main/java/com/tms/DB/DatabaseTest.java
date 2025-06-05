package com.tms.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://196.221.167.63:5432/tms_db";
        String username = "tms_user";
        String password = "tms_pass";

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC driver loaded successfully");

            // Try to connect to the database
            System.out.println("Attempting to connect to database...");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to database!");

            // Test the connection
            if (conn.isValid(5)) {
                System.out.println("Connection is valid");
            } else {
                System.out.println("Connection is invalid");
            }

            // Close the connection
            conn.close();
            System.out.println("Connection closed successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load PostgreSQL JDBC driver: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
} 