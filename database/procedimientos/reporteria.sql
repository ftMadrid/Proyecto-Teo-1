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
    WHERE (p_id_usuario IS NULL OR id_usuario = p_id_usuario) 
      AND fecha BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY DATE_FORMAT(fecha, '%Y-%m')
    ORDER BY mes_anio ASC;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_reporte_distribucion_gastos(
    IN p_id_usuario VARCHAR(30),
    IN p_mes INT,
    IN p_anio INT
)
BEGIN
    DECLARE v_total_gastos DECIMAL(20,2) DEFAULT 0;

    SELECT IFNULL(SUM(monto), 0) INTO v_total_gastos
    FROM transaccion
    WHERE id_usuario = p_id_usuario 
      AND mes = p_mes 
      AND anio = p_anio 
      AND tipo_transaccion = 'Gasto';

    IF v_total_gastos = 0 THEN
        SET v_total_gastos = 1;
    END IF;

    SELECT 
        c.nombre AS categoria,
        SUM(t.monto) AS total_gastado,
        COUNT(t.id_transaccion) AS numero_transacciones,
        ROUND((SUM(t.monto) / v_total_gastos) * 100, 2) AS porcentaje
    FROM transaccion t
    INNER JOIN subcategoria s ON t.id_subcategoria = s.id_subcategoria
    INNER JOIN categoria c ON s.id_categoria = c.id_categoria
    WHERE t.id_usuario = p_id_usuario 
          AND t.mes = p_mes 
          AND t.anio = p_anio
          AND t.tipo_transaccion = 'Gasto'
    GROUP BY c.id_categoria, c.nombre
    ORDER BY total_gastado DESC;
END $$
DELIMITER ;