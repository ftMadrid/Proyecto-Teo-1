package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.operaciones.Reporteria;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;

public class reporteEjecucionFrame extends JPanel {

    private Reporteria rp = new Reporteria();
    private reporteriaFrame menuReportes;

    private JTextField txtIdUsuario, txtMes, txtAnio;
    private JComboBox<String> cmbTipo;
    private DefaultTableModel modeloTabla;
    private JTable tablaReporte;
    private ChartPanel chartPanel;
    private JFreeChart graficoActual;
    private JButton btnExportar;

    public reporteEjecucionFrame(reporteriaFrame menuReportes) {
        this.menuReportes = menuReportes;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Ejecución Presupuestaria",
                "Análisis de cumplimiento por categoría y subcategoría",
                "←  Volver a reportería",
                menuReportes::volverAlMenu), BorderLayout.NORTH);

        add(crearPanelReporte3(), BorderLayout.CENTER);
    }

    private JPanel crearPanelReporte3() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 16));
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.borde(), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JPanel panelFiltros = new JPanel(new BorderLayout());
        panelFiltros.setOpaque(false);

        JPanel panelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelCampos.setOpaque(false);

        LocalDate hoy = LocalDate.now();

        txtIdUsuario = Estilo.crearCampo();
        txtIdUsuario.setPreferredSize(new Dimension(90, 40));

        txtMes = Estilo.crearCampo();
        txtMes.setPreferredSize(new Dimension(60, 40));
        txtMes.setText(String.valueOf(hoy.getMonthValue())); 

        txtAnio = Estilo.crearCampo();
        txtAnio.setPreferredSize(new Dimension(70, 40));
        txtAnio.setText(String.valueOf(hoy.getYear())); 

        cmbTipo = new JComboBox<>(new String[]{"Todas", "Gasto", "Ingreso", "Ahorro"});
        cmbTipo.setPreferredSize(new Dimension(100, 40));

        panelCampos.add(Estilo.crearGrupo("ID Usuario", txtIdUsuario));
        panelCampos.add(Estilo.crearGrupo("Mes", txtMes));
        panelCampos.add(Estilo.crearGrupo("Año", txtAnio));
        panelCampos.add(Estilo.crearGrupo("Tipo", cmbTipo));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));

        JButton btnGenerar = Estilo.botonPrimario("Generar", this::cargarReporte);

        btnExportar = Estilo.botonSecundario("PDF", this::exportarPDF);
        btnExportar.setEnabled(false);

        panelBotones.add(btnGenerar);
        panelBotones.add(btnExportar);

        panelFiltros.add(panelCampos, BorderLayout.CENTER);
        panelFiltros.add(panelBotones, BorderLayout.EAST);

        String[] columnas = {"Categoría", "Subcategoría", "Presupuestado", "Ejecutado", "Diferencia", "% Consumido"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaReporte = new JTable(modeloTabla);
        Estilo.estilizarTabla(tablaReporte);
        
        tablaReporte.getColumnModel().getColumn(5).setCellRenderer(new RenderizadorColores());

        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(800, 300));
        chartPanel.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                Estilo.crearScroll(tablaReporte), chartPanel);
        splitPane.setResizeWeight(0.5);
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
        String tipo = cmbTipo.getSelectedItem().toString();

        if (idUsuario.isEmpty()) {
            Estilo.mostrarAviso(this, "Por favor ingresa el ID del Usuario.");
            return;
        }

        try {
            int mes = Integer.parseInt(mesTexto);
            int anio = Integer.parseInt(anioTexto);

            modeloTabla.setRowCount(0);
            ArrayList<String[]> datos = rp.reporteEjecucionCompleto(idUsuario, mes, anio, tipo);

            if (datos != null && !datos.isEmpty()) {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                
                for (String[] fila : datos) {
                    double pres = Double.parseDouble(fila[2]);
                    double ejec = Double.parseDouble(fila[3]);
                    double dif = Double.parseDouble(fila[4]);
                    double porc = Double.parseDouble(fila[5]);

                    modeloTabla.addRow(new Object[]{
                            fila[0],
                            fila[1],
                            "L. " + formatearMonto(fila[2]),
                            "L. " + formatearMonto(fila[3]),
                            "L. " + formatearMonto(fila[4]),
                            formatearMonto(String.valueOf(porc)) + "%"
                    });
                    
                    dataset.addValue(pres, "Presupuestado", fila[1]);
                    dataset.addValue(ejec, "Ejecutado", fila[1]);
                }

                crearGrafico(dataset);
                btnExportar.setEnabled(true);
            } else {
                Estilo.mostrarAviso(this, "No hay datos para estos filtros.");
                chartPanel.setChart(null);
                btnExportar.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            Estilo.mostrarAviso(this, "Mes y año deben ser numéricos.");
        }
    }

    private void crearGrafico(DefaultCategoryDataset dataset) {
        graficoActual = ChartFactory.createBarChart(
                "Comparativa Presupuestado vs Ejecutado",
                "Subcategoría",
                "Monto (L.)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        graficoActual.setBackgroundPaint(Color.WHITE);
        chartPanel.setChart(graficoActual);
    }
    
    private class RenderizadorColores extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            try {
                String valStr = value.toString().replace("%", "").replace(",", "");
                double porcentaje = Double.parseDouble(valStr);
                
                if (porcentaje < 80) {
                    c.setBackground(new Color(195, 230, 203));
                    c.setForeground(Color.BLACK);
                } else if (porcentaje <= 100) {
                    c.setBackground(new Color(255, 238, 186));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(245, 198, 203));
                    c.setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    c.setBackground(c.getBackground().darker());
                }
            } catch (Exception e) {
                c.setBackground(table.getBackground());
                c.setForeground(table.getForeground());
            }
            return c;
        }
    }

    private void exportarPDF() {
        if (graficoActual == null || modeloTabla.getRowCount() == 0) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Reporte_Ejecucion_" + txtIdUsuario.getText().trim() + ".pdf"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Reporte 3: Analisis de Cumplimiento de Presupuesto"));
                document.add(new Paragraph("Generado para ID Usuario: " + txtIdUsuario.getText().trim()));
                document.add(new Paragraph("Periodo: " + txtMes.getText() + "/" + txtAnio.getText()));
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(6);
                pdfTable.setWidthPercentage(100);
                String[] headers = {"Categoria", "Subcategoria", "Presupuestado", "Ejecutado", "Diferencia", "% Consumido"};
                for(String h : headers) pdfTable.addCell(h);

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < 6; j++) {
                        pdfTable.addCell(modeloTabla.getValueAt(i, j).toString());
                    }
                }
                document.add(pdfTable);
                document.add(new Paragraph(" "));

                BufferedImage bufferedImage = graficoActual.createBufferedImage(500, 350);
                Image image = Image.getInstance(writer, bufferedImage, 1.0f);
                image.setAlignment(Image.ALIGN_CENTER);
                document.add(image);

                document.close();
                Estilo.mostrarInfo(this, "PDF exportado exitosamente.");
            } catch (Exception e) {
                Estilo.mostrarError(this, "Error PDF: " + e.getMessage());
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