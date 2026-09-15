DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_categoria (
    IN p_id_categoria VARCHAR(30),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(255),
    IN p_tipo_categoria VARCHAR(12),
    IN p_orden INT,
    IN p_creado_por VARCHAR(30)
)
BEGIN
    INSERT INTO categoria (
        id_categoria,
        nombre,
        descripcion,
        tipo_categoria,
        orden,
        creado_por
    ) VALUES (
        p_id_categoria,
        p_nombre,
        p_descripcion,
        p_tipo_categoria,
        p_orden,
        p_creado_por
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_categoria (
    IN p_id_categoria VARCHAR(30),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(255),
    IN p_tipo_categoria VARCHAR(12),
    IN p_orden INT,
    IN p_modificado_por VARCHAR(30)
)
BEGIN
    UPDATE categoria
    SET nombre = p_nombre,
        descripcion = p_descripcion,
        tipo_categoria = p_tipo_categoria,
        orden = p_orden,
        modificado_por = p_modificado_por
    WHERE id_categoria = p_id_categoria;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_categoria (
    IN p_id_categoria VARCHAR(30)
)
BEGIN
    DECLARE subcategoria_adicionales INT DEFAULT 0;

    SET subcategoria_adicionales = (
        SELECT COUNT(*)
        FROM subcategoria
        WHERE id_categoria = p_id_categoria AND activa = 1 AND es_predeterminada = 0
    );

    IF subcategoria_adicionales = 0 THEN
        DELETE FROM subcategoria WHERE id_categoria = p_id_categoria;
        DELETE FROM categoria WHERE id_categoria = p_id_categoria;
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede eliminar ya que tiene subcategorias activas'; 
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_categoria (
    IN p_id_categoria VARCHAR(30)
)
BEGIN
    SELECT * FROM categoria
    WHERE id_categoria = p_id_categoria;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_categoria (
    IN p_tipo_categoria VARCHAR(12)
)
BEGIN
    IF p_tipo_categoria IS NULL OR p_tipo_categoria = '' THEN
        SELECT * FROM categoria
        ORDER BY orden ASC;
    ELSE 
        SELECT * FROM categoria
        WHERE tipo_categoria = p_tipo_categoria
        ORDER BY orden ASC;
    END IF;
END $$
DELIMITER ;