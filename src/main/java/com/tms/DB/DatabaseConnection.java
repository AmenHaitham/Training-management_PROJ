package com.tms.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static final String URL = "jdbc:postgresql://196.221.167.63:5432/tms_db";
    private static final String USERNAME = "tms_user";
    private static final String PASSWORD = "tms_pass";
    private static final int POOL_SIZE = 10; // Reduced pool size
    private static final int CONNECTION_TIMEOUT = 30;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int CONNECTION_IDLE_TIMEOUT = 300; // 5 minutes in seconds
    private static final int CONNECTION_MAX_LIFETIME = 1800; // 30 minutes in seconds
    
    private static BlockingQueue<Connection> connectionPool;
    private static boolean initialized = false;
    private static final AtomicInteger activeConnections = new AtomicInteger(0);
    private static final Map<Connection, Long> connectionTimestamps = new ConcurrentHashMap<>();
    private static final Map<Connection, Long> connectionLastUsed = new ConcurrentHashMap<>();

    static {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL JDBC driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load PostgreSQL JDBC driver", e);
        }
    }

    private static synchronized void initializePool() throws SQLException {
        if (!initialized) {
            connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
            
            // Initialize the pool with connections
            for (int i = 0; i < POOL_SIZE; i++) {
                try {
                    Connection conn = createConnection();
                    connectionPool.offer(conn);
                    connectionTimestamps.put(conn, System.currentTimeMillis());
                    connectionLastUsed.put(conn, System.currentTimeMillis());
                } catch (SQLException e) {
                    logger.error("Error creating initial connection", e);
                }
            }
            initialized = true;
            logger.info("Database connection pool initialized with {} connections", connectionPool.size());
            
            // Start connection cleanup thread
            startConnectionCleanupThread();
        }
    }

    private static void startConnectionCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (initialized) {
                try {
                    cleanupConnections();
                    Thread.sleep(60000); // Check every minute
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    private static void cleanupConnections() {
        long currentTime = System.currentTimeMillis();
        connectionTimestamps.forEach((conn, timestamp) -> {
            try {
                // Check if connection is too old
                if (currentTime - timestamp > CONNECTION_MAX_LIFETIME * 1000) {
                    removeConnection(conn);
                    return;
                }
                
                // Check if connection has been idle too long
                Long lastUsed = connectionLastUsed.get(conn);
                if (lastUsed != null && currentTime - lastUsed > CONNECTION_IDLE_TIMEOUT * 1000) {
                    removeConnection(conn);
                }
            } catch (SQLException e) {
                logger.error("Error during connection cleanup", e);
            }
        });
    }

    private static void removeConnection(Connection conn) throws SQLException {
        connectionTimestamps.remove(conn);
        connectionLastUsed.remove(conn);
        if (!conn.isClosed()) {
            conn.close();
        }
        // Create a new connection to replace the removed one
        try {
            Connection newConn = createConnection();
            connectionPool.offer(newConn);
            connectionTimestamps.put(newConn, System.currentTimeMillis());
            connectionLastUsed.put(newConn, System.currentTimeMillis());
        } catch (SQLException e) {
            logger.error("Error creating replacement connection", e);
        }
    }

    private static Connection createConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            
            // Validate the connection
            if (!conn.isValid(5)) {
                throw new SQLException("Database connection validation failed");
            }
            
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to connect to database: {} (SQL State: {}, Error Code: {})", 
                e.getMessage(), e.getSQLState(), e.getErrorCode());
            throw e;
        }
    }

    public static Connection getDatabaseConnection() throws SQLException {
        if (!initialized) {
            initializePool();
        }

        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                Connection conn = connectionPool.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
                if (conn == null) {
                    throw new SQLException("Timeout waiting for available connection");
                }
                
                if (conn.isClosed() || !conn.isValid(5)) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        // Ignore close errors
                    }
                    conn = createConnection();
                }
                
                activeConnections.incrementAndGet();
                connectionLastUsed.put(conn, System.currentTimeMillis());
                logger.debug("Connection obtained. Available: {}, Active: {}", 
                    connectionPool.size(), activeConnections.get());
                return conn;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("Interrupted while waiting for connection", e);
            } catch (SQLException e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    logger.error("Failed to get connection after {} attempts", MAX_RETRIES, e);
                    throw e;
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Interrupted during retry delay", ie);
                }
            }
        }
        throw new SQLException("Failed to get database connection after multiple retries");
    }

    public static void releaseConnection(Connection conn) {
        if (conn != null && initialized) {
            try {
                if (!conn.isClosed() && conn.isValid(5)) {
                    boolean offered = connectionPool.offer(conn);
                    if (!offered) {
                        logger.warn("Could not return connection to pool - pool is full");
                        conn.close();
                    } else {
                        activeConnections.decrementAndGet();
                        connectionLastUsed.put(conn, System.currentTimeMillis());
                        logger.debug("Connection released. Available: {}, Active: {}", 
                            connectionPool.size(), activeConnections.get());
                    }
                } else {
                    logger.warn("Connection is closed or invalid, closing it");
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error("Error releasing connection", e);
            }
        }
    }

    public static void closePool() {
        if (initialized && connectionPool != null) {
            Connection conn;
            while ((conn = connectionPool.poll()) != null) {
                try {
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
            connectionTimestamps.clear();
            connectionLastUsed.clear();
            activeConnections.set(0);
            initialized = false;
            logger.info("Database connection pool closed");
        }
    }

    public static void main(String[] args) {
        try {
            getDatabaseConnection();
        } catch (SQLException e) {
            logger.error("Error: {}", e.getMessage());
        }
    }
}
