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
