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
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;

public class reporteObligacionesFrame extends JPanel {

    private Reporteria rp = new Reporteria();
    private reporteriaFrame menuReportes;

    private JTextField txtIdUsuario, txtMes, txtAnio;
    private JComboBox<String> cmbEstado;
    private DefaultTableModel modeloTabla;
    private JTable tablaReporte;
    private ChartPanel chartPanel;
    private JFreeChart graficoActual;
    private JButton btnExportar;

    public reporteObligacionesFrame(reporteriaFrame menuReportes) {
        this.menuReportes = menuReportes;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Cumplimiento de Pagos",
                "Monitoreo de obligaciones fijas mensuales",
                "←  Volver a reportería",
                menuReportes::volverAlMenu), BorderLayout.NORTH);

        add(crearPanelReporte4(), BorderLayout.CENTER);
    }

    private JPanel crearPanelReporte4() {
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

        cmbEstado = new JComboBox<>(new String[]{"Todos", "Pagada", "Pendiente", "Por Vencer", "Vencida"});
        cmbEstado.setPreferredSize(new Dimension(110, 40));

        panelCampos.add(Estilo.crearGrupo("ID Usuario", txtIdUsuario));
        panelCampos.add(Estilo.crearGrupo("Mes", txtMes));
        panelCampos.add(Estilo.crearGrupo("Año", txtAnio));
        panelCampos.add(Estilo.crearGrupo("Estado", cmbEstado));

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

        String[] columnas = {"Obligación", "Categoría", "Monto", "Día Venc.", "Tiempo", "Últ. Pago", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaReporte = new JTable(modeloTabla);
        Estilo.estilizarTabla(tablaReporte);
        
        tablaReporte.getColumnModel().getColumn(6).setCellRenderer(new RenderizadorEstados());

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
        if (idUsuario.isEmpty()) {
            Estilo.mostrarAviso(this, "Por favor ingresa el ID del Usuario.");
            return;
        }

        try {
            int mes = Integer.parseInt(txtMes.getText().trim());
            int anio = Integer.parseInt(txtAnio.getText().trim());
            String estado = cmbEstado.getSelectedItem().toString();

            modeloTabla.setRowCount(0);
            ArrayList<String[]> datos = rp.reporteObligaciones(idUsuario, mes, anio, estado);

            if (datos != null && !datos.isEmpty()) {
                int pagadas = 0, pendientes = 0, porVencer = 0, vencidas = 0;
                
                for (String[] fila : datos) {
                    modeloTabla.addRow(new Object[]{
                            fila[0], fila[1], "L. " + formatearMonto(fila[2]), 
                            fila[3], fila[4], fila[5], fila[6]
                    });
                    
                    switch(fila[6]) {
                        case "Pagada": pagadas++; break;
                        case "Pendiente": pendientes++; break;
                        case "Por Vencer": porVencer++; break;
                        case "Vencida": vencidas++; break;
                    }
                }

                DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
                if(pagadas > 0) dataset.setValue("Pagadas", pagadas);
                if(pendientes > 0) dataset.setValue("Pendientes", pendientes);
                if(porVencer > 0) dataset.setValue("Por Vencer", porVencer);
                if(vencidas > 0) dataset.setValue("Vencidas", vencidas);

                crearGrafico(dataset);
                btnExportar.setEnabled(true);
            } else {
                Estilo.mostrarAviso(this, "No hay obligaciones fijas para este usuario.");
                chartPanel.setChart(null);
                btnExportar.setEnabled(false);
            }
        } catch (Exception e) {
            Estilo.mostrarAviso(this, "Mes y año deben ser numéricos.");
        }
    }

    private void crearGrafico(DefaultPieDataset<String> dataset) {
        graficoActual = ChartFactory.createPieChart(
                "Resumen de Obligaciones",
                dataset,
                true, true, false
        );
        graficoActual.setBackgroundPaint(Color.WHITE);
        
        PiePlot<?> plot = (PiePlot<?>) graficoActual.getPlot();
        
        plot.setSectionPaint("Pagadas", new Color(195, 230, 203));
        plot.setSectionPaint("Pendientes", new Color(255, 238, 186));
        plot.setSectionPaint("Por Vencer", new Color(255, 218, 166));
        plot.setSectionPaint("Vencidas", new Color(245, 198, 203));
        
        chartPanel.setChart(graficoActual);
    }
    
    private class RenderizadorEstados extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String estado = value.toString();
            
            switch (estado) {
                case "Pagada":
                    c.setBackground(new Color(195, 230, 203));
                    break;
                case "Pendiente":
                    c.setBackground(new Color(255, 238, 186));
                    break;
                case "Por Vencer":
                    c.setBackground(new Color(255, 218, 166));
                    break;
                case "Vencida":
                    c.setBackground(new Color(245, 198, 203));
                    break;
                default:
                    c.setBackground(table.getBackground());
            }
            c.setForeground(Color.BLACK);
            if (isSelected) c.setBackground(c.getBackground().darker());
            return c;
        }
    }

    private void exportarPDF() {
        if (graficoActual == null || modeloTabla.getRowCount() == 0) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Reporte_Obligaciones_" + txtIdUsuario.getText().trim() + ".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fc.getSelectedFile()));
                document.open();

                document.add(new Paragraph("Reporte 4: Estado de Obligaciones Fijas"));
                document.add(new Paragraph("ID Usuario: " + txtIdUsuario.getText().trim()));
                document.add(new Paragraph("Periodo: " + txtMes.getText() + "/" + txtAnio.getText()));
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(7);
                pdfTable.setWidthPercentage(100);
                String[] headers = {"Obligación", "Categoría", "Monto", "Día Venc.", "Tiempo", "Últ. Pago", "Estado"};
                for(String h : headers) pdfTable.addCell(h);

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < 7; j++) {
                        pdfTable.addCell(modeloTabla.getValueAt(i, j).toString());
                    }
                }
                document.add(pdfTable);
                document.add(new Paragraph(" "));

                BufferedImage img = graficoActual.createBufferedImage(500, 350);
                Image image = Image.getInstance(writer, img, 1.0f);
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
        try { return String.format(Locale.US, "%,.2f", Double.parseDouble(bruto.trim())); } 
        catch (Exception e) { return bruto; }
    }
}