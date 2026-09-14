DELIMITER $$
CREATE OR REPLACE TRIGGER tr_usuario_bi
BEFORE INSERT ON usuario
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_usuario_bu
BEFORE UPDATE ON usuario
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_presupuesto_bi
BEFORE INSERT ON presupuesto
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_presupuesto_bu
BEFORE UPDATE ON presupuesto
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_categoria_bi
BEFORE INSERT ON categoria
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_categoria_bu
BEFORE UPDATE ON categoria
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_categoria_ai
AFTER INSERT ON categoria
FOR EACH ROW
BEGIN
    INSERT INTO subcategoria (
        id_subcategoria,
        id_categoria,
        nombre,
        descripcion,
        es_defecto,
        creado_por,
        creado_en,
        modificado_por,
        modificado_en
    ) VALUES (
        SUBSTRING(REPLACE(UUID(), '-', ''), 1, 30),
        NEW.id_categoria,
        'General',
        'Subcategoria autogenerada.',
        1,
        NEW.creado_por,
        CURRENT_TIMESTAMP,
        NEW.creado_por,
        CURRENT_TIMESTAMP
    );
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_subcategoria_bi
BEFORE INSERT ON subcategoria
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_subcategoria_bu
BEFORE UPDATE ON subcategoria
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_presupuesto_detalle_bi
BEFORE INSERT ON presupuesto_detalle
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_presupuesto_detalle_bu
BEFORE UPDATE ON presupuesto_detalle
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_obligacion_fija_bi
BEFORE INSERT ON obligacion_fija
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_obligacion_fija_bu
BEFORE UPDATE ON obligacion_fija
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_transaccion_bi
BEFORE INSERT ON transaccion
FOR EACH ROW
BEGIN
    SET NEW.creado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
    SET NEW.modificado_por = NEW.creado_por; 
END $$
DELIMITER ;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_transaccion_bu
BEFORE UPDATE ON transaccion
FOR EACH ROW
BEGIN
    SET NEW.modificado_en = CURRENT_TIMESTAMP;
END $$
DELIMITER ;