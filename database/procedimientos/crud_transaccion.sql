DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_transaccion(
    IN p_id_transaccion VARCHAR(30),
    IN p_id_usuario VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_anio INT,
    IN p_mes INT,
    IN p_id_subcategoria VARCHAR(30),
    IN p_id_obligacion VARCHAR(30),
    IN p_tipo_transaccion VARCHAR(50),
    IN p_descripcion VARCHAR(255),
    IN p_monto DECIMAL(20,2),
    IN p_fecha TIMESTAMP,
    IN p_metodo_pago VARCHAR(100),
    IN p_numero_factura VARCHAR(30),
    IN p_observaciones VARCHAR(255)
)
BEGIN
    INSERT INTO transaccion(
        id_transaccion,
        id_usuario,
        id_presupuesto,
        anio,
        mes,
        id_subcategoria,
        id_obligacion,
        tipo_transaccion,
        descripcion,
        monto,
        fecha,
        metodo_pago,
        numero_factura,
        observaciones,
        fecha_registro
    )VALUES (
        p_id_transaccion,
        p_id_usuario,
        p_id_presupuesto,
        p_anio,
        p_mes,
        p_id_subcategoria,
        p_id_obligacion,
        p_tipo_transaccion,
        p_descripcion,
        p_monto,
        p_fecha,
        p_metodo_pago,
        p_numero_factura,
        p_observaciones,
        CURRENT_TIMESTAMP
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_transaccion(
    IN p_id_transaccion VARCHAR(30),
    IN p_id_subcategoria VARCHAR(30),
    IN p_tipo_transaccion VARCHAR(50),
    IN p_descripcion VARCHAR(255),
    IN p_monto DECIMAL(20,2),
    IN p_fecha TIMESTAMP,
    IN p_metodo_pago VARCHAR(100),
    IN p_numero_factura VARCHAR(30),
    IN p_observaciones VARCHAR(255)
)
BEGIN
    UPDATE transaccion
    SET id_subcategoria = p_id_subcategoria,
        tipo_transaccion = p_tipo_transaccion,
        descripcion = p_descripcion,
        monto = p_monto,
        fecha = p_fecha,
        metodo_pago = p_metodo_pago,
        numero_factura = p_numero_factura,
        observaciones = p_observaciones
    WHERE id_transaccion = p_id_transaccion;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_transaccion(
    IN p_id_transaccion VARCHAR(30)
)
BEGIN
    DELETE FROM transaccion WHERE id_transaccion = p_id_transaccion;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_transaccion(
    IN p_id_transaccion VARCHAR(30)
)
BEGIN
    SELECT t.*, s.nombre AS nombre_subcategoria, p.nombre_descriptivo AS nombre_presupuesto
    FROM transaccion t
    LEFT JOIN subcategoria s ON t.id_subcategoria = s.id_subcategoria
    LEFT JOIN presupuesto p ON t.id_presupuesto = p.id_presupuesto
    WHERE t.id_transaccion = p_id_transaccion;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_transacciones_presupuesto(
    IN p_id_presupuesto VARCHAR(30),
    IN p_tipo_transaccion VARCHAR(50)
)
BEGIN
    IF p_tipo_transaccion IS NULL OR p_tipo_transaccion = '' THEN
        SELECT t.*, s.nombre AS nombre_subcategoria
        FROM transaccion t
        LEFT JOIN subcategoria s ON t.id_subcategoria = s.id_subcategoria
        WHERE t.id_presupuesto = p_id_presupuesto
        ORDER BY t.fecha DESC;
    ELSE
        SELECT t.*, s.nombre AS nombre_subcategoria
        FROM transaccion t
        LEFT JOIN subcategoria s ON t.id_subcategoria = s.id_subcategoria
        WHERE t.id_presupuesto = p_id_presupuesto AND t.tipo_transaccion = p_tipo_transaccion
        ORDER BY t.fecha DESC;
    END IF;
END $$
DELIMITER ;