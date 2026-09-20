package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudPresupuesto {

    public boolean insertarPresupuesto(String idPresupuesto, String idUsuario, String nombre, int anioInicio, int mesInicio, int anioFin, int mesFin, String creadoPor){
        
        String query = "{CALL sp_insertar_presupuesto(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            cs.setString(2, idUsuario);
            cs.setString(3, nombre);
            cs.setInt(4, anioInicio);
            cs.setInt(5, mesInicio);
            cs.setInt(6, anioFin);
            cs.setInt(7, mesFin);
            cs.setString(8, creadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPresupuesto(String idPresupuesto, String nombre, int anioInicio, int mesInicio, int anioFin, int mesFin, String estado, String modificadoPor){
        
        String query = "{CALL sp_actualizar_presupuesto(?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            cs.setString(2, nombre);
            cs.setInt(3, anioInicio);
            cs.setInt(4, mesInicio);
            cs.setInt(5, anioFin);
            cs.setInt(6, mesFin);
            cs.setString(7, estado);
            cs.setString(8, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPresupuesto(String idPresupuesto){
        
        String query = "{CALL sp_eliminar_presupuesto(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarPresupuesto(String idPresupuesto){

        String query = "{CALL sp_consultar_presupuesto(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resultado = "ID Presupuesto: " + rs.getString("id_presupuesto") + "\n" +
                            "ID Usuario: " + rs.getString("id_usuario") + "\n" +
                            "Nombre: " + rs.getString("nombre_descriptivo") + "\n" +
                            "Inicio: " + rs.getInt("mes_inicio") + "/" + rs.getInt("anio_inicio") + "\n" +
                            "Fin: " + rs.getInt("mes_fin") + "/" + rs.getInt("anio_fin") + "\n" +
                            "Estado: " + rs.getString("estado_presupuesto");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarPresupuestosUsuario(String idUsuario){
        String query = "{CALL sp_listar_presupuestos_usuario(?, ?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setString(2, "");
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_presupuesto") + "," + 
                              rs.getString("id_usuario") + "," + 
                              rs.getString("nombre_descriptivo") + "," + 
                              rs.getInt("anio_inicio") + "," + 
                              rs.getInt("mes_inicio") + "," + 
                              rs.getInt("anio_fin") + "," + 
                              rs.getInt("mes_fin") + "," + 
                              rs.getString("estado_presupuesto");
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
    
}
