DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_subcategoria(
    IN p_id_subcategoria VARCHAR(30),
    IN p_id_categoria VARCHAR(30),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(255)
)
BEGIN
    INSERT INTO subcategoria(
        id_subcategoria,
        id_categoria,
        nombre,
        descripcion
    )VALUES (
        p_id_subcategoria,
        p_id_categoria,
        p_nombre,
        p_descripcion
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_subcategoria (
    IN p_id_subcategoria VARCHAR(30),
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(255),
    IN p_activa BOOLEAN
)
BEGIN
    UPDATE subcategoria
    SET nombre = p_nombre,
        descripcion = p_descripcion,
        activa = p_activa
    WHERE id_subcategoria = p_id_subcategoria;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_subcategoria (
    IN p_id_subcategoria VARCHAR(30)
)
BEGIN
    DECLARE en_uso INT DEFAULT 0;

    SELECT COUNT(*) INTO en_uso
    FROM presupuesto_detalle
    WHERE id_subcategoria = p_id_subcategoria;

    -- para ver si tiene transacciones
    IF en_uso = 0 THEN
        SELECT COUNT(*) INTO en_uso FROM transaccion
        WHERE id_subcategoria = p_id_subcategoria;
    END IF;

    -- se vuelve a verificar si no hay nada de verdad
    IF en_uso = 0 THEN
        DELETE FROM subcategoria WHERE id_subcategoria = p_id_subcategoria;
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_subcategoria (
    IN p_id_subcategoria VARCHAR(30)
)
BEGIN
    SELECT s.*, c.nombre AS nombre_categoria, c.tipo_categoria 
    FROM subcategoria s
    INNER JOIN categoria c ON s.id_categoria = c.id_categoria
    WHERE s.id_subcategoria = p_id_subcategoria;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_subcategoria (
    IN p_id_categoria VARCHAR(30)
)
BEGIN
    SELECT * FROM subcategoria
    WHERE id_categoria = p_id_categoria
    ORDER BY nombre ASC;
END $$
DELIMITER ;