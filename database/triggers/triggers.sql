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
        es_predeterminada,
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

-- Seccion de sequences

CREATE SEQUENCE IF NOT EXISTS seq_usuario 
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_usuario_generar_id
BEFORE INSERT ON usuario
FOR EACH ROW
BEGIN
    IF NEW.id_usuario IS NULL OR NEW.id_usuario = '' THEN
        SET NEW.id_usuario = CONCAT('usr_', LPAD(NEXTVAL(seq_usuario), 2, '0'));
    END IF;
END $$
DELIMITER ;

CREATE SEQUENCE IF NOT EXISTS seq_presupuesto
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_presupuesto_generar_id
BEFORE INSERT ON presupuesto
FOR EACH ROW
BEGIN
    IF NEW.id_presupuesto IS NULL OR NEW.id_presupuesto = '' THEN
        SET NEW.id_presupuesto = CONCAT('pres_', LPAD(NEXTVAL(seq_presupuesto), 2, '0'));
    END IF;
END $$
DELIMITER ;

CREATE SEQUENCE IF NOT EXISTS seq_categoria
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_categoria_generar_id
BEFORE INSERT ON categoria
FOR EACH ROW
BEGIN
    IF NEW.id_categoria IS NULL OR NEW.id_categoria = '' THEN
        SET NEW.id_categoria = CONCAT('cat_', LPAD(NEXTVAL(seq_categoria), 2, '0'));
    END IF;
END $$
DELIMITER ;

CREATE SEQUENCE IF NOT EXISTS seq_subcategoria
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_subcategoria_generar_id
BEFORE INSERT ON subcategoria
FOR EACH ROW
BEGIN
    IF NEW.id_subcategoria IS NULL OR NEW.id_subcategoria = '' THEN
        SET NEW.id_subcategoria = CONCAT('sbc_', LPAD(NEXTVAL(seq_subcategoria), 2, '0'));
    END IF;
END $$
DELIMITER ;

CREATE SEQUENCE IF NOT EXISTS seq_presupuesto_detalle
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_presupuesto_detalle_generar_id
BEFORE INSERT ON presupuesto_detalle
FOR EACH ROW
BEGIN
    IF NEW.id_presupuesto_detalle IS NULL OR NEW.id_presupuesto_detalle = '' THEN
        SET NEW.id_presupuesto_detalle = CONCAT('det_', LPAD(NEXTVAL(seq_presupuesto_detalle), 2, '0'));
    END IF;
END $$
DELIMITER ;

CREATE SEQUENCE IF NOT EXISTS seq_obligacion_fija
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_obligacion_fija_generar_id
BEFORE INSERT ON obligacion_fija
FOR EACH ROW
BEGIN
    IF NEW.id_obligacion_fija IS NULL OR NEW.id_obligacion_fija = '' THEN
        SET NEW.id_obligacion_fija = CONCAT('obf_', LPAD(NEXTVAL(seq_obligacion_fija), 2, '0'));
    END IF;
END $$
DELIMITER ;

CREATE SEQUENCE IF NOT EXISTS seq_transaccion
START WITH 1 
INCREMENT BY 1;

DELIMITER $$
CREATE OR REPLACE TRIGGER tr_transaccion_generar_id
BEFORE INSERT ON transaccion
FOR EACH ROW
BEGIN
    IF NEW.id_transaccion IS NULL OR NEW.id_transaccion = '' THEN
        SET NEW.id_transaccion = CONCAT('tra_', LPAD(NEXTVAL(seq_transaccion), 2, '0'));
    END IF;
END $$
DELIMITER ;