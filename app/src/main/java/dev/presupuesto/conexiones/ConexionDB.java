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
            System.out.println("[LOG] Conexion exitosa a MariaDB.");
        } catch (SQLException e) {
            System.err.println("[LOG-ERROR] Error al conectar con la base de datos: ");
            e.printStackTrace();
        }
        return conexion;
    }

    public static void main(String[] args) {
        Connection conn = obtenerConexion();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
