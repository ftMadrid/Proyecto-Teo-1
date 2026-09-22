package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudTransaccion {

    public boolean insertarTransaccion(String idUsuario, String idPresupuesto, int anio, int mes, String idSubcategoria, String idObligacion, String tipo, String descripcion, double monto, String fecha, String metodoPago, String numeroFactura, String observaciones, String creadoPor){
        String query = "{CALL sp_insertar_transaccion(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setString(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            cs.setString(5, idSubcategoria);
            cs.setString(6, idObligacion);
            cs.setString(7, tipo);
            cs.setString(8, descripcion);
            cs.setDouble(9, monto);
            cs.setTimestamp(10, Timestamp.valueOf(fecha.length() == 10 ? fecha + " 00:00:00" : fecha)); 
            cs.setString(11, metodoPago);
            cs.setString(12, numeroFactura);
            cs.setString(13, observaciones);
            cs.setString(14, creadoPor);
            cs.execute();
            
            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarTransaccion(String idTransaccion, String idSubcategoria, String tipo, String descripcion, double monto, String fecha, String metodoPago, String numeroFactura, String observaciones, String modificadoPor){
        String query = "{CALL sp_actualizar_transaccion(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idTransaccion);
            cs.setString(2, idSubcategoria);
            cs.setString(3, tipo);
            cs.setString(4, descripcion);
            cs.setDouble(5, monto);
            cs.setTimestamp(6, Timestamp.valueOf(fecha.length() == 10 ? fecha + " 00:00:00" : fecha));
            cs.setString(7, metodoPago);
            cs.setString(8, numeroFactura);
            cs.setString(9, observaciones);
            cs.setString(10, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarTransaccion(String idTransaccion){
        String query = "{CALL sp_eliminar_transaccion(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idTransaccion);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarTransaccion(String idTransaccion){
        String query = "{CALL sp_consultar_transaccion(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idTransaccion);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                String presup = rs.getString("nombre_presupuesto") != null ? rs.getString("nombre_presupuesto") : "N/A";
                String subcat = rs.getString("nombre_subcategoria") != null ? rs.getString("nombre_subcategoria") : "N/A";
                
                resultado = "ID Transacción: " + rs.getString("id_transaccion") + "\n" +
                            "Tipo: " + rs.getString("tipo_transaccion") + "\n" +
                            "Monto: L. " + rs.getDouble("monto") + "\n" +
                            "Fecha: " + rs.getTimestamp("fecha") + "\n" +
                            "Presupuesto: " + presup + "\n" +
                            "Subcategoría: " + subcat;
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarTransaccionesPresupuesto(String idPresupuesto, String tipoTransaccion){
        String query = "{CALL sp_listar_transacciones_presupuesto(?, ?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            
            if(tipoTransaccion == null || tipoTransaccion.trim().isEmpty()){
                cs.setNull(2, Types.VARCHAR);
            } else {
                cs.setString(2, tipoTransaccion);
            }
            
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_transaccion") + "," + 
                              rs.getString("id_presupuesto") + "," + 
                              rs.getString("id_subcategoria") + "," + 
                              rs.getString("id_obligacion") + "," + 
                              rs.getString("tipo_transaccion") + "," + 
                              rs.getDouble("monto") + "," + 
                              rs.getTimestamp("fecha");
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
}