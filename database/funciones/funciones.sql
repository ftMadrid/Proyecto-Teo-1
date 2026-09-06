DELIMITER $$
CREATE OR REPLACE FUNCTION fn_calcular_monto_ejecutado(f_id_subcategoria VARCHAR(30), f_anio INT, f_mes INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN 
    RETURN (
        SELECT SUM(monto)
        FROM transaccion
        WHERE id_subcategoria = f_id_subcategoria AND anio = f_anio AND mes = f_mes
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_calcular_porcentaje_ejecutado(f_id_subcategoria VARCHAR(30), f_id_presupuesto VARCHAR(30), f_anio INT, f_mes INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN
    DECLARE total_ejecutado, total_presupuestado, total_porcentaje DECIMAL(20, 2) DEFAULT 0.00;

    SET total_ejecutado = (
        SELECT COALESCE(SUM(monto), 0)
        FROM transaccion
        WHERE id_subcategoria = f_id_subcategoria AND anio = f_anio AND mes = f_mes
    );

    SET total_presupuestado = (
        SELECT COALESCE(SUM(monto_mensual), 0)
        FROM presupuesto_detalle
        WHERE id_presupuesto = f_id_presupuesto AND id_subcategoria = f_id_subcategoria
    );

    IF total_presupuestado > 0 THEN
        SET total_porcentaje = (total_ejecutado / total_presupuestado) * 100;
    ELSE
        SET total_porcentaje = 0.00;
    END IF;

    RETURN total_porcentaje;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_obtener_balance_subcategoria(f_id_presupuesto VARCHAR(30), f_id_subcategoria VARCHAR(30), f_anio INT, f_mes INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN
    DECLARE total_presupuestado, total_ejecutado, balance_disponible DECIMAL(20, 2) DEFAULT 0.00;

    SET total_presupuestado = (
        SELECT COALESCE(SUM(monto_mensual), 0)
        FROM presupuesto_detalle
        WHERE id_presupuesto = f_id_presupuesto AND id_subcategoria = f_id_subcategoria
    );

    SET total_ejecutado = (
        SELECT COALESCE(SUM(monto), 0)
        FROM transaccion
        WHERE id_subcategoria = f_id_subcategoria AND anio = f_anio AND mes = f_mes
    );

    SET balance_disponible = total_presupuestado - total_ejecutado;

    RETURN balance_disponible;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_obtener_total_categoria_mes(f_id_categoria VARCHAR(30), f_id_presupuesto VARCHAR(30), f_anio INT, f_mes INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN
    DECLARE total_presupuestado DECIMAL(20, 2) DEFAULT 0.00;

    SET total_presupuestado = (
        SELECT COALESCE(SUM(pd.monto_mensual), 0)
        FROM presupuesto_detalle pd
        INNER JOIN subcategoria sc ON pd.id_subcategoria = sc.id_subcategoria
        INNER JOIN presupuesto p ON pd.id_presupuesto = p.id_presupuesto
        WHERE pd.id_presupuesto = f_id_presupuesto AND sc.id_categoria = f_id_categoria
            AND f_anio >= p.anio_inicio AND f_anio <= p.anio_fin
            AND f_mes >= p.mes_inicio AND f_mes <= p.mes_fin
    );

    RETURN total_presupuestado;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_obtener_total_ejecutado_categoria_mes(f_id_categoria VARCHAR(30), f_anio INT, f_mes INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN
    DECLARE total_ejecutado DECIMAL(20, 2) DEFAULT 0.00;

    SET total_ejecutado = (
        SELECT COALESCE(SUM(t.monto), 0)
        FROM transaccion t
        INNER JOIN subcategoria sc ON t.id_subcategoria = sc.id_subcategoria
        WHERE sc.id_categoria = f_id_categoria AND t.anio = f_anio AND t.mes = f_mes
    );

    RETURN total_ejecutado;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_dias_hasta_vencimiento(f_id_obligacion VARCHAR(30))
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE vencimiento INT DEFAULT 0;

    SET vencimiento = (
        SELECT dia_vencimiento
        FROM obligacion_fija
        WHERE id_obligacion = f_id_obligacion
    );

    IF vencimiento IS NULL THEN
        RETURN 0;
    END IF;

    IF vencimiento >= DAY(CURDATE()) THEN
        RETURN vencimiento - DAY(CURDATE());
    ELSE
        RETURN 0;
    END IF;

END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_validar_vigencia_presupuesto(f_fecha DATE, f_id_presupuesto VARCHAR(30))
RETURNS BOOLEAN
READS SQL DATA
BEGIN
    DECLARE vigente INT DEFAULT 0;

    SET vigente = (
        SELECT COUNT(*)
        FROM presupuesto
        WHERE id_presupuesto = f_id_presupuesto 
            AND EXTRACT(YEAR_MONTH FROM f_fecha) >= (anio_inicio * 100 + mes_inicio) -- EXTRACT es para extraer una parte especifica de una fecha
            AND EXTRACT(YEAR_MONTH FROM f_fecha) <= (anio_fin * 100 + mes_fin)
    );

    IF vigente > 0 THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_obtener_categoria_por_subcategoria(f_id_subcategoria VARCHAR(30))
RETURNS VARCHAR(30)
READS SQL DATA
BEGIN
    RETURN (
        SELECT id_categoria
        FROM subcategoria
        WHERE id_subcategoria = f_id_subcategoria
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_calcular_proyeccion_gasto_mensual(f_id_subcategoria VARCHAR(30), f_anio INT, f_mes INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN
    DECLARE monto_actual DECIMAL(20, 2) DEFAULT 0.00;
    DECLARE dias_totales INT;
    DECLARE dias_transcurridos INT;
    DECLARE fecha_mes DATE;

    SET fecha_mes = STR_TO_DATE(CONCAT(f_anio, '-', f_mes, '-01'), '%Y-%m-%d');
    SET dias_totales = DAY(LAST_DAY(fecha_mes));

    IF f_anio = YEAR(CURDATE()) AND f_mes = MONTH(CURDATE()) THEN
        SET dias_transcurridos = DAY(CURDATE());
    ELSEIF fecha_mes < CURDATE() THEN
        SET dias_transcurridos = dias_totales; 
    ELSE
        RETURN 0.00;
    END IF;

    SET monto_actual = fn_calcular_monto_ejecutado(f_id_subcategoria, f_anio, f_mes);

    IF dias_transcurridos > 0 THEN
        RETURN (monto_actual / dias_transcurridos) * dias_totales;
    ELSE
        RETURN 0.00;
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE FUNCTION fn_obtener_promedio_gasto_subcategoria(f_id_usuario VARCHAR(30), f_id_subcategoria VARCHAR(30), f_cantidad_meses INT)
RETURNS DECIMAL(20, 2)
READS SQL DATA
BEGIN
    DECLARE total_gasto DECIMAL(20, 2) DEFAULT 0.00;

    IF f_cantidad_meses <= 0 THEN
        RETURN 0.00;
    END IF;

    SET total_gasto = (
        SELECT SUM(monto)
        FROM transaccion
        WHERE id_usuario = f_id_usuario
            AND id_subcategoria = f_id_subcategoria
            AND tipo = 'Gasto'
            AND fecha >= DATE_SUB(CURDATE(), INTERVAL f_cantidad_meses MONTH) -- DATE_SUB resta la cantidad de meses especificando la fecha actual para no generar busqueda al pasado
    );

    IF total_gasto IS NULL THEN
        RETURN 0.00;
    END IF;

    RETURN total_gasto/f_cantidad_meses;

END $$
DELIMITER ;