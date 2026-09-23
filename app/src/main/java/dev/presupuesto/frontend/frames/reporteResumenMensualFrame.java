package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.operaciones.Reporteria;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;

public class reporteResumenMensualFrame extends JPanel {

    private Reporteria rp = new Reporteria();
    private reporteriaFrame menuReportes;

    private JTextField txtIdUsuario, txtFechaInicio, txtFechaFin;
    private DefaultTableModel modeloTabla;
    private JTable tablaReporte;
    private ChartPanel chartPanel;
    private JFreeChart graficoActual;
    private JButton btnExportar;

    public reporteResumenMensualFrame(reporteriaFrame menuReportes) {
        this.menuReportes = menuReportes;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Resumen Mensual Ingresos vs Gastos",
                "Compara ingresos, gastos y ahorro mes a mes",
                "←  Volver a reportería",
                menuReportes::volverAlMenu), BorderLayout.NORTH);

        add(crearPanelReporte1(), BorderLayout.CENTER);
    }

    private JPanel crearPanelReporte1() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 16));
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.borde(), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JPanel panelFiltros = new JPanel(new BorderLayout());
        panelFiltros.setOpaque(false);

        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelCampos.setOpaque(false);

        txtIdUsuario = Estilo.crearCampo();
        txtIdUsuario.setPreferredSize(new Dimension(100, 40));

        txtFechaInicio = Estilo.crearCampo();
        txtFechaInicio.setPreferredSize(new Dimension(110, 40));
        txtFechaInicio.setText("2024-01-01"); 

        txtFechaFin = Estilo.crearCampo();
        txtFechaFin.setPreferredSize(new Dimension(110, 40));
        txtFechaFin.setText("2024-12-31"); 

        panelCampos.add(Estilo.crearGrupo("ID Usuario", txtIdUsuario));
        panelCampos.add(Estilo.crearGrupo("Desde (AAAA-MM-DD)", txtFechaInicio));
        panelCampos.add(Estilo.crearGrupo("Hasta (AAAA-MM-DD)", txtFechaFin));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JButton btnGenerar = Estilo.botonPrimario("Generar", this::cargarReporte);
        btnGenerar.setPreferredSize(new Dimension(100, 40));

        btnExportar = Estilo.botonSecundario("PDF", this::exportarPDF);
        btnExportar.setPreferredSize(new Dimension(80, 40));
        btnExportar.setEnabled(false);

        panelBotones.add(btnGenerar);
        panelBotones.add(btnExportar);

        panelFiltros.add(panelCampos, BorderLayout.CENTER);
        panelFiltros.add(panelBotones, BorderLayout.EAST);

        String[] columnas = {"Mes / Año", "Total Ingresos", "Total Gastos", "Balance Final"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaReporte = new JTable(modeloTabla);
        Estilo.estilizarTabla(tablaReporte);

        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(800, 300));
        chartPanel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                Estilo.crearScroll(tablaReporte), chartPanel);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerSize(4);
        splitPane.setOpaque(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        tarjeta.add(panelFiltros, BorderLayout.NORTH);
        tarjeta.add(splitPane, BorderLayout.CENTER);

        return tarjeta;
    }

    private void cargarReporte() {
        String idUsuario = txtIdUsuario.getText().trim();
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();

        if (idUsuario.isEmpty()) {
            Estilo.mostrarAviso(this, "Por favor ingresa el ID del usuario (Ej: usr_01).");
            return;
        }

        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa las fechas de inicio y fin.");
            return;
        }

        modeloTabla.setRowCount(0);
        
        ArrayList<String> datos = rp.reporteResumenMensual(idUsuario, fechaInicio, fechaFin);

        if (datos != null && !datos.isEmpty()) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (String fila : datos) {
                String[] columnas = fila.split(",", -1);
                modeloTabla.addRow(new Object[]{
                    columnas[0],
                    "L. " + formatearMonto(columnas[1]),
                    "L. " + formatearMonto(columnas[2]),
                    "L. " + formatearMonto(columnas[3])
                });
                dataset.addValue(Double.parseDouble(columnas[1]), "Ingresos", columnas[0]);
                dataset.addValue(Double.parseDouble(columnas[2]), "Gastos", columnas[0]);
            }
            crearGrafico(dataset);
            btnExportar.setEnabled(true);
        } else {
            modeloTabla.addRow(new Object[]{"Sin datos", "-", "-", "-"});
            chartPanel.setChart(null);
            graficoActual = null;
            btnExportar.setEnabled(false);
        }
    }

    private void crearGrafico(DefaultCategoryDataset dataset) {
        graficoActual = ChartFactory.createBarChart(
                "Ingresos vs Gastos Mensuales",
                "Mes / Año",
                "Monto (L.)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        graficoActual.setBackgroundPaint(Color.WHITE);
        chartPanel.setChart(graficoActual);
    }

    private void exportarPDF() {
        if (graficoActual == null || modeloTabla.getRowCount() == 0) {
            Estilo.mostrarAviso(this, "No hay datos para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte PDF");
        fileChooser.setSelectedFile(new File("Reporte_Mensual_" + txtIdUsuario.getText().trim() + ".pdf"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Reporte 1: Resumen Mensual de Ingresos vs Gastos"));
                document.add(new Paragraph("Generado para ID Usuario: " + txtIdUsuario.getText().trim()));
                document.add(new Paragraph("Periodo: " + txtFechaInicio.getText() + " a " + txtFechaFin.getText()));
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(4);
                pdfTable.addCell("Mes / Año");
                pdfTable.addCell("Total Ingresos");
                pdfTable.addCell("Total Gastos");
                pdfTable.addCell("Balance Final");

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < 4; j++) {
                        pdfTable.addCell(modeloTabla.getValueAt(i, j).toString());
                    }
                }
                document.add(pdfTable);
                document.add(new Paragraph(" "));

                BufferedImage bufferedImage = graficoActual.createBufferedImage(600, 400);
                Image image = Image.getInstance(writer, bufferedImage, 1.0f);
                image.scalePercent(80f);
                image.setAlignment(Image.ALIGN_CENTER);
                document.add(image);

                document.close();
                Estilo.mostrarInfo(this, "Reporte exportado exitosamente a PDF.");
            } catch (Exception e) {
                Estilo.mostrarError(this, "Error al generar el PDF: " + e.getMessage());
            }
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