DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_obligacion(
    IN p_id_obligacion VARCHAR(30),
    IN p_id_usuario VARCHAR(30),
    IN p_id_subcategoria VARCHAR(30),
    IN p_nombre VARCHAR(255),
    IN p_descripcion VARCHAR(255),
    IN p_monto_mensual DECIMAL(20,2),
    IN p_dia_vencimiento INT,
    IN p_fecha_inicio TIMESTAMP,
    IN p_fecha_finalizacion TIMESTAMP
)
BEGIN
    INSERT INTO obligacion_fija(
        id_obligacion,
        id_usuario,
        id_subcategoria,
        nombre,
        descripcion,
        monto_mensual,
        dia_vencimiento,
        fecha_inicio,
        fecha_finalizacion
    )VALUES (
        p_id_obligacion,
        p_id_usuario,
        p_id_subcategoria,
        p_nombre,
        p_descripcion,
        p_monto_mensual,
        p_dia_vencimiento,
        p_fecha_inicio,
        p_fecha_finalizacion
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_obligacion(
    IN p_id_obligacion VARCHAR(30),
    IN p_id_subcategoria VARCHAR(30),
    IN p_nombre VARCHAR(255),
    IN p_descripcion VARCHAR(255),
    IN p_monto_mensual DECIMAL(20,2),
    IN p_dia_vencimiento INT,
    IN p_fecha_inicio TIMESTAMP,
    IN p_fecha_finalizacion TIMESTAMP
)
BEGIN
    UPDATE obligacion_fija
    SET id_subcategoria = p_id_subcategoria,
        nombre = p_nombre,
        descripcion = p_descripcion,
        monto_mensual = p_monto_mensual,
        dia_vencimiento = p_dia_vencimiento,
        fecha_inicio = p_fecha_inicio,
        fecha_finalizacion = p_fecha_finalizacion
    WHERE id_obligacion = p_id_obligacion;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_obligacion(
    IN p_id_obligacion VARCHAR(30)
)
BEGIN
    UPDATE obligacion_fija
    SET es_vigente = 0 WHERE id_obligacion = p_id_obligacion;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_obligacion(
    IN p_id_obligacion VARCHAR(30)
)
BEGIN
    SELECT o.*, s.nombre AS nombre_subcategoria, id_categoria
    FROM obligacion_fija o
    INNER JOIN subcategoria s ON o.id_subcategoria = s.id_subcategoria
    WHERE o.id_obligacion = p_id_obligacion;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_obligaciones_usuario(
    IN p_id_usuario VARCHAR(30),
    IN p_es_vigente BOOLEAN
)
BEGIN
    IF p_es_vigente IS NULL THEN
        SELECT o.*, s.nombre AS nombre_subcategoria
        FROM obligacion_fija o
        INNER JOIN subcategoria s ON o.id_subcategoria = s.id_subcategoria
        WHERE o.id_usuario = p_id_usuario
        ORDER BY o.dia_vencimiento DESC;
    ELSE
        SELECT o.*, s.nombre AS nombre_subcategoria
        FROM obligacion_fija o
        INNER JOIN subcategoria s ON o.id_subcategoria = s.id_subcategoria
        WHERE o.id_usuario = p_id_usuario AND o.es_vigente = p_es_vigente
        ORDER BY o.dia_vencimiento ASC;
    END IF;
END $$
DELIMITER ;