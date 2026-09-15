package dev.presupuesto.backend.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudSubCategoria {

    public boolean insertarSubcategoria(String idSubcategoria, String idCategoria, String nombre, String descripcion, String creadoPor){
        
        String query = "{CALL sp_insertar_subcategoria(?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idSubcategoria);
            cs.setString(2, idCategoria);
            cs.setString(3, nombre);
            cs.setString(4, descripcion);
            cs.setString(5, creadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarSubcategoria(String idSubcategoria, String nombre, String descripcion, boolean activa, String modificadoPor){
        
        String query = "{CALL sp_actualizar_subcategoria(?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idSubcategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setBoolean(4, activa);
            cs.setString(5, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarSubcategoria(String idSubcategoria){
        
        String query = "{CALL sp_eliminar_subcategoria(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idSubcategoria);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarSubcategoria(String idSubcategoria){
        
        String query = "{CALL sp_consultar_subcategoria(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idSubcategoria);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resultado = "| ID-Subcategoria: " + rs.getString("id_subcategoria") +
                            " | Nombre: " + rs.getString("nombre") + 
                            " | Categoria: " + rs.getString("nombre_categoria") + 
                            " | Desc: " + rs.getString("descripcion") + 
                            " | Activa: " + (rs.getBoolean("activa") ? "Si" : "No");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarSubcategoriasPorCategoria(String idCategoria){
        
        String query = "{CALL sp_listar_subcategorias_por_categoria(?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_subcategoria") + " - " + 
                              rs.getString("nombre") + " [" + (rs.getBoolean("activa") ? "ACTIVA" : "INACTIVA") + "]";
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
    
}
