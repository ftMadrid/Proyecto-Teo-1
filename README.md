# Sistema de Presupuesto Personal

Aplicación de escritorio para gestión de presupuesto personal que permite planificar, controlar y analizar finanzas a través de ingresos, gastos, obligaciones fijas y metas de ahorro.

## Tecnologías

- **Base de Datos**: MariaDB
- **Frontend/Backend**: Java / Swing (Aplicación de escritorio)
- **Arquitectura**: Tres capas (Presentación, Negocio, Datos)
- **Reportería**: 6 reportes analíticos con exportación a PDF
 
## Modelo de Datos

El sistema cuenta con las siguientes entidades principales:

1. **USUARIO**: Persona que gestiona su presupuesto
2. **PRESUPUESTO**: Plan financiero con vigencia temporal
3. **CATEGORIA**: Clasificación principal (ingreso/gasto/ahorro)
4. **SUBCATEGORIA**: Clasificación secundaria (obligatoria para toda categoría)
5. **PRESUPUESTO_DETALLE**: Asignación de montos mensuales por subcategoría
6. **OBLIGACION_FIJA**: Gastos recurrentes mensuales
7. **TRANSACCION**: Movimientos financieros reales

### Características del Modelo

- **Campos de auditoría**: Todas las tablas incluyen `creado_por`, `modificado_por`, `creado_en`, `modificado_en`
- **Trigger obligatorio**: Crea automáticamente una subcategoría "General" al insertar una categoría
- **Flexibilidad contable**: Campos año/mes en transacciones modificables por el usuario

## Reportes Implementados

1. Resumen Mensual de Ingresos vs Gastos
2. Distribución de Gastos por Categoría
3. Análisis de Cumplimiento de Presupuesto por Categoría y Subcategoría
4. Estado de Obligaciones Fijas y Cumplimiento de Pagos

## Instalación

### Requisitos previos

- MariaDB (versión 10.5 o superior)
- JDK 11 o superior
- IDE (IntelliJ, Eclipse, NetBeans) o compilador Java

## Autor
- **Nombre**: Carlos Fernando Madrid Valdiviezo
- **Cuenta**: 22511215
- **Asignatura**: Teoria Base de Datos I