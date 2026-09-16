package dev.presupuesto.backend.operaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Types;

import dev.presupuesto.conexiones.ConexionDB;

public class Funciones {

    public double calcularMontoEjecutado(String idSubcategoria, int anio, int mes){
        
        String query = "{? = CALL fn_calcular_monto_ejecutado(?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idSubcategoria);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            
            cs.execute();
            
            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular el monto: " + e.getMessage());
            return 0.0;
        }
    }

    public double calcularPorcentajeEjecutado(String idSubcategoria, String idPresupuesto, int anio, int mes){
        
        String query = "{? = CALL fn_calcular_porcentaje_ejecutado(?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idSubcategoria);
            cs.setString(3, idPresupuesto);
            cs.setInt(4, anio);
            cs.setInt(5, mes);
            cs.execute();

            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular el porcentaje: " + e.getMessage());
            return 0.0;
        }
    }

    public double obtenerBalanceSubcategoria(String idPresupuesto, String idSubcategoria, int anio, int mes){
        
        String query = "{? = CALL fn_obtener_balance_subcategoria(?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idPresupuesto);
            cs.setString(3, idSubcategoria);
            cs.setInt(4, anio);
            cs.setInt(5, mes);
            cs.execute();
            
            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo obtener el balance: " + e.getMessage());
            return 0.0;
        }
    }

    public double obtenerTotalCategoriaMes(String idCategoria, String idPresupuesto, int anio, int mes){
        
        String query = "{? = CALL fn_obtener_total_categoria_mes(?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idCategoria);
            cs.setString(3, idPresupuesto);
            cs.setInt(4, anio);
            cs.setInt(5, mes);
            cs.execute();
            
            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo obtener el total de la categoria: " + e.getMessage());
            return 0.0;
        }
    }

    public double obtenerTotalEjecutadoCategoriaMes(String idCategoria, int anio, int mes){
        
        String query = "{? = CALL fn_obtener_total_ejecutado_categoria_mes(?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idCategoria);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            cs.execute();
            
            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo obtener el total ejecutado: " + e.getMessage());
            return 0.0;
        }
    }

    public int diasHastaVencimiento(String idObligacion){
        
        String query = "{? = CALL fn_dias_hasta_vencimiento(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, idObligacion);
            cs.execute();
            
            return cs.getInt(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular los dias hasta el vencimiento: " + e.getMessage());
            return 0;
        }
    }

    public boolean validarVigenciaPresupuesto(String fecha, String idPresupuesto){
        
        String query = "{? = CALL fn_validar_vigencia_presupuesto(?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.BOOLEAN);
            
            cs.setDate(2, Date.valueOf(fecha)); // para que sea con formato bonito "YYYY-MM-DD"
            cs.setString(3, idPresupuesto);
            cs.execute();
            
            return cs.getBoolean(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo validar la vigencia del presupuesto: " + e.getMessage());
            return false;
        }
    }

    public String obtenerCategoriaPorSubcategoria(String idSubcategoria){
        
        String query = "{? = CALL fn_obtener_categoria_por_subcategoria(?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.setString(2, idSubcategoria);
            cs.execute();
            
            return cs.getString(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo obtener la categoria padre: " + e.getMessage());
            return null;
        }
    }

    public double calcularProyeccionGastoMensual(String idSubcategoria, int anio, int mes){
        
        String query = "{? = CALL fn_calcular_proyeccion_gasto_mensual(?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idSubcategoria);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            cs.execute();

            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular la proyeccion: " + e.getMessage());
            return 0.0;
        }
    }

    public double obtenerPromedioGastoSubcategoria(String idUsuario, String idSubcategoria, int cantidadMeses){
        
        String query = "{? = CALL fn_obtener_promedio_gasto_subcategoria(?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, idUsuario);
            cs.setString(3, idSubcategoria);
            cs.setInt(4, cantidadMeses);
            cs.execute();
            
            return cs.getDouble(1);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular el promedio: " + e.getMessage());
            return 0.0;
        }
    }

}
