DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_crear_presupuesto_completo(
    IN p_id_usuario VARCHAR(30),
    IN p_nombre VARCHAR(255),
    IN p_descripcion VARCHAR(255),
    IN p_periodo_inicio DATE,
    IN p_periodo_fin DATE,
    IN p_lista_subcategorias_json LONGTEXT,
    IN p_creado_por VARCHAR(30)
)
BEGIN
    DECLARE v_id_presupuesto VARCHAR(30);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SET v_id_presupuesto = SUBSTRING(REPLACE(UUID(), '-', ''), 1, 30);

    INSERT INTO presupuesto (
        id_presupuesto,
        id_usuario,
        nombre_descriptivo,
        anio_inicio,
        mes_inicio,
        anio_fin,
        mes_fin,
        estado_presupuesto,
        fecha_creacion,
        creado_por
    )VALUES (
        v_id_presupuesto,
        p_id_usuario,
        p_nombre,
        YEAR(p_periodo_inicio), MONTH(p_periodo_inicio),
        YEAR(p_periodo_fin), MONTH(p_periodo_fin),
        'ACTIVO',
        CURRENT_TIMESTAMP,
        p_creado_por
    );

    INSERT INTO presupuesto_detalle(
        id_presupuesto_detalle,
        id_presupuesto,
        id_subcategoria,
        monto_mensual,
        creado_por
    )
    SELECT
        SUBSTRING(REPLACE(UUID(), '-', ''), 1, 30),
        v_id_presupuesto,
        jt.id_subcategoria,
        jt.monto_mensual,
        p_creado_por
    FROM JSON_TABLE(
        p_lista_subcategorias_json,
        '$[*]' COLUMNS (
            id_subcategoria VARCHAR(30) PATH '$.id_subcategoria',
            monto_mensual DECIMAL(20,2) PATH '$.monto_mensual'
        )
    ) AS jt;

    COMMIT;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_registrar_transaccion_completa(
    IN p_id_usuario VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    IN p_id_subcategoria VARCHAR(30),
    IN p_tipo VARCHAR(50),
    IN p_descripcion VARCHAR(255),
    IN p_monto DECIMAL(20,2),
    IN p_fecha TIMESTAMP,
    IN p_metodo_pago VARCHAR(100),
    IN p_creado_por VARCHAR(30)
)
BEGIN
    DECLARE v_anio_inicio INT;
    DECLARE v_mes_inicio INT;
    DECLARE v_anio_fin INT;
    DECLARE v_mes_fin INT;
    DECLARE v_id_transaccion VARCHAR(30);
    DECLARE v_fecha_valida BOOLEAN DEFAULT FALSE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_mes < 1 OR p_mes > 12 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'El mes es invalido [debe ser entre 1 y 12].';
    END IF;

    SELECT anio_inicio, mes_inicio, anio_fin, mes_fin 
    INTO v_anio_inicio, v_mes_inicio, v_anio_fin, v_mes_fin
    FROM presupuesto 
    WHERE id_presupuesto = p_id_presupuesto;

    IF (p_anio > v_anio_inicio OR (p_anio = v_anio_inicio AND p_mes >= v_mes_inicio)) AND 
       (p_anio < v_anio_fin OR (p_anio = v_anio_fin AND p_mes <= v_mes_fin)) THEN
        SET v_fecha_valida = TRUE;
    END IF;

    IF v_fecha_valida = FALSE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'La transaccion esta fuera del periodo de vigencia de este presupuesto.';
    END IF;

    START TRANSACTION;
    SET v_id_transaccion = SUBSTRING(REPLACE(UUID(), '-', ''), 1, 30);

    INSERT INTO transaccion (
        id_transaccion, 
        id_usuario, 
        id_presupuesto, 
        anio, 
        mes,
        id_subcategoria, 
        tipo_transaccion, 
        descripcion, 
        monto,
        fecha, 
        metodo_pago, 
        creado_por
    ) VALUES (
        v_id_transaccion, 
        p_id_usuario, 
        p_id_presupuesto, 
        p_anio, 
        p_mes,
        p_id_subcategoria, 
        p_tipo, 
        p_descripcion, 
        p_monto,
        p_fecha, 
        p_metodo_pago, 
        p_creado_por
    );

    COMMIT;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_procesar_obligaciones_mes(
    IN p_id_usuario VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    IN p_id_presupuesto VARCHAR(30)
)
BEGIN
    INSERT INTO alerta (
        id_alerta,
        id_usuario,
        id_obligacion,
        mensaje_alerta,
        anio,
        mes,
        estado_alerta
    )
    SELECT 
        SUBSTRING(REPLACE(UUID(), '-', ''), 1, 30),
        p_id_usuario,
        id_obligacion,
        CONCAT('[ADVERTENCIA] La obligacion ', nombre, ' vence el dia ', dia_vencimiento, ' de este mes.'),
        p_anio,
        p_mes,
        'ACTIVA'
    FROM obligacion_fija
    WHERE id_usuario = p_id_usuario 
          AND es_vigente = 1
          AND (YEAR(fecha_inicio) < p_anio OR (YEAR(fecha_inicio) = p_anio AND MONTH(fecha_inicio) <= p_mes))
          AND (fecha_finalizacion IS NULL OR YEAR(fecha_finalizacion) > p_anio OR (YEAR(fecha_finalizacion) = p_anio AND MONTH(fecha_finalizacion) >= p_mes));
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_calcular_balance_mensual(
    IN p_id_usuario VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    OUT p_total_ingresos DECIMAL(20,2),
    OUT p_total_gastos DECIMAL(20,2),
    OUT p_total_ahorros DECIMAL(20,2),
    OUT p_balance_final DECIMAL(20,2)
)
BEGIN
    SELECT 
        COALESCE(SUM(CASE WHEN tipo_transaccion = 'INGRESO' THEN monto END), 0),
        COALESCE(SUM(CASE WHEN tipo_transaccion = 'GASTO' THEN monto END), 0),
        COALESCE(SUM(CASE WHEN tipo_transaccion = 'AHORRO' THEN monto END), 0)
    INTO 
        p_total_ingresos, 
        p_total_gastos, 
        p_total_ahorros
    FROM transaccion
    WHERE id_usuario = p_id_usuario
          AND id_presupuesto = p_id_presupuesto
          AND anio = p_anio
          AND mes = p_mes;

    SET p_balance_final = p_total_ingresos - (p_total_gastos + p_total_ahorros);
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_calcular_monto_ejecutado_mes(
    IN p_id_subcategoria VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    OUT p_monto_ejecutado DECIMAL(20,2)
)
BEGIN
    SELECT 
        COALESCE(SUM(monto), 0)
    INTO 
        p_monto_ejecutado
    FROM transaccion
    WHERE id_subcategoria = p_id_subcategoria
          AND id_presupuesto = p_id_presupuesto
          AND anio = p_anio
          AND mes = p_mes;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_calcular_porcentaje_ejecucion_mes(
    IN p_id_subcategoria VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    OUT p_porcentaje DECIMAL(10,2)
)
BEGIN
    DECLARE v_monto_presupuestado DECIMAL(20,2) DEFAULT 0;
    DECLARE v_monto_ejecutado DECIMAL(20,2) DEFAULT 0;

    SELECT COALESCE(MAX(monto_mensual), 0)
    INTO v_monto_presupuestado
    FROM presupuesto_detalle
    WHERE id_presupuesto = p_id_presupuesto
          AND id_subcategoria = p_id_subcategoria;

    SELECT COALESCE(SUM(monto), 0)
    INTO v_monto_ejecutado
    FROM transaccion
    WHERE id_subcategoria = p_id_subcategoria
          AND id_presupuesto = p_id_presupuesto
          AND anio = p_anio
          AND mes = p_mes;

    IF v_monto_presupuestado > 0 THEN
        SET p_porcentaje = (v_monto_ejecutado / v_monto_presupuestado) * 100;
    ELSE
        SET p_porcentaje = 0.00;
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_cerrar_presupuesto(
    IN p_id_presupuesto VARCHAR(30),
    IN p_modificado_por VARCHAR(30)
)
BEGIN
    DECLARE v_periodo_fin DATE;

    SELECT MAX(periodo_fin)
    INTO v_periodo_fin
    FROM presupuesto
    WHERE id_presupuesto = p_id_presupuesto;

    IF v_periodo_fin >= CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = '[Error] La fecha de fin del presupuesto aun no ha pasado.';
    END IF;

    UPDATE presupuesto
    SET estado = 'CERRADO', modificado_por = p_modificado_por
    WHERE id_presupuesto = p_id_presupuesto;

    SELECT 
        id_presupuesto,
        COALESCE(SUM(CASE WHEN tipo_transaccion = 'INGRESO' THEN monto END), 0) AS total_ingresos,
        COALESCE(SUM(CASE WHEN tipo_transaccion = 'GASTO' THEN monto END), 0) AS total_gastos,
        COALESCE(SUM(CASE WHEN tipo_transaccion = 'AHORRO' THEN monto END), 0) AS total_ahorros
    FROM transaccion
    WHERE id_presupuesto = p_id_presupuesto;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_obtener_resumen_categoria_mes(
    IN p_id_categoria VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    OUT p_monto_presupuestado DECIMAL(20,2),
    OUT p_monto_ejecutado DECIMAL(20,2),
    OUT p_porcentaje DECIMAL(10,2)
)
BEGIN
    SELECT COALESCE(SUM(pd.monto_mensual), 0)
    INTO p_monto_presupuestado
    FROM presupuesto_detalle pd
    INNER JOIN subcategoria s ON pd.id_subcategoria = s.id_subcategoria
    WHERE pd.id_presupuesto = p_id_presupuesto
          AND s.id_categoria = p_id_categoria;

    SELECT COALESCE(SUM(t.monto), 0)
    INTO p_monto_ejecutado
    FROM transaccion t
    INNER JOIN subcategoria s ON t.id_subcategoria = s.id_subcategoria
    WHERE t.id_presupuesto = p_id_presupuesto
          AND t.anio = p_anio
          AND t.mes = p_mes
          AND s.id_categoria = p_id_categoria;

    IF p_monto_presupuestado > 0 THEN
        SET p_porcentaje = (p_monto_ejecutado / p_monto_presupuestado) * 100;
    ELSE
        SET p_porcentaje = 0.00;
    END IF;
END $$
DELIMITER ;