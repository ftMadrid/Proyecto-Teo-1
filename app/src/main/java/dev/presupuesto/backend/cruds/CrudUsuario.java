package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudUsuario {
    
    public boolean insertarUsuario(String id, String nombres, String apellidos, String correo, double salario, String creadoPor){
        String query = "{CALL sp_insertar_usuario(?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, id);
            cs.setString(2, nombres);
            cs.setString(3, apellidos);
            cs.setString(4, correo);
            cs.setDouble(5, salario);
            cs.setString(6, creadoPor); 
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarUsuario(String id, String nombres, String apellidos, String correo, double salario, String modificadoPor){
        String query = "{CALL sp_actualizar_usuario(?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, id);
            cs.setString(2, nombres);
            cs.setString(3, apellidos);
            cs.setString(4, correo);
            cs.setDouble(5, salario);
            cs.setString(6, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUsuario(String id, String modificadoPor){

        String query = "{CALL sp_eliminar_usuario(?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, id);
            cs.setString(2, modificadoPor); 
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarUsuario(String id){
        String query = "{CALL sp_consultar_usuario(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, id);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resultado = "| ID: " + rs.getString("id_usuario") + "\n" + 
                            "| Nombre: " + rs.getString("nombres") + " " + rs.getString("apellidos") + "\n" + 
                            "| Correo: " + rs.getString("correo") + "\n" + 
                            "| Fecha de Registro: " + rs.getDate("fecha_registro") + "\n" + 
                            "| Salario Base: L." + rs.getDouble("salario_base") + "\n" + 
                            "| Estado Actual: " + rs.getString("estado");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarUsuarios(){
        
        String query = "{CALL sp_listar_usuarios()}";
        ArrayList<String> listaUsuarios = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_usuario") + "," + 
                              rs.getString("nombres") + "," + 
                              rs.getString("apellidos") + "," + 
                              rs.getString("correo") + "," + 
                              rs.getDate("fecha_registro") + "," + 
                              rs.getDouble("salario_base") + "," + 
                              rs.getString("estado");
                listaUsuarios.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return listaUsuarios;
    }
}
