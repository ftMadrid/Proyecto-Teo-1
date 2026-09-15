package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudObligacionFija {

    public boolean insertarObligacion(String idObligacion, String idUsuario, String idSubcategoria, String nombre, String descripcion, double montoMensual, int diaVencimiento, String fechaInicio, String fechaFin, String creadoPor){
        
        String query = "{CALL sp_insertar_obligacion(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idObligacion);
            cs.setString(2, idUsuario);
            cs.setString(3, idSubcategoria);
            cs.setString(4, nombre);
            cs.setString(5, descripcion);
            cs.setDouble(6, montoMensual);
            cs.setInt(7, diaVencimiento);
            cs.setTimestamp(8, Timestamp.valueOf(fechaInicio)); // solo para dar formato "YYYY-MM-DD HH:MM:SS"
            cs.setTimestamp(9, Timestamp.valueOf(fechaFin));
            cs.setString(10, creadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarObligacion(String idObligacion, String idSubcategoria, String nombre, String descripcion, double montoMensual, int diaVencimiento, String fechaInicio, String fechaFin, String modificadoPor){
        
        String query = "{CALL sp_actualizar_obligacion(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idObligacion);
            cs.setString(2, idSubcategoria);
            cs.setString(3, nombre);
            cs.setString(4, descripcion);
            cs.setDouble(5, montoMensual);
            cs.setInt(6, diaVencimiento);
            cs.setTimestamp(7, Timestamp.valueOf(fechaInicio));
            cs.setTimestamp(8, Timestamp.valueOf(fechaFin));
            cs.setString(9, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarObligacion(String idObligacion, String modificadoPor){
        
        String query = "{CALL sp_eliminar_obligacion(?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idObligacion);
            cs.setString(2, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarObligacion(String idObligacion){
        
        String query = "{CALL sp_consultar_obligacion(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idObligacion);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resultado = "| ID-Obligacion: " + rs.getString("id_obligacion") +
                            " | Obligacion: " + rs.getString("nombre") + 
                            " | Subcategoria: " + rs.getString("nombre_subcategoria") + 
                            " | Monto: L." + rs.getDouble("monto_mensual") + 
                            " | Vence dia: " + rs.getInt("dia_vencimiento") +
                            " | Vigente: " + (rs.getBoolean("es_vigente") ? "Si" : "No");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarObligacionesUsuario(String idUsuario, Boolean esVigente){
        
        String query = "{CALL sp_listar_obligaciones_usuario(?, ?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            
            if(esVigente == null){
                cs.setNull(2, Types.BOOLEAN);
            } else {
                cs.setBoolean(2, esVigente);
            }
            
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_obligacion") + " - " + 
                              rs.getString("nombre") + " [Dia " + rs.getInt("dia_vencimiento") + "] (L." + rs.getDouble("monto_mensual") + ")";
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
    
}
