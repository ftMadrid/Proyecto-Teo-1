# Sistema de Presupuesto Personal

Aplicación para la gestión de presupuesto personal que permite a un usuario planificar, controlar y analizar sus finanzas de manera efectiva. El sistema gestiona ingresos, gastos, obligaciones mensuales y metas de ahorro a través de una aplicación con reportería integrada.

## Objetivo del Proyecto

Aplicar los conocimientos adquiridos en la asignatura de Fundamentos de Sistemas de Bases de Datos (Teoría Base de Datos I) mediante el diseño, implementación y despliegue de una solución completa de gestión financiera personal, con énfasis en la lógica de negocio a nivel de base de datos (procedimientos almacenados y triggers).

## Arquitectura y Tecnologías

El sistema sigue una arquitectura de tres capas:

- **Capa de Presentación (Frontend)**: Java / Swing (Aplicación de escritorio).
- **Capa de Negocio (Backend)**: Java (conexión y llamadas a procedimientos).
- **Capa de Datos (Database)**: MariaDB (toda la lógica de negocio reside aquí mediante procedimientos almacenados, funciones y triggers).

## Modelo de Datos

El sistema cuenta con las siguientes entidades principales, cada una con campos de auditoría obligatorios (`creado_por`, `modificado_por`, `creado_en`, `modificado_en`):

1. **USUARIO**: Persona que gestiona su presupuesto personal.
2. **PRESUPUESTO**: Plan financiero para un período específico con vigencia determinada (meses o años).
3. **CATEGORIA**: Clasificación principal (ingreso, gasto o ahorro).
4. **SUBCATEGORIA**: Clasificación secundaria obligatoria para toda categoría. 
5. **PRESUPUESTO_DETALLE**: Asignación de montos presupuestados mensuales a nivel de subcategoría.
6. **OBLIGACION_FIJA**: Gastos recurrentes mensuales con monto y fecha de vencimiento predecibles.
7. **TRANSACCION**: Registro de todos los movimientos financieros reales (ingresos, gastos, ahorros) independientes de la fecha de registro (flexibilidad contable).

### Lógica de Base de Datos Implementada

- **Procedimientos Almacenados CRUD**: Para todas las tablas principales.
- **Procedimientos de Lógica de Negocio**: 
  - `sp_crear_presupuesto_completo`
  - `sp_registrar_transaccion_completa`
  - `sp_procesar_obligaciones_mes`
  - Cálculos de balance, montos ejecutados y porcentajes de ejecución.
- **Funciones**: Cálculos de promedios, días de vencimiento, validaciones de vigencia, etc.
- **Triggers**: Creación automática de subcategoría "General" al insertar una categoría, validaciones de reglas de negocio y auditoría.

## Módulo de Reportería

El sistema incluye reportes analíticos exportables a PDF:

1. **Resumen Mensual de Ingresos vs Gastos**: Comparativa mensual para evaluar el balance financiero.
2. **Distribución de Gastos por Categoría**: Gráfico circular que muestra el porcentaje del presupuesto destinado a cada categoría.
3. **Análisis de Cumplimiento de Presupuesto**: Comparativa entre presupuesto asignado vs monto gastado.
4. **Estado de Obligaciones Fijas y Pagos**: Monitoreo de cumplimiento de pagos (Pagado, Pendiente, Vencido).

## Estructura del Proyecto

```text
proyecto-presupuesto-personal/
├── README.md             # Descripción del proyecto
├── docs/                 # Documentación y Modelo Relacional
├── database/             # Scripts DDL, procedimientos, funciones, triggers y datos de prueba
├── app/                  # Código fuente de la aplicación (Java/Swing)
```

## Instalación y Requisitos previos

- **Base de Datos**: MariaDB (versión 10.5 o superior)
- **Lenguaje**: JDK 11 o superior
- **IDE**: IntelliJ IDEA, Eclipse, Visual Studio Code, etc.

## Autor

- **Nombre**: Carlos Fernando Madrid Valdiviezo
- **Cuenta**: 22511215
- **Asignatura**: Teoría Base de Datos I