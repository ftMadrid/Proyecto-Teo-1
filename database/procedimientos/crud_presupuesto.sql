DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_presupuesto(
    IN p_id_presupuesto VARCHAR(30),
    IN p_id_usuario VARCHAR(30),
    IN p_nombre_descriptivo VARCHAR(255),
    IN p_anio_inicio INT,
    IN p_mes_inicio INT,
    IN p_anio_fin INT,
    IN p_mes_fin INT
)
BEGIN
    INSERT INTO presupuesto (
        id_presupuesto,
        id_usuario,
        nombre_descriptivo,
        anio_inicio,
        mes_inicio,
        anio_fin,
        mes_fin,
        fecha_creacion,
        estado_presupuesto
    )VALUES (
        p_id_presupuesto,
        p_id_usuario,
        p_nombre_descriptivo,
        p_anio_inicio,
        p_mes_inicio,
        p_anio_fin,
        p_mes_fin,
        CURRENT_TIMESTAMP,
        'ACTIVO'
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_presupuesto(
    IN p_id_presupuesto VARCHAR(30),
    IN p_nombre_descriptivo VARCHAR(255),
    IN p_anio_inicio INT,
    IN p_mes_inicio INT,
    IN p_anio_fin INT,
    IN p_mes_fin INT,
    IN p_estado_presupuesto VARCHAR(12)
)
BEGIN
    UPDATE presupuesto
    SET nombre_descriptivo = p_nombre_descriptivo,
        anio_inicio = p_anio_inicio,
        mes_inicio = p_mes_inicio,
        anio_fin = p_anio_fin,
        mes_fin = p_mes_fin,
        estado_presupuesto = p_estado_presupuesto
    WHERE id_presupuesto = p_id_presupuesto;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_presupuesto(
    IN p_id_presupuesto VARCHAR(30)
)
BEGIN
    DECLARE en_uso INT DEFAULT 0;

    SELECT COUNT(*) INTO en_uso FROM transaccion
    WHERE id_presupuesto = p_id_presupuesto;

    IF en_uso = 0 THEN 
        DELETE FROM presupuesto_detalle WHERE id_presupuesto = p_id_presupuesto;
        DELETE FROM presupuesto WHERE id_presupuesto = p_id_presupuesto;
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_presupuesto(
    IN p_id_presupuesto VARCHAR(30)
)
BEGIN
    SELECT * FROM presupuesto
    WHERE id_presupuesto = p_id_presupuesto;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_presupuestos_usuario(
    IN p_id_usuario VARCHAR(30),
    IN p_estado_presupuesto VARCHAR(12)
)
BEGIN
    IF p_estado_presupuesto IS NULL OR p_estado_presupuesto = '' THEN
        SELECT * FROM presupuesto
        WHERE id_usuario = p_id_usuario
        ORDER BY anio_inicio DESC, mes_inicio DESC;
    ELSE
        SELECT * FROM presupuesto
        WHERE id_usuario = p_id_usuario AND estado_presupuesto = p_estado_presupuesto
        ORDER BY anio_inicio DESC, mes_inicio DESC;
    END IF;
END $$
DELIMITER ;