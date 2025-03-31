package com.logiscope.servlets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/logs";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    static {
        try {
            System.out.println("Loading MySQL JDBC driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Failed to load MySQL JDBC driver");
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Attempting to connect to database...");
        System.out.println("URL: " + URL);
        System.out.println("Username: " + USERNAME);
        
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.println("Database connection established successfully!");
        return connection;
    }
}
