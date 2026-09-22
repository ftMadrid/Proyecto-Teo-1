package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.operaciones.Reporteria;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class reporteriaFrame extends JPanel {

    private Reporteria rp = new Reporteria();
    private hubFrame ventanaPrincipal;

    private JTextField txtFechaInicio, txtFechaFin;
    private DefaultTableModel modeloTabla;
    private JTable tablaReporte;
    
    // Aquí después agregaremos el panel para el gráfico de JFreeChart

    public reporteriaFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Reportería y Estadísticas",
                "Analiza el balance financiero de tu presupuesto",
                "←  Volver al menú",
                () -> {
                    ventanaPrincipal.mostrarPanel("Menu");
                }), BorderLayout.NORTH);

        add(crearPanelReporte1(), BorderLayout.CENTER);
    }

    private JPanel crearPanelReporte1() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 16));
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.borde(), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        // Filtros Superiores
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelFiltros.setOpaque(false);
        
        txtFechaInicio = Estilo.crearCampo();
        txtFechaInicio.setPreferredSize(new Dimension(120, 40));
        txtFechaInicio.setText("2024-01-01"); // Valor por defecto

        txtFechaFin = Estilo.crearCampo();
        txtFechaFin.setPreferredSize(new Dimension(120, 40));
        txtFechaFin.setText("2024-12-31"); // Valor por defecto

        panelFiltros.add(Estilo.crearGrupo("Desde (AAAA-MM-DD)", txtFechaInicio));
        panelFiltros.add(Estilo.crearGrupo("Hasta (AAAA-MM-DD)", txtFechaFin));
        
        JButton btnGenerar = Estilo.botonPrimario("Generar Reporte", this::cargarReporte);
        // Ajustamos la altura del botón para que se alinee con los campos
        JPanel contenedorBoton = new JPanel(new BorderLayout());
        contenedorBoton.setOpaque(false);
        contenedorBoton.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        contenedorBoton.add(btnGenerar, BorderLayout.CENTER);
        
        panelFiltros.add(contenedorBoton);

        // Tabla de Resultados
        String[] columnas = {"Mes / Año", "Total Ingresos", "Total Gastos", "Balance Final"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaReporte = new JTable(modeloTabla);
        Estilo.estilizarTabla(tablaReporte);

        tarjeta.add(panelFiltros, BorderLayout.NORTH);
        tarjeta.add(Estilo.crearScroll(tablaReporte), BorderLayout.CENTER);
        
        // Más adelante, aquí en el SOUTH o en un SplitPane meteremos el Gráfico y el botón PDF

        return tarjeta;
    }

    private void cargarReporte() {
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa las fechas de inicio y fin.");
            return;
        }

        modeloTabla.setRowCount(0);
        ArrayList<String> datos = rp.reporteResumenMensual(ventanaPrincipal.getNombreCuenta(), fechaInicio, fechaFin);

        if (datos != null && !datos.isEmpty()) {
            for (String fila : datos) {
                String[] columnas = fila.split(",", -1);
                modeloTabla.addRow(new Object[]{
                    columnas[0], 
                    "L. " + formatearMonto(columnas[1]), 
                    "L. " + formatearMonto(columnas[2]), 
                    "L. " + formatearMonto(columnas[3])
                });
            }
        } else {
            modeloTabla.addRow(new Object[]{"Sin datos", "-", "-", "-"});
        }
    }

    private String formatearMonto(String bruto) {
        try {
            return String.format(Locale.US, "%,.2f", Double.parseDouble(bruto.trim()));
        } catch (NumberFormatException e) {
            return bruto;
        }
    }
}