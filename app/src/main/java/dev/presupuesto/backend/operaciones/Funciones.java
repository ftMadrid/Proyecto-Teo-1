package dev.presupuesto.backend.operaciones;

import java.sql.CallableStatement;
import java.sql.Connection;
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

}
