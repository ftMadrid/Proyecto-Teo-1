DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_reporte_mensual_ingresos_gastos(
    IN p_id_usuario VARCHAR(30),
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE
)
BEGIN
    SELECT 
        DATE_FORMAT(fecha, '%Y-%m') AS mes_anio,
        SUM(CASE WHEN tipo_transaccion = 'Ingreso' THEN monto ELSE 0 END) AS total_ingresos,
        SUM(CASE WHEN tipo_transaccion = 'Gasto' THEN monto ELSE 0 END) AS total_gastos,
        SUM(CASE WHEN tipo_transaccion = 'Ingreso' THEN monto ELSE 0 END) - 
        SUM(CASE WHEN tipo_transaccion = 'Gasto' THEN monto ELSE 0 END) AS balance
    FROM transaccion
    WHERE id_usuario = p_id_usuario AND fecha BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY DATE_FORMAT(fecha, '%Y-%m')
    ORDER BY mes_anio ASC;
END $$
DELIMITER ;