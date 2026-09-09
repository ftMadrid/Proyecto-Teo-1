DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_presupuesto_detalle(
    IN p_id_presupuesto_detalle VARCHAR(30),
    IN p_id_presupuesto VARCHAR(30),
    IN p_id_subcategoria VARCHAR(30),
    IN p_monto_mensual DECIMAL(20,2),
    IN p_observaciones_monto VARCHAR(255)
)
BEGIN
    INSERT INTO presupuesto_detalle(
        id_presupuesto_detalle,
        id_presupuesto,
        id_subcategoria,
        monto_mensual,
        observaciones_monto
    )VALUES (
        p_id_presupuesto_detalle,
        p_id_presupuesto,
        p_id_subcategoria,
        p_monto_mensual,
        p_observaciones_monto
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_presupuesto_detalle(
    IN p_id_presupuesto_detalle VARCHAR(30),
    IN p_monto_mensual DECIMAL(20,2),
    IN p_observaciones_monto VARCHAR(255)
)
BEGIN
    UPDATE presupuesto_detalle
    SET monto_mensual = p_monto_mensual,
        observaciones_monto = p_observaciones_monto
    WHERE id_presupuesto_detalle = p_id_presupuesto_detalle;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_presupuesto_detalle(
    IN p_id_presupuesto_detalle VARCHAR(30)
)
BEGIN
    DELETE FROM presupuesto_detalle
    WHERE id_presupuesto_detalle = p_id_presupuesto_detalle;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_presupuesto_detalle(
    IN p_id_presupuesto_detalle VARCHAR(30)
)
BEGIN
    SELECT pd.*, s.nombre AS nombre_subcategoria, c.id_categoria,
           c.nombre AS nombre_categoria, c.tipo_categoria
    FROM presupuesto_detalle pd
    INNER JOIN subcategoria s ON pd.id_subcategoria = s.id_subcategoria
    INNER JOIN categoria c ON s.id_categoria = c.id_categoria
    WHERE pd.id_presupuesto_detalle = p_id_presupuesto_detalle;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_detalles_presupuesto(
    IN p_id_presupuesto VARCHAR(30)
)
BEGIN
    SELECT pd.*, s.nombre AS nombre_subcategoria
    FROM presupuesto_detalle pd
    INNER JOIN subcategoria s on pd.id_subcategoria = s.id_subcategoria
    WHERE pd.id_presupuesto = p_id_presupuesto
    ORDER BY s.nombre ASC;
END $$
DELIMITER ;