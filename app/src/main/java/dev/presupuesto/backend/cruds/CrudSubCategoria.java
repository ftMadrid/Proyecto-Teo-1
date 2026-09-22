package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudSubCategoria {

    public boolean insertarSubcategoria(String idCategoria, String nombre, String descripcion, String creadoPor){
        
        String query = "{CALL sp_insertar_subcategoria(?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setString(4, creadoPor);
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
                resultado = "ID Subcategoría: " + rs.getString("id_subcategoria") + "\n" +
                            "Nombre: " + rs.getString("nombre") + "\n" +
                            "Categoría: " + rs.getString("nombre_categoria") + "\n" +
                            "Desc: " + rs.getString("descripcion") + "\n" +
                            "Activa: " + (rs.getBoolean("activa") ? "Sí" : "No");
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
                String idSub = rs.getString("id_subcategoria");
                String nom = rs.getString("nombre");
                String act = rs.getBoolean("activa") ? "Sí" : "No";
                
                String desc = "";
                String idCat = "";
                try {
                    desc = rs.getString("descripcion");
                    idCat = rs.getString("id_categoria");
                } catch(Exception e) {}
                
                String fila = idSub + "," + idCat + "," + nom + "," + desc + "," + act;
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
    
}
