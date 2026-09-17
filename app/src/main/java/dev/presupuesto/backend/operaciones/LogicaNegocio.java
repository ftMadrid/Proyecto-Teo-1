package dev.presupuesto.backend.operaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import dev.presupuesto.conexiones.ConexionDB;

public class LogicaNegocio {

    public boolean crearPresupuestoCompleto(String idUsuario, String nombre, String descripcion, String periodoInicio, String periodoFin, String jsonSubcategorias, String creadoPor){
        
        String query = "{CALL sp_crear_presupuesto_completo(?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setDate(4, Date.valueOf(periodoInicio));
            cs.setDate(5, Date.valueOf(periodoFin));
            cs.setString(6, jsonSubcategorias); // texto largo, igual tengo que testear mucho esto
            cs.setString(7, creadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo crear el presupuesto completo: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarTransaccionCompleta(String idUsuario, String idPresupuesto, int anio, int mes, String idSubcategoria, String tipo, String descripcion, double monto, String fecha, String metodoPago, String creadoPor){
        
        String query = "{CALL sp_registrar_transaccion_completa(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setString(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            cs.setString(5, idSubcategoria);
            cs.setString(6, tipo);
            cs.setString(7, descripcion);
            cs.setDouble(8, monto);
            cs.setTimestamp(9, Timestamp.valueOf(fecha));
            cs.setString(10, metodoPago);
            cs.setString(11, creadoPor);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] La transaccion fue rechazada: " + e.getMessage());
            return false;
        }
    }

    public boolean procesarObligacionesMes(String idUsuario, int anio, int mes, String idPresupuesto){
        
        String query = "{CALL sp_procesar_obligaciones_mes(?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setInt(2, anio);
            cs.setInt(3, mes);
            cs.setString(4, idPresupuesto);
            cs.execute();

            return true;
        }catch(Exception e){
            System.err.println("[ERROR] No se pudieron procesar las obligaciones: " + e.getMessage());
            return false;
        }
    }

    public double[] calcularBalanceMensual(String idUsuario, String idPresupuesto, int anio, int mes){
        
        String query = "{CALL sp_calcular_balance_mensual(?, ?, ?, ?, ?, ?, ?, ?)}";
        double[] resultados = new double[4]; // para que no se me olvide [0 = ingresos], [1 = gastos], [2 = ahorros], [3 = balance]
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idUsuario);
            cs.setString(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            
            cs.registerOutParameter(5, Types.DOUBLE);
            cs.registerOutParameter(6, Types.DOUBLE);
            cs.registerOutParameter(7, Types.DOUBLE);
            cs.registerOutParameter(8, Types.DOUBLE);
            
            cs.execute();

            resultados[0] = cs.getDouble(5);
            resultados[1] = cs.getDouble(6);
            resultados[2] = cs.getDouble(7);
            resultados[3] = cs.getDouble(8);
            
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular el balance mensual: " + e.getMessage());
        }
        return resultados;
    }

    public double calcularMontoEjecutadoMes(String idSubcategoria, String idPresupuesto, int anio, int mes){
        
        String query = "{CALL sp_calcular_monto_ejecutado_mes(?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idSubcategoria);
            cs.setString(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            
            cs.registerOutParameter(5, java.sql.Types.DOUBLE);
            cs.execute();
            
            return cs.getDouble(5);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular el monto ejecutado del mes: " + e.getMessage());
            return 0.0;
        }
    }

    public double calcularPorcentajeEjecucionMes(String idSubcategoria, String idPresupuesto, int anio, int mes){
        
        String query = "{CALL sp_calcular_porcentaje_ejecucion_mes(?, ?, ?, ?, ?)}";
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idSubcategoria);
            cs.setString(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            
            cs.registerOutParameter(5, java.sql.Types.DOUBLE);
            cs.execute();
            
            return cs.getDouble(5);
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo calcular el porcentaje de ejecucion: " + e.getMessage());
            return 0.0;
        }
    }

    public double[] cerrarPresupuesto(String idPresupuesto, String modificadoPor){
        
        String query = "{CALL sp_cerrar_presupuesto(?, ?)}";
        double[] resumen = new double[3]; // no olvidar!!! [0 = ingresos], [1 = gastos], [2 = ahorros]
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idPresupuesto);
            cs.setString(2, modificadoPor);
            ResultSet rs = cs.executeQuery();
            
            if(rs.next()){
                resumen[0] = rs.getDouble("total_ingresos");
                resumen[1] = rs.getDouble("total_gastos");
                resumen[2] = rs.getDouble("total_ahorros");
            }
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo cerrar el presupuesto: " + e.getMessage());
        }
        return resumen;
    }

    public double[] obtenerResumenCategoriaMes(String idCategoria, String idPresupuesto, int anio, int mes){
        String query = "{CALL sp_obtener_resumen_categoria_mes(?, ?, ?, ?, ?, ?, ?)}";
        double[] resumen = new double[3]; // [0 = presupuestado], [1 = ejecutado], [2 = porcentaje]
        
        try(Connection con = ConexionDB.obtenerConexion();
            CallableStatement cs = con.prepareCall(query)){
            
            cs.setString(1, idCategoria);
            cs.setString(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            
            cs.registerOutParameter(5, java.sql.Types.DOUBLE);
            cs.registerOutParameter(6, java.sql.Types.DOUBLE);
            cs.registerOutParameter(7, java.sql.Types.DOUBLE);
            
            cs.execute();
            
            resumen[0] = cs.getDouble(5);
            resumen[1] = cs.getDouble(6);
            resumen[2] = cs.getDouble(7);
            
        }catch(Exception e){
            System.err.println("[ERROR] No se pudo obtener el resumen de la categoria: " + e.getMessage());
        }
        return resumen;
    }

}
