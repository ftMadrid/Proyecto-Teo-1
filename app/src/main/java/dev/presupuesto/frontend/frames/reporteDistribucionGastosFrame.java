package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.operaciones.Reporteria;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;

public class reporteDistribucionGastosFrame extends JPanel {

    private Reporteria rp = new Reporteria();
    private reporteriaFrame menuReportes;

    private JTextField txtIdUsuario, txtMes, txtAnio;
    private DefaultTableModel modeloTabla;
    private JTable tablaReporte;
    private ChartPanel chartPanel;
    private JFreeChart graficoActual;
    private JButton btnExportar;

    public reporteDistribucionGastosFrame(reporteriaFrame menuReportes) {
        this.menuReportes = menuReportes;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Distribución de Gastos por Categoría",
                "Qué porcentaje del presupuesto se destina a cada categoría",
                "←  Volver a reportería",
                menuReportes::volverAlMenu), BorderLayout.NORTH);

        add(crearPanelReporte2(), BorderLayout.CENTER);
    }

    private JPanel crearPanelReporte2() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 16));
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.borde(), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelFiltros.setOpaque(false);

        LocalDate hoy = LocalDate.now();

        txtIdUsuario = Estilo.crearCampo();
        txtIdUsuario.setPreferredSize(new Dimension(120, 40));

        txtMes = Estilo.crearCampo();
        txtMes.setPreferredSize(new Dimension(80, 40));
        txtMes.setText(String.valueOf(hoy.getMonthValue())); 

        txtAnio = Estilo.crearCampo();
        txtAnio.setPreferredSize(new Dimension(100, 40));
        txtAnio.setText(String.valueOf(hoy.getYear())); 

        panelFiltros.add(Estilo.crearGrupo("ID Usuario", txtIdUsuario));
        panelFiltros.add(Estilo.crearGrupo("Mes (1-12)", txtMes));
        panelFiltros.add(Estilo.crearGrupo("Año", txtAnio));

        JButton btnGenerar = Estilo.botonPrimario("Generar Reporte", this::cargarReporte);
        btnExportar = Estilo.botonSecundario("Exportar PDF", this::exportarPDF);
        btnExportar.setEnabled(false);

        JPanel contenedorBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        contenedorBoton.setOpaque(false);
        contenedorBoton.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        contenedorBoton.add(btnGenerar);
        contenedorBoton.add(btnExportar);

        panelFiltros.add(contenedorBoton);

        String[] columnas = {"Categoría", "Monto gastado", "Transacciones", "% del total"};
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
        String mesTexto = txtMes.getText().trim();
        String anioTexto = txtAnio.getText().trim();

        if (idUsuario.isEmpty()) {
            Estilo.mostrarAviso(this, "Por favor ingresa el ID del usuario (Ej: usr_01).");
            return;
        }

        String error = validarMesAnio(mesTexto, anioTexto);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        int mes = Integer.parseInt(mesTexto);
        int anio = Integer.parseInt(anioTexto);

        modeloTabla.setRowCount(0);
        
        ArrayList<String[]> datos = rp.reporteDistribucionGastos(idUsuario, mes, anio);

        if (datos != null && !datos.isEmpty()) {
            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
            for (String[] fila : datos) {
                String categoria = fila[0];
                double monto = Double.parseDouble(fila[1]);
                String transacciones = fila[2];

                modeloTabla.addRow(new Object[]{
                        categoria,
                        "L. " + formatearMonto(fila[1]),
                        transacciones,
                        formatearMonto(fila[3]) + "%"
                });
                dataset.setValue(categoria, monto);
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

    private void crearGrafico(DefaultPieDataset<String> dataset) {
        graficoActual = ChartFactory.createPieChart(
                "Distribución de Gastos por Categoría",
                dataset,
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
        fileChooser.setSelectedFile(new File("Reporte_Distribucion_Gastos_" + txtIdUsuario.getText().trim() + ".pdf"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Reporte 2: Distribución de Gastos por Categoría"));
                document.add(new Paragraph("Generado para ID Usuario: " + txtIdUsuario.getText().trim()));
                document.add(new Paragraph("Periodo: " + txtMes.getText() + "/" + txtAnio.getText()));
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(4);
                pdfTable.addCell("Categoría");
                pdfTable.addCell("Monto gastado");
                pdfTable.addCell("Transacciones");
                pdfTable.addCell("% del total");

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

    private String validarMesAnio(String mesTexto, String anioTexto) {
        try {
            int mes = Integer.parseInt(mesTexto);
            if (mes < 1 || mes > 12) {
                return "El mes debe estar entre 1 y 12.";
            }
        } catch (NumberFormatException e) {
            return "El mes debe ser un número entre 1 y 12.";
        }
        try {
            int anio = Integer.parseInt(anioTexto);
            if (anio < 2000 || anio > 2100) {
                return "El año debe estar entre 2000 y 2100.";
            }
        } catch (NumberFormatException e) {
            return "El año debe ser un número, por ejemplo: 2026";
        }
        return null;
    }

    private String formatearMonto(String bruto) {
        try {
            return String.format(Locale.US, "%,.2f", Double.parseDouble(bruto.trim()));
        } catch (NumberFormatException e) {
            return bruto;
        }
    }
}