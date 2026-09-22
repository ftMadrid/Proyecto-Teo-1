package dev.presupuesto.backend.cruds;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import dev.presupuesto.conexiones.ConexionDB;

public class CrudPresupuestoDetalle {

    public boolean insertarPresupuestoDetalle(String idPresupuesto, String idSubcategoria, double monto, String observaciones, String creadoPor){
        String query = "{CALL sp_insertar_presupuesto_detalle(?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            cs.setString(2, idSubcategoria);
            cs.setDouble(3, monto);
            cs.setString(4, observaciones);
            cs.setString(5, creadoPor);
            cs.execute();
            
            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo insertar: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPresupuestoDetalle(String idDetalle, double monto, String observaciones, String modificadoPor){
        String query = "{CALL sp_actualizar_presupuesto_detalle(?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idDetalle);
            cs.setDouble(2, monto);
            cs.setString(3, observaciones);
            cs.setString(4, modificadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPresupuestoDetalle(String idDetalle){
        String query = "{CALL sp_eliminar_presupuesto_detalle(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idDetalle);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo eliminar: " + e.getMessage());
            return false;
        }
    }
    
    public String consultarPresupuestoDetalle(String idDetalle){
        String query = "{CALL sp_consultar_presupuesto_detalle(?)}";
        String resultado = null;
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idDetalle);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resultado = "ID Detalle: " + rs.getString("id_presupuesto_detalle") + "\n" +
                            "Categoría: " + rs.getString("nombre_categoria") + " (" + rs.getString("tipo_categoria") + ")\n" +
                            "Subcategoría: " + rs.getString("nombre_subcategoria") + "\n" +
                            "Monto: L. " + rs.getDouble("monto_mensual") + "\n" +
                            "Observación: " + rs.getString("observaciones_monto");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo consultar: " + e.getMessage());
        }
        return resultado;
    }

    public ArrayList<String> listarDetallesPresupuesto(String idPresupuesto){
        String query = "{CALL sp_listar_detalles_presupuesto(?)}";
        ArrayList<String> lista = new ArrayList<>();
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            ResultSet rs = cs.executeQuery();
            
            while(rs.next()){
                String fila = rs.getString("id_presupuesto_detalle") + " - " + 
                              rs.getString("nombre_subcategoria") + " [L." + 
                              rs.getDouble("monto_mensual") + "]";
                lista.add(fila);
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo listar: " + e.getMessage());
        }
        return lista;
    }
}