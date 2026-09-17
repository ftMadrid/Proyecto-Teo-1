package dev.presupuesto.conexiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    private static final String URL = "jdbc:mariadb://localhost:3306/presupuesto"; 
    private static final String USER = "root";
    private static final String PASSWORD = "sofia2026";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("[LOG-ERROR] Error al contactar con la base de datos: ");
            e.printStackTrace();
        }
        return conexion;
    }
}
