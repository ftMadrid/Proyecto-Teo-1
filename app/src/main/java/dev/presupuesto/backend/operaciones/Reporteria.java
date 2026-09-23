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

    public ArrayList<String[]> reporteEjecucionCompleto(String idUsuario, int mes, int anio, String tipo) {
        String query = "{CALL sp_reporte_ejecucion(?, ?, ?, ?)}";
        ArrayList<String[]> listaDatos = new ArrayList<>();
        
        try (Connection con = ConexionDB.obtenerConexion();
             CallableStatement cs = con.prepareCall(query)) {
            
            cs.setString(1, idUsuario);
            cs.setInt(2, mes);
            cs.setInt(3, anio);
            cs.setString(4, tipo);
            
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                String cat = rs.getString("categoria");
                String sub = rs.getString("subcategoria");
                double pres = rs.getDouble("presupuestado");
                double ejec = rs.getDouble("ejecutado");
                
                double dif = pres - ejec;
                double porc = (pres > 0) ? (ejec / pres) * 100 : 0;
                
                listaDatos.add(new String[]{cat, sub, String.valueOf(pres), String.valueOf(ejec), String.valueOf(dif), String.valueOf(porc)});
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Reporte 3: " + e.getMessage());
        }
        return listaDatos;
    }

    public ArrayList<String[]> reporteObligaciones(String idUsuario, int mes, int anio, String estadoFiltro) {
        String query = "{CALL sp_reporte_obligaciones(?, ?, ?, ?)}";
        ArrayList<String[]> lista = new ArrayList<>();
        
        try (Connection con = ConexionDB.obtenerConexion();
             CallableStatement cs = con.prepareCall(query)) {
            
            cs.setString(1, idUsuario);
            cs.setInt(2, mes);
            cs.setInt(3, anio);
            cs.setString(4, estadoFiltro);
            
            ResultSet rs = cs.executeQuery();
            
            while (rs.next()) {
                String obli = rs.getString("obligacion");
                String cat = rs.getString("categoria");
                String monto = String.valueOf(rs.getDouble("monto"));
                String diaVenc = String.valueOf(rs.getInt("dia_vencimiento"));
                String fechaPago = rs.getString("fecha_pago") != null ? rs.getString("fecha_pago") : "Sin pago";
                int dias = rs.getInt("dias_restantes");
                String estado = rs.getString("estado");
                
                String textoDias = (estado.equals("Pagada")) ? "-" : (dias < 0 ? Math.abs(dias) + " atrasados" : dias + " restantes");
                
                lista.add(new String[]{obli, cat, monto, diaVenc, textoDias, fechaPago, estado});
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Reporte 4: " + e.getMessage());
        }
        return lista;
    }
    
}
