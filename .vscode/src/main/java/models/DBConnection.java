package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Gèrer la connexion à la base de données
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/clinic_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    private DBConnection() {}

    // Retourner une connexion à la base de données
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database Connection successfully established.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add the dependency to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database! Ensure local XAMPP MySQL server is running on port 3306.");
            e.printStackTrace();
        }
        return connection;
    }

    // Fermer la connexion à la base de données
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error while closing database connection.");
            e.printStackTrace();
        }
    }
}
