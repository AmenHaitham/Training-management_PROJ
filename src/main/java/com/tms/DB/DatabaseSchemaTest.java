package com.tms.DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchemaTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing database schema...");
            Connection conn = DatabaseConnection.getDatabaseConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("\nDatabase Information:");
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Product: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            
            System.out.println("\nTables in database:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            boolean hasTables = false;
            while (tables.next()) {
                hasTables = true;
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\nTable: " + tableName);
                
                // Get columns for this table
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.println("  Column: " + columnName + " (" + columnType + ")");
                }
                columns.close();
                
                // Get row count for this table
                try (Statement stmt = conn.createStatement()) {
                    ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    if (countRs.next()) {
                        System.out.println("  Row count: " + countRs.getInt(1));
                    }
                    countRs.close();
                }
            }
            tables.close();
            
            if (!hasTables) {
                System.out.println("No tables found in the database!");
            }
            
            DatabaseConnection.releaseConnection(conn);
        } catch (SQLException e) {
            System.err.println("Schema test failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
} 