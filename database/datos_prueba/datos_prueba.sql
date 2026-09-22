-- =====================================================================
-- 1. Creacion de usuarios, presupuestos y categorias
-- =====================================================================

-- usuarios
INSERT INTO usuario (nombres, apellidos, correo, salario_base, estado, fecha_registro, creado_por) VALUES 
('Leonardo', 'Lopez', 'leonardo@mail.com', 30000.00, 'Activo', CURRENT_TIMESTAMP, 'Said Napky'),
('Aaron', 'Cerrato', 'aaron@mail.com', 45000.00, 'Activo', CURRENT_TIMESTAMP, 'Elkin Cruz'),
('Samuel', 'Vasquez', 'samuel@mail.com', 20000.00, 'Activo', CURRENT_TIMESTAMP, 'Fernando Madrid');

-- presupuesto
INSERT INTO presupuesto (id_usuario, nombre_descriptivo, anio_inicio, mes_inicio, anio_fin, mes_fin, estado_presupuesto, creado_por) VALUES 
('usr_01', 'Presupuesto Base 2024', 2024, 1, 2024, 12, 'ACTIVO', 'Elkin Cruz'),
('usr_02', 'Presupuesto Ejecutivo 2024', 2024, 1, 2024, 12, 'ACTIVO', 'Fernando Madrid'),
('usr_03', 'Presupuesto Estudiantil 2024', 2024, 1, 2024, 12, 'ACTIVO', 'Said Napky');

-- categorias
INSERT INTO categoria (nombre, descripcion, tipo_categoria, orden, creado_por) VALUES
('Ingresos', 'Sueldo y bonos', 'Ingreso', 1, 'Said Napky'),
('Vivienda', 'Gastos fijos del hogar', 'Gasto', 2, 'Fernando Madrid'),
('Alimentacion', 'Comida y despensa', 'Gasto', 3, 'Elkin Cruz'),
('Transporte', 'Movilidad diaria', 'Gasto', 4, 'Said Napky'),
('Educacion', 'Universidad y cursos', 'Gasto', 5, 'Fernando Madrid'),
('Deudas', 'Prestamos y tarjetas', 'Gasto', 6, 'Elkin Cruz');

-- subcategorias
INSERT INTO subcategoria (id_categoria, nombre, descripcion, creado_por) VALUES
('cat_01', 'Salario Base', 'Pago de planilla', 'Fernando Madrid'),
('cat_02', 'Alquiler', 'Pago de renta mensual', 'Said Napky'),
('cat_02', 'Energia Electrica', 'Recibo de la ENEE', 'Elkin Cruz'),
('cat_03', 'Supermercado', 'Compras de quincena', 'Said Napky'),
('cat_04', 'Gasolina', 'Combustible del carro', 'Fernando Madrid'),
('cat_04', 'Transporte Publico', 'Buses y taxis', 'Elkin Cruz'),
('cat_05', 'Mensualidad Universidad', 'Pago cuota mensual', 'Said Napky'),
('cat_06', 'Cuota Vehiculo', 'Prestamo de auto', 'Fernando Madrid');

-- =====================================================================
-- 2. Presupuesto detalle y obligacion fija
-- =====================================================================

-- presupuesto detalle
INSERT INTO presupuesto_detalle (id_presupuesto, id_subcategoria, monto_mensual, observaciones_monto, creado_por) VALUES
('pres_01', 'sbc_01', 30000.00, 'Ingreso esperado', 'Elkin Cruz'),
('pres_01', 'sbc_02', 10000.00, 'Renta fija', 'Said Napky'),
('pres_01', 'sbc_03', 1500.00, 'Promedio de luz', 'Fernando Madrid'),
('pres_02', 'sbc_01', 45000.00, 'Ingreso alto', 'Said Napky'),
('pres_02', 'sbc_02', 12000.00, 'Apartamento premium', 'Elkin Cruz'),
('pres_02', 'sbc_08', 8000.00, 'Cuota de camioneta', 'Fernando Madrid'),
('pres_03', 'sbc_01', 20000.00, 'Sueldo junior', 'Said Napky'),
('pres_03', 'sbc_07', 3000.00, 'Universidad', 'Fernando Madrid'),
('pres_03', 'sbc_06', 1500.00, 'Buses al mes', 'Elkin Cruz');

-- obligacion fija
INSERT INTO obligacion_fija (id_usuario, id_subcategoria, nombre, descripcion, monto_mensual, dia_vencimiento, fecha_inicio, fecha_finalizacion, creado_por) VALUES
('usr_01', 'sbc_02', 'Alquiler Leonardo', 'Renta mes', 10000.00, 5, '2024-01-01', '2024-12-31', 'Elkin Cruz'),
('usr_01', 'sbc_03', 'Luz Leonardo', 'Recibo ENEE', 1500.00, 15, '2024-01-01', '2024-12-31', 'Said Napky'),
('usr_02', 'sbc_08', 'Carro Aaron', 'Prestamo banco', 8000.00, 10, '2024-01-01', '2024-12-31', 'Fernando Madrid'),
('usr_02', 'sbc_02', 'Alquiler Aaron', 'Renta premium', 12000.00, 1, '2024-01-01', '2024-12-31', 'Said Napky'),
('usr_03', 'sbc_07', 'U Samuel', 'Mensualidad UNITEC', 3000.00, 20, '2024-01-01', '2024-12-31', 'Elkin Cruz');

-- =====================================================================
-- 3. Transacciones en mes 1 - Enero 2024
-- =====================================================================

INSERT INTO transaccion (id_usuario, id_presupuesto, anio, mes, id_subcategoria, id_obligacion, tipo_transaccion, descripcion, monto, fecha, metodo_pago, numero_factura, observaciones, creado_por) VALUES
('usr_01', 'pres_01', 2024, 1, 'sbc_01', NULL, 'Ingreso', 'Sueldo Enero', 30000.00, '2024-01-01', 'transferencia', 'FAC-001', 'Pago de planilla correspondiente a la primera quincena y complemento', 'Fernando Madrid'),
('usr_01', 'pres_01', 2024, 1, 'sbc_02', 'obf_01', 'Gasto', 'Pago Renta', 10000.00, '2024-01-04', 'transferencia', 'FAC-002', 'Renta del apartamento del mes de enero', 'Said Napky'),
('usr_01', 'pres_01', 2024, 1, 'sbc_03', 'obf_02', 'Gasto', 'Pago Luz', 1450.00, '2024-01-14', 'tarjeta_debito', 'ENEE-9981', 'Recibo de energia electrica residencial', 'Elkin Cruz'),
('usr_01', 'pres_01', 2024, 1, 'sbc_04', NULL, 'Gasto', 'Super Quincena 1', 4200.00, '2024-01-03', 'tarjeta_credito', 'SUP-4412', 'Compras de despensa para el hogar', 'Fernando Madrid'),
('usr_01', 'pres_01', 2024, 1, 'sbc_05', NULL, 'Gasto', 'Gasolina', 1000.00, '2024-01-08', 'efectivo', 'GAS-1102', 'Combustible para semana laboral', 'Said Napky'),
('usr_01', 'pres_01', 2024, 1, 'sbc_04', NULL, 'Gasto', 'Super Quincena 2', 3500.00, '2024-01-18', 'tarjeta_credito', 'SUP-4590', 'Abastecimiento de viveres', 'Elkin Cruz'),

('usr_02', 'pres_02', 2024, 1, 'sbc_01', NULL, 'Ingreso', 'Sueldo Enero', 45000.00, '2024-01-01', 'transferencia', 'FAC-003', 'Sueldo mensual ejecutivo', 'Said Napky'),
('usr_02', 'pres_02', 2024, 1, 'sbc_02', 'obf_04', 'Gasto', 'Pago Renta', 12000.00, '2024-01-01', 'transferencia', 'FAC-004', 'Renta de zona residencial alta', 'Fernando Madrid'),
('usr_02', 'pres_02', 2024, 1, 'sbc_08', 'obf_03', 'Gasto', 'Cuota Carro', 8000.00, '2024-01-09', 'tarjeta_debito', 'BANCO-01', 'Cuota de financiamiento vehicular', 'Elkin Cruz'),
('usr_02', 'pres_02', 2024, 1, 'sbc_04', NULL, 'Gasto', 'Super Walmart', 5000.00, '2024-01-05', 'tarjeta_credito', 'WALM-883', 'Supermercado general', 'Said Napky'),
('usr_02', 'pres_02', 2024, 1, 'sbc_05', NULL, 'Gasto', 'Gasolina Full', 1500.00, '2024-01-12', 'tarjeta_credito', 'GAS-3321', 'Tanque lleno camioneta', 'Fernando Madrid'),

('usr_03', 'pres_03', 2024, 1, 'sbc_01', NULL, 'Ingreso', 'Sueldo Enero', 20000.00, '2024-01-01', 'transferencia', 'FAC-005', 'Ingreso por pasantia y soporte junior', 'Elkin Cruz'),
('usr_03', 'pres_03', 2024, 1, 'sbc_07', 'obf_05', 'Gasto', 'Mensualidad U', 3000.00, '2024-01-19', 'tarjeta_debito', 'UNI-554', 'Pago cuota mensual universitaria', 'Said Napky'),
('usr_03', 'pres_03', 2024, 1, 'sbc_04', NULL, 'Gasto', 'Despensa', 2500.00, '2024-01-02', 'efectivo', 'PULGA-12', 'Articulos de despensa basica', 'Fernando Madrid'),
('usr_03', 'pres_03', 2024, 1, 'sbc_06', NULL, 'Gasto', 'Saldo tarjeta bus', 500.00, '2024-01-04', 'efectivo', 'BUS-09', 'Recarga de tarjeta de transporte', 'Said Napky'),
('usr_03', 'pres_03', 2024, 1, 'sbc_03', NULL, 'Gasto', 'Ayuda Luz Casa', 800.00, '2024-01-15', 'efectivo', 'ENEE-112', 'Aporte familiar para servicio electrico', 'Elkin Cruz');

