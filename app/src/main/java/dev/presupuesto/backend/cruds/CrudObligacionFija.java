package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import dev.presupuesto.backend.operaciones.Funciones;
import dev.presupuesto.conexiones.ConexionDB;

public class CrudObligacionFija {

    public boolean insertarObligacion(String idUsuario, String idSubcategoria, String nombre, String descripcion, double montoMensual, int diaVencimiento, String fechaInicio, String fechaFin, String creadoPor){
        String query = "{CALL sp_insertar_obligacion(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setString(2, idSubcategoria);
            cs.setString(3, nombre);
            cs.setString(4, descripcion);
            cs.setDouble(5, montoMensual);
            cs.setInt(6, diaVencimiento);
            cs.setTimestamp(7, Timestamp.valueOf(fechaInicio)); 
            cs.setTimestamp(8, Timestamp.valueOf(fechaFin));
            cs.setString(9, creadoPor);
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
                Funciones fn = new Funciones();
                
                int dias = fn.diasHastaVencimiento(idObligacion);
                String alertaDias = (dias == 0) ? "¡Vence hoy o ya venció!" : dias + " días";

                resultado = "ID Obligación: " + rs.getString("id_obligacion") + "\n" +
                            "ID Subcategoría: " + rs.getString("id_subcategoria") + "\n" +
                            "Obligacion: " + rs.getString("nombre") + "\n" +
                            "Descripción: " + rs.getString("descripcion") + "\n" +
                            "Subcategoría: " + rs.getString("nombre_subcategoria") + "\n" +
                            "Monto: L." + rs.getDouble("monto_mensual") + "\n" +
                            "Vence dia: " + rs.getInt("dia_vencimiento") + "\n" +
                            "Días para vencer: " + alertaDias + "\n" +
                            "Fecha Inicio: " + rs.getTimestamp("fecha_inicio") + "\n" +
                            "Fecha Fin: " + rs.getTimestamp("fecha_finalizacion") + "\n" +
                            "Modificado por: " + rs.getString("modificado_por");
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