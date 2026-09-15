package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudCategoria {

    public boolean insertarCategoria(String idCategoria, String nombre, String descripcion, String tipoCategoria, int orden, String creadoPor){
        
        String query = "{CALL sp_insertar_categoria(?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setString(4, tipoCategoria);
            cs.setInt(5, orden);
            cs.setString(6, creadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarCategoria(String idCategoria, String nombre, String descripcion, String tipoCategoria, int orden, String modificadoPor){
        
        String query = "{CALL sp_actualizar_categoria(?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setString(4, tipoCategoria);
            cs.setInt(5, orden);
            cs.setString(6, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCategoria(String idCategoria){
        
        String query = "{CALL sp_eliminar_categoria(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarCategoria(String idCategoria){
        
        String query = "{CALL sp_consultar_categoria(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resultado = "| ID-Categoria: " + rs.getString("id_categoria") +
                            " | Nombre: " + rs.getString("nombre") + 
                            " | Tipo: " + rs.getString("tipo_categoria") + 
                            " | Orden: " + rs.getInt("orden") +
                            " | Desc: " + rs.getString("descripcion");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarCategorias(String tipoCategoria){
        
        String query = "{CALL sp_listar_categoria(?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, tipoCategoria);
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_categoria") + " - " + 
                              rs.getString("nombre") + " [" + rs.getString("tipo_categoria") + "]";
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
    
}