-- =====================================================================
-- 4. Transacciones en mes 2 - Febrero 2024
-- =====================================================================

INSERT INTO transaccion (id_usuario, id_presupuesto, anio, mes, id_subcategoria, id_obligacion, tipo_transaccion, descripcion, monto, fecha, metodo_pago, numero_factura, observaciones, creado_por) VALUES
('usr_01', 'pres_01', 2024, 2, 'sbc_01', NULL, 'Ingreso', 'Sueldo Febrero', 30000.00, '2024-02-01', 'transferencia', 'FAC-006', 'Sueldo mensual regular', 'Fernando Madrid'),
('usr_01', 'pres_01', 2024, 2, 'sbc_02', 'obf_01', 'Gasto', 'Pago Renta', 10000.00, '2024-02-04', 'transferencia', 'FAC-007', 'Renta correspondiente a febrero', 'Said Napky'),
('usr_01', 'pres_01', 2024, 2, 'sbc_03', 'obf_02', 'Gasto', 'Pago Luz', 1650.00, '2024-02-14', 'tarjeta_debito', 'ENEE-5541', 'Recibo con ligero incremento', 'Elkin Cruz'),
('usr_01', 'pres_01', 2024, 2, 'sbc_04', NULL, 'Gasto', 'Super Quincena 1', 4800.00, '2024-02-04', 'tarjeta_credito', 'SUP-9912', 'Compras de viveres y aseo', 'Fernando Madrid'),
('usr_01', 'pres_01', 2024, 2, 'sbc_05', NULL, 'Gasto', 'Gasolina', 900.00, '2024-02-10', 'efectivo', 'GAS-4412', 'Combustible semanal', 'Said Napky'),
('usr_01', 'pres_01', 2024, 2, 'sbc_04', NULL, 'Gasto', 'Super Quincena 2', 2900.00, '2024-02-19', 'tarjeta_credito', 'SUP-9988', 'Complemento de despensa', 'Elkin Cruz'),

('usr_02', 'pres_02', 2024, 2, 'sbc_01', NULL, 'Ingreso', 'Sueldo Febrero', 45000.00, '2024-02-01', 'transferencia', 'FAC-008', 'Sueldo ejecutivo febrero', 'Said Napky'),
('usr_02', 'pres_02', 2024, 2, 'sbc_02', 'obf_04', 'Gasto', 'Pago Renta', 12000.00, '2024-02-01', 'transferencia', 'FAC-009', 'Renta mensual premium', 'Fernando Madrid'),
('usr_02', 'pres_02', 2024, 2, 'sbc_08', 'obf_03', 'Gasto', 'Cuota Carro', 8000.00, '2024-02-09', 'tarjeta_debito', 'BANCO-02', 'Cuota de vehiculo mes 2', 'Elkin Cruz'),
('usr_02', 'pres_02', 2024, 2, 'sbc_04', NULL, 'Gasto', 'Super Walmart', 4500.00, '2024-02-06', 'tarjeta_credito', 'WALM-991', 'Supermercado ejecutivo', 'Said Napky'),
('usr_02', 'pres_02', 2024, 2, 'sbc_05', NULL, 'Gasto', 'Gasolina Full', 1600.00, '2024-02-14', 'tarjeta_credito', 'GAS-7722', 'Combustible alto rendimiento', 'Fernando Madrid'),

('usr_03', 'pres_03', 2024, 2, 'sbc_01', NULL, 'Ingreso', 'Sueldo Febrero', 20000.00, '2024-02-01', 'transferencia', 'FAC-010', 'Ingreso mensual junior', 'Elkin Cruz'),
('usr_03', 'pres_03', 2024, 2, 'sbc_07', 'obf_05', 'Gasto', 'Mensualidad U', 3000.00, '2024-02-18', 'tarjeta_debito', 'UNI-881', 'Cuota universitaria febrero', 'Said Napky'),
('usr_03', 'pres_03', 2024, 2, 'sbc_04', NULL, 'Gasto', 'Despensa', 2000.00, '2024-02-03', 'efectivo', 'PULGA-44', 'Compras menores de comida', 'Fernando Madrid'),
('usr_03', 'pres_03', 2024, 2, 'sbc_06', NULL, 'Gasto', 'Saldo tarjeta bus', 600.00, '2024-02-05', 'efectivo', 'BUS-15', 'Recarga mensual de transporte', 'Said Napky'),
('usr_03', 'pres_03', 2024, 2, 'sbc_03', NULL, 'Gasto', 'Ayuda Luz Casa', 850.00, '2024-02-16', 'efectivo', 'ENEE-332', 'Aporte de luz febrero', 'Elkin Cruz');