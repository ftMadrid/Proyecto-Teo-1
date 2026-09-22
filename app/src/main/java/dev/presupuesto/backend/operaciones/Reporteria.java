package dev.presupuesto.backend.operaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class Reporteria {

    public ArrayList<String> reporteResumenMensual(String idUsuario, String fechaInicio, String fechaFin) {
        String query = "{CALL sp_reporte_mensual_ingresos_gastos(?, ?, ?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try (Connection con = ConexionDB.obtenerConexion();
             CallableStatement cs = con.prepareCall(query)) {
            
            cs.setString(1, idUsuario);
            cs.setString(2, fechaInicio);
            cs.setString(3, fechaFin);
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                String fila = rs.getString("mes_anio") + "," + 
                              rs.getDouble("total_ingresos") + "," + 
                              rs.getDouble("total_gastos") + "," + 
                              rs.getDouble("balance");
                lista.add(fila);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo generar el reporte: " + e.getMessage());
        }
        return lista;
    }
    
}
