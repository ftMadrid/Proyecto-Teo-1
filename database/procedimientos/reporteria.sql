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

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_reporte_ejecucion(
    IN p_id_usuario VARCHAR(30),
    IN p_mes INT,
    IN p_anio INT,
    IN p_tipo VARCHAR(20)
)
BEGIN
    SELECT 
        c.nombre AS categoria,
        s.nombre AS subcategoria,
        pd.monto_mensual AS presupuestado,
        COALESCE(
            (SELECT SUM(monto) 
             FROM transaccion t 
             WHERE t.id_subcategoria = s.id_subcategoria 
               AND t.mes = p_mes 
               AND t.anio = p_anio 
               AND t.id_usuario = p_id_usuario), 0
        ) AS ejecutado
    FROM presupuesto p
    INNER JOIN presupuesto_detalle pd ON p.id_presupuesto = pd.id_presupuesto
    INNER JOIN subcategoria s ON pd.id_subcategoria = s.id_subcategoria
    INNER JOIN categoria c ON s.id_categoria = c.id_categoria
    WHERE p.id_usuario = p_id_usuario
          AND (p_tipo = 'Todas' OR c.tipo_categoria = p_tipo)
          AND (p_anio * 12 + p_mes) BETWEEN (p.anio_inicio * 12 + p.mes_inicio) AND (p.anio_fin * 12 + p.mes_fin)
    ORDER BY c.nombre, s.nombre;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_reporte_obligaciones(
    IN p_id_usuario VARCHAR(30),
    IN p_mes INT,
    IN p_anio INT,
    IN p_estado_filtro VARCHAR(20)
)
BEGIN
    SELECT * FROM (
        SELECT 
            o.nombre AS obligacion,
            c.nombre AS categoria,
            o.monto_mensual AS monto,
            o.dia_vencimiento,
            (SELECT MAX(DATE(t.fecha)) 
             FROM transaccion t 
             WHERE t.id_obligacion = o.id_obligacion 
                AND t.mes = p_mes 
                AND t.anio = p_anio) AS fecha_pago,
            DATEDIFF(
                STR_TO_DATE(CONCAT(p_anio, '-', LPAD(p_mes, 2, '0'), '-', 
                LPAD(LEAST(o.dia_vencimiento, DAY(LAST_DAY(CONCAT(p_anio, '-', LPAD(p_mes, 2, '0'), '-01')))), 2, '0')), '%Y-%m-%d'), 
                CURDATE()
            ) AS dias_restantes,
            CASE
                WHEN (SELECT COUNT(*) FROM transaccion t WHERE t.id_obligacion = o.id_obligacion AND t.mes = p_mes AND t.anio = p_anio) > 0 THEN 'Pagada'
                WHEN DATEDIFF(STR_TO_DATE(CONCAT(p_anio, '-', LPAD(p_mes, 2, '0'), '-', LPAD(LEAST(o.dia_vencimiento, DAY(LAST_DAY(CONCAT(p_anio, '-', LPAD(p_mes, 2, '0'), '-01')))), 2, '0')), '%Y-%m-%d'), CURDATE()) < 0 THEN 'Vencida'
                WHEN DATEDIFF(STR_TO_DATE(CONCAT(p_anio, '-', LPAD(p_mes, 2, '0'), '-', LPAD(LEAST(o.dia_vencimiento, DAY(LAST_DAY(CONCAT(p_anio, '-', LPAD(p_mes, 2, '0'), '-01')))), 2, '0')), '%Y-%m-%d'), CURDATE()) < 3 THEN 'Por Vencer'
                ELSE 'Pendiente'
            END AS estado
        FROM obligacion_fija o
        INNER JOIN subcategoria s ON o.id_subcategoria = s.id_subcategoria
        INNER JOIN categoria c ON s.id_categoria = c.id_categoria
        WHERE o.id_usuario = p_id_usuario
    ) AS reporte
    WHERE p_estado_filtro = 'Todos' OR estado = p_estado_filtro;
END $$
DELIMITER ;