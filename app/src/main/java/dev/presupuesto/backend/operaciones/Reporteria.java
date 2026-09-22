package dev.presupuesto.backend.operaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class Reporteria {

    public ArrayList<String> reporteResumenMensual(String idUsuario, String fechaInicio, String fechaFin) {
        String query = "{CALL sp_reporte_mensual_ingresos_gastos(?, ?, ?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try (Connection con = ConexionDB.obtenerConexion();
             CallableStatement cs = con.prepareCall(query)) {
            
            cs.setNull(1, Types.VARCHAR);
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

    public ArrayList<String[]> reporteDistribucionGastos(String idUsuario, int mes, int anio) {
        String query = "{CALL sp_reporte_distribucion_gastos(?, ?, ?)}";
        ArrayList<String[]> listaDatos = new ArrayList<>();
        
        try (Connection con = ConexionDB.obtenerConexion();
             CallableStatement cs = con.prepareCall(query)) {
            
            cs.setString(1, idUsuario);
            cs.setInt(2, mes);
            cs.setInt(3, anio);
            
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                String categoria = rs.getString("categoria");
                String total = String.valueOf(rs.getDouble("total_gastado"));
                String transacciones = String.valueOf(rs.getInt("numero_transacciones"));
                String porcentaje = String.valueOf(rs.getDouble("porcentaje"));
                listaDatos.add(new String[]{categoria, total, transacciones, porcentaje});
            }
        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo generar Reporte 2: " + e.getMessage());
        }
        return listaDatos;
    }
    
}
