package dev.presupuesto.frontend.frames;

import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class reporteriaFrame extends JPanel {

    private static final String CARD_MENU = "MenuReportes";
    private static final String CARD_RESUMEN = "Resumen";
    private static final String CARD_DISTRIBUCION = "Distribucion";
    private static final String CARD_CUMPLIMIENTO = "Ejecucion";
    private static final String CARD_OBLIGACIONES = "Obligaciones";

    private enum IconoReporte {
        RESUMEN, DISTRIBUCION, CUMPLIMIENTO, OBLIGACIONES
    }

    private hubFrame ventanaPrincipal;

    private CardLayout cardReportes;
    private JPanel panelReportes;

    public reporteriaFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());

        cardReportes = new CardLayout();
        panelReportes = new JPanel(cardReportes);
        panelReportes.setOpaque(false);

        panelReportes.add(crearMenuReportes(), CARD_MENU);
        panelReportes.add(new reporteResumenMensualFrame(this), CARD_RESUMEN);
        panelReportes.add(new reporteDistribucionGastosFrame(this), CARD_DISTRIBUCION);
        panelReportes.add(new reporteEjecucionFrame(this), CARD_CUMPLIMIENTO);
        panelReportes.add(new reporteObligacionesFrame(this), CARD_OBLIGACIONES);

        add(panelReportes, BorderLayout.CENTER);
        cardReportes.show(panelReportes, CARD_MENU);
    }

    public hubFrame getVentanaPrincipal() {
        return ventanaPrincipal;
    }

    public void volverAlMenu() {
        cardReportes.show(panelReportes, CARD_MENU);
    }

    private void mostrarReporte(String card) {
        cardReportes.show(panelReportes, card);
    }

    private JPanel crearMenuReportes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBackground(Tema.fondoPrincipal());
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        panel.add(Estilo.crearEncabezado(
                "Reportería y Estadísticas",
                "Selecciona un reporte para continuar",
                "←  Volver al menú",
                () -> ventanaPrincipal.mostrarPanel("Menu")), BorderLayout.NORTH);

        panel.add(crearSeccionReportes(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearSeccionReportes() {
        JPanel seccion = new JPanel(new BorderLayout());
        seccion.setOpaque(false);

        JLabel labelReportes = new JLabel("REPORTES DISPONIBLES");
        labelReportes.setFont(Estilo.fuente(Font.BOLD, 11));
        labelReportes.setForeground(Tema.textoSecundario());
        labelReportes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        seccion.add(labelReportes, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);

        grid.add(crearTarjeta("Resumen mensual", "Ingresos, gastos y ahorro por mes",
                IconoReporte.RESUMEN, CARD_RESUMEN));
        grid.add(crearTarjeta("Distribución de gastos", "Porcentaje del presupuesto por categoría",
                IconoReporte.DISTRIBUCION, CARD_DISTRIBUCION));
        grid.add(crearTarjeta("Cumplimiento de presupuesto", "Asignado vs. gastado por categoría",
                IconoReporte.CUMPLIMIENTO, CARD_CUMPLIMIENTO));
        grid.add(crearTarjeta("Obligaciones fijas", "Cumplimiento de pagos recurrentes",
                IconoReporte.OBLIGACIONES, CARD_OBLIGACIONES));

        seccion.add(grid, BorderLayout.CENTER);
        return seccion;
    }

    private TarjetaReporte crearTarjeta(String titulo, String descripcion, IconoReporte icono, String card) {
        return new TarjetaReporte(titulo, descripcion, icono, () -> mostrarReporte(card));
    }

    private static class TarjetaReporte extends JPanel {

        private static final int PADDING = 20;
        private static final int TAM_ICONO = 40;

        private final String titulo;
        private final String descripcion;
        private final IconoReporte icono;
        private boolean hover = false;

        TarjetaReporte(String titulo, String descripcion, IconoReporte icono, Runnable accion) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icono = icono;

            setOpaque(true);
            setBackground(Tema.fondoTarjeta());
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && contains(e.getPoint())) {
                        hover = false;
                        repaint();
                        accion.run();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(Tema.fondoTarjeta());
            g2.fillRect(0, 0, w, h);
            g2.setColor(hover ? Tema.boton() : Tema.borde());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(0, 0, w - 1, h - 1);
            if (hover) {
                g2.fillRect(0, 0, w, 3);
            }

            Font fuenteTitulo = Estilo.fuente(Font.BOLD, 14);
            Font fuenteDescripcion = Estilo.fuente(Font.PLAIN, 12);
            FontMetrics fmTitulo = g2.getFontMetrics(fuenteTitulo);
            FontMetrics fmDesc = g2.getFontMetrics(fuenteDescripcion);

            int separacion = 14;
            int bloque = TAM_ICONO + separacion + fmTitulo.getHeight() + 2 + fmDesc.getHeight();
            int y = (h - bloque) / 2;
            int anchoTexto = w - PADDING * 2;

            g2.setColor(hover ? Tema.boton() : Tema.acentoSuave());
            g2.fillRect(PADDING, y, TAM_ICONO, TAM_ICONO);
            dibujarIcono(g2, icono, PADDING + TAM_ICONO / 2.0, y + TAM_ICONO / 2.0,
                    hover ? Color.WHITE : Tema.boton());

            int yTitulo = y + TAM_ICONO + separacion;
            g2.setFont(fuenteTitulo);
            g2.setColor(Tema.textoPrimario());
            g2.drawString(ajustarTexto(titulo, fmTitulo, anchoTexto), PADDING, yTitulo + fmTitulo.getAscent());

            int yDesc = yTitulo + fmTitulo.getHeight() + 2;
            g2.setFont(fuenteDescripcion);
            g2.setColor(Tema.textoSecundario());
            g2.drawString(ajustarTexto(descripcion, fmDesc, anchoTexto), PADDING, yDesc + fmDesc.getAscent());

            g2.dispose();
        }

        private static String ajustarTexto(String texto, FontMetrics fm, int anchoMax) {
            if (fm.stringWidth(texto) <= anchoMax) {
                return texto;
            }
            String elipsis = "…";
            int fin = texto.length();
            while (fin > 0 && fm.stringWidth(texto.substring(0, fin) + elipsis) > anchoMax) {
                fin--;
            }
            return texto.substring(0, fin) + elipsis;
        }

        private static void dibujarIcono(Graphics2D g2, IconoReporte icono, double cx, double cy, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (icono) {
                case RESUMEN:
                    g2.draw(new Line2D.Double(cx - 9, cy + 9, cx + 9, cy + 9));
                    g2.draw(new Line2D.Double(cx - 9, cy - 9, cx - 9, cy + 9));
                    g2.draw(new Rectangle2D.Double(cx - 5, cy + 2, 3, 7));
                    g2.draw(new Rectangle2D.Double(cx - 1, cy - 3, 3, 12));
                    g2.draw(new Rectangle2D.Double(cx + 3, cy - 7, 3, 16));
                    break;

                case DISTRIBUCION:
                    g2.draw(new Ellipse2D.Double(cx - 9, cy - 9, 18, 18));
                    g2.draw(new Line2D.Double(cx, cy, cx, cy - 9));
                    g2.draw(new Line2D.Double(cx, cy, cx + 8, cy + 4));
                    g2.draw(new Line2D.Double(cx, cy, cx - 7, cy + 5));
                    break;

                case CUMPLIMIENTO:
                    g2.draw(new Line2D.Double(cx - 9, cy + 9, cx + 9, cy + 9));
                    g2.draw(new Rectangle2D.Double(cx - 7, cy - 1, 5, 10));
                    g2.draw(new Rectangle2D.Double(cx + 2, cy - 6, 5, 15));
                    g2.draw(new Line2D.Double(cx - 2, cy - 9, cx, cy - 6));
                    g2.draw(new Line2D.Double(cx, cy - 6, cx + 5, cy - 12));
                    break;

                case OBLIGACIONES:
                    g2.draw(new Rectangle2D.Double(cx - 9, cy - 7, 18, 16));
                    g2.draw(new Line2D.Double(cx - 9, cy - 2, cx + 9, cy - 2));
                    g2.draw(new Line2D.Double(cx - 4, cy - 10, cx - 4, cy - 5));
                    g2.draw(new Line2D.Double(cx + 4, cy - 10, cx + 4, cy - 5));
                    break;
            }
        }
    }
}