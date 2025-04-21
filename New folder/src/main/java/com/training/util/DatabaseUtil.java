package com.training.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseUtil {
    private static final Properties props = new Properties();
    private static final String DB_URL;
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;
    private static final int MIN_POOL_SIZE;
    private static final int MAX_POOL_SIZE;
    private static final BlockingQueue<Connection> connectionPool;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    throw new RuntimeException("Unable to find database.properties");
                }
                props.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Error loading database properties", e);
            }
            
            DB_URL = props.getProperty("db.url");
            DB_USERNAME = props.getProperty("db.username");
            DB_PASSWORD = props.getProperty("db.password");
            MIN_POOL_SIZE = Integer.parseInt(props.getProperty("db.min.connections", "1"));
            MAX_POOL_SIZE = Integer.parseInt(props.getProperty("db.max.connections", "10"));
            
            if (DB_URL == null || DB_USERNAME == null || DB_PASSWORD == null) {
                throw new RuntimeException("Database properties are not properly configured");
            }

            // Initialize connection pool
            connectionPool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
            for (int i = 0; i < MIN_POOL_SIZE; i++) {
                connectionPool.offer(createConnection());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

    private static Connection createConnection() {
        try {
            Properties connectionProps = new Properties();
            connectionProps.setProperty("user", DB_USERNAME);
            connectionProps.setProperty("password", DB_PASSWORD);
            return DriverManager.getConnection(DB_URL, connectionProps);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = connectionPool.poll();
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }
    
    public static void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed() && connectionPool.size() < MAX_POOL_SIZE) {
                    connectionPool.offer(connection);
                } else {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Cleanup method to close all pooled connections
    public static void closeAllConnections() {
        Connection connection;
        while ((connection = connectionPool.poll()) != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 