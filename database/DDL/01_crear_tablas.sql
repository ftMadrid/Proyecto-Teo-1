CREATE OR REPLACE TABLE `usuario` (
	`id_usuario` VARCHAR(30) UNSIGNED NOT NULL,
	`nombres` VARCHAR(255),
	`apellidos` VARCHAR(255),
	`correo` VARCHAR(100) UNIQUE,
	`fecha_registro` TIMESTAMP,
	`salario_base` DECIMAL(20, 2) DEFAULT 0.00 CHECK(salario_base >= 0),
	`estado` VARCHAR(10) DEFAULT 'Activo' CHECK(estado = 'Activo' OR estado = 'Inactivo'),
	PRIMARY KEY(`id_usuario`)
);

CREATE OR REPLACE TABLE `presupuesto` (
	`id_presupuesto` VARCHAR(30) UNSIGNED NOT NULL,
	`id_usuario` VARCHAR(30) NOT NULL,
	`nombre_descriptivo` VARCHAR(255),
	`anio_inicio` INTEGER,
	`mes_fin` INTEGER CHECK(mes_fin >= 1 AND mes_fin <= 12),
	`total_ingresos` DECIMAL(20, 2) DEFAULT 0.00 CHECK(total_ingresos >= (total_gastos + total_ahorro)),
	`total_gastos` DECIMAL(20, 2) DEFAULT 0.00,
	`total_ahorro` DECIMAL(20, 2) DEFAULT 0.00,
	`fecha_creacion` TIMESTAMP,
	`estado_presupuesto` VARCHAR(12) CHECK(estado_presupuesto = 'Activo' OR estado_presupuesto = 'Cerrado' OR estado_presupuesto = 'Borrador'),
	`mes_inicio` INTEGER CHECK(mes_inicio >= 1 AND mes_inicio <= 12),
	`anio_fin` INTEGER CHECK(anio_fin > anio_inicio OR (anio_fin = anio_inicio AND mes_fin >= mes_inicio)),
	PRIMARY KEY(`id_presupuesto`)
);

CREATE OR REPLACE TABLE `categoria` (
	`id_categoria` VARCHAR(30) UNSIGNED NOT NULL,
	`nombre` VARCHAR(100),
	`descripcion` VARCHAR(255),
	`tipo_categoria` VARCHAR(12) CHECK(tipo_categoria = 'Ingreso' OR tipo_categoria = 'Gasto' OR tipo_categoria = 'Ahorro'),
	`orden` INTEGER,
	PRIMARY KEY(`id_categoria`)
);

CREATE OR REPLACE TABLE `subcategoria` (
	`id_subcategoria` VARCHAR(30) UNSIGNED NOT NULL,
	`id_categoria` VARCHAR(30) NOT NULL,
	`nombre` VARCHAR(100),
	`descripcion` VARCHAR(255),
	`activa` BOOLEAN DEFAULT 1,
	`es_predeterminada` BOOLEAN DEFAULT 0,
	PRIMARY KEY(`id_subcategoria`)
);

CREATE OR REPLACE TABLE `presupuesto_detalle` (
	`id_presupuesto_detalle` VARCHAR(30) UNSIGNED NOT NULL,
	`id_presupuesto` VARCHAR(30) NOT NULL,
	`id_subcategoria` VARCHAR(30) NOT NULL,
	`monto_mensual` DECIMAL(20, 2) DEFAULT 0.00,
	`observaciones_monto` VARCHAR(255),
	PRIMARY KEY(`id_presupuesto_detalle`)
);

CREATE OR REPLACE TABLE `obligacion_fija` (
	`id_obligacion` VARCHAR(30) UNSIGNED NOT NULL,
	`id_usuario` VARCHAR(30) NOT NULL,
	`id_subcategoria` VARCHAR(30) NOT NULL,
	`nombre` VARCHAR(255),
	`descripcion` VARCHAR(255),
	`monto_mensual` DECIMAL(20, 2) DEFAULT 0.00,
	`dia_vencimiento` INTEGER CHECK(dia_vencimiento >= 1 AND dia_vencimiento <= 31),
	`es_vigente` BOOLEAN DEFAULT 1,
	`fecha_inicio` TIMESTAMP,
	`fecha_finalizacion` TIMESTAMP,
	PRIMARY KEY(`id_obligacion`)
);

CREATE OR REPLACE TABLE `transaccion` (
	`id_transaccion` VARCHAR(30) UNSIGNED NOT NULL,
	`id_usuario` VARCHAR(30),
	`id_presupuesto` VARCHAR(30),
	`anio` INTEGER,
	`mes` INTEGER CHECK(mes >= 1 AND mes <= 12),
	`id_subcategoria` VARCHAR(30),
	`id_obligacion` VARCHAR(30),
	`tipo_transaccion` VARCHAR(50) CHECK(tipo_transaccion = 'Ingreso' OR tipo_transaccion = 'Gasto' OR tipo_transaccion = 'Ahorro'),
	`descripcion` VARCHAR(255),
	`monto` DECIMAL(20, 2) DEFAULT 0.00,
	`fecha` TIMESTAMP,
	`metodo_pago` VARCHAR(100) CHECK(metodo_pago = 'efectivo' OR metodo_pago = 'tarjeta_debito' OR metodo_pago = 'tarjeta_credito' OR metodo_pago = 'transferencia'),
	`numero_factura` VARCHAR(30),
	`observaciones` VARCHAR(255),
	`fecha_registro` TIMESTAMP,
	PRIMARY KEY(`id_transaccion`)
);

ALTER TABLE `subcategoria`
ADD FOREIGN KEY(`id_categoria`) REFERENCES `categoria`(`id_categoria`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `presupuesto`
ADD FOREIGN KEY(`id_usuario`) REFERENCES `usuario`(`id_usuario`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `presupuesto_detalle`
ADD FOREIGN KEY(`id_presupuesto`) REFERENCES `presupuesto`(`id_presupuesto`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `presupuesto_detalle`
ADD FOREIGN KEY(`id_subcategoria`) REFERENCES `subcategoria`(`id_subcategoria`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `obligacion_fija`
ADD FOREIGN KEY(`id_usuario`) REFERENCES `usuario`(`id_usuario`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `obligacion_fija`
ADD FOREIGN KEY(`id_subcategoria`) REFERENCES `subcategoria`(`id_subcategoria`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `transaccion`
ADD FOREIGN KEY(`id_usuario`) REFERENCES `usuario`(`id_usuario`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `transaccion`
ADD FOREIGN KEY(`id_presupuesto`) REFERENCES `presupuesto`(`id_presupuesto`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `transaccion`
ADD FOREIGN KEY(`id_subcategoria`) REFERENCES `subcategoria`(`id_subcategoria`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `transaccion`
ADD FOREIGN KEY(`id_obligacion`) REFERENCES `obligacion_fija`(`id_obligacion`)
ON UPDATE NO ACTION ON DELETE NO ACTION;