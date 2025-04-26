package com.example.web.listener;

import com.example.util.DbInitUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ServletContextListener to initialize database when the application starts
 */
@WebListener
public class DatabaseInitListener implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Web application is starting, initializing database...");
        
        try {
            // Initialize database
            DbInitUtil.initDatabase();
            logger.info("Database initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Web application is shutting down, cleaning up resources...");
        
        // Shutdown connection pool
        try {
            com.example.util.DatabaseUtil.shutdown();
            logger.info("Database connection pool shut down successfully");
        } catch (Exception e) {
            logger.error("Error shutting down database connection pool", e);
        }
    }
} 