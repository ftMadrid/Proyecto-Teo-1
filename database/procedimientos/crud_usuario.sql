DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_insertar_usuario (
    IN p_id_usuario VARCHAR(30),
    IN p_nombres VARCHAR(255),
    IN p_apellidos VARCHAR(255),
    IN p_correo VARCHAR(100),
    IN p_salario_base DECIMAL(20, 2)
)
BEGIN
    INSERT INTO usuario (
        id_usuario,
        nombres,
        apellidos,
        correo,
        fecha_registro,
        salario_base,
        estado
    )VALUES (
        p_id_usuario,
        p_nombres,
        p_apellidos,
        p_correo,
        CURDATE(),
        p_salario_base,
        'Activo'
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_actualizar_usuario (
    IN p_id_usuario VARCHAR(30),
    IN p_nombres VARCHAR(255),
    IN p_apellidos VARCHAR(255),
    IN p_correo VARCHAR(100),
    IN p_salario_base DECIMAL(20, 2)
)
BEGIN
    UPDATE usuario
    SET nombres = p_nombres,
        apellidos = p_apellidos,
        correo = p_correo,
        salario_base = p_salario_base
    WHERE id_usuario = p_id_usuario;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_eliminar_usuario (
    IN p_id_usuario VARCHAR(30)
)
BEGIN
    UPDATE usuario
    SET estado = 'Inactivo'
    WHERE id_usuario = p_id_usuario;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_consultar_usuario (
    IN p_id_usuario VARCHAR(30)
)
BEGIN
    SELECT * FROM usuario
    WHERE id_usuario = p_id_usuario;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE PROCEDURE sp_listar_usuarios()
BEGIN
    SELECT * FROM usuario
    ORDER BY fecha_registro DESC;
END $$
DELIMITER ;