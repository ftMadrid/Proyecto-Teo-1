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
