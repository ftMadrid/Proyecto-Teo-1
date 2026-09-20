package dev.presupuesto.frontend.frames;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class hubFrame extends JFrame {

    private static final Color FONDO_PRINCIPAL = new Color(235, 238, 242);
    private static final Color FONDO_TARJETA = new Color(255, 255, 255);
    private static final Color TEXTO_PRIMARIO = new Color(40, 40, 40);
    private static final Color TEXTO_SECUNDARIO = new Color(120, 120, 120);
    private static final Color COLOR_BORDE = new Color(220, 225, 230);
    private static final Color COLOR_BOTON = new Color(0, 110, 255);
    private static final Color COLOR_BOTON_HOVER = new Color(0, 90, 215);
    private static final Color COLOR_ACENTO_SUAVE = new Color(230, 240, 255);

    private static final String FUENTE = "Segoe UI";

    private enum Icono {
        USUARIO, PRESUPUESTO, CATEGORIA, SUBCATEGORIA, DETALLE, OBLIGACION, TRANSACCION, REPORTERIA
    }

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private String nombreCuenta;

    public hubFrame(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
        setTitle("Presupuesto Personal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        JPanel panelMenu = crearPanelMenu();
        usuarioFrame panelUsuario = new usuarioFrame(this);

        panelContenedor.add(panelMenu, "Menu");
        panelContenedor.add(panelUsuario, "Usuario");

        panelContenedor.add(new presupuestoFrame(this), "Presupuesto");
        panelContenedor.add(crearPanelPendiente("Categorías"), "Categoria");
        panelContenedor.add(crearPanelPendiente("Subcategorías"), "Subcategoria");
        panelContenedor.add(crearPanelPendiente("Detalle de presupuesto"), "PresupuestoDetalle");
        panelContenedor.add(crearPanelPendiente("Obligaciones fijas"), "ObligacionFija");
        panelContenedor.add(crearPanelPendiente("Transacciones"), "Transaccion");
        panelContenedor.add(crearPanelPendiente("Reportería"), "Reporteria");

        setContentPane(panelContenedor);
        setVisible(true);
    }


    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO_PRINCIPAL);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        panel.add(crearEncabezado(), BorderLayout.NORTH);
        panel.add(crearSeccionModulos(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel labelBienvenida = new JLabel("¡Bienvenido, " + nombreCuenta + "!");
        labelBienvenida.setFont(new Font(FUENTE, Font.BOLD, 26));
        labelBienvenida.setForeground(TEXTO_PRIMARIO);
        labelBienvenida.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelSubtitulo = new JLabel("Selecciona un módulo para continuar");
        labelSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        labelSubtitulo.setForeground(TEXTO_SECUNDARIO);
        labelSubtitulo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        labelSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(labelBienvenida);
        textos.add(labelSubtitulo);
        encabezado.add(textos, BorderLayout.WEST);

        JPanel contenedorSalir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        contenedorSalir.setOpaque(false);
        contenedorSalir.add(crearBotonSecundario("Cerrar sesión", () -> {
            dispose();
            new loginFrame();
        }));
        encabezado.add(contenedorSalir, BorderLayout.EAST);

        return encabezado;
    }

    private JPanel crearSeccionModulos() {
        JPanel seccion = new JPanel(new BorderLayout());
        seccion.setOpaque(false);

        JLabel labelModulos = new JLabel("MÓDULOS");
        labelModulos.setFont(new Font(FUENTE, Font.BOLD, 11));
        labelModulos.setForeground(TEXTO_SECUNDARIO);
        labelModulos.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        seccion.add(labelModulos, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 3, 16, 16));
        grid.setOpaque(false);

        grid.add(crearTarjeta("Usuarios", "Gestiona las cuentas", Icono.USUARIO, "Usuario"));
        grid.add(crearTarjeta("Presupuestos", "Planifica tus presupuestos", Icono.PRESUPUESTO, "Presupuesto"));
        grid.add(crearTarjeta("Categorías", "Organiza tus gastos", Icono.CATEGORIA, "Categoria"));
        grid.add(crearTarjeta("Subcategorías", "Divide cada categoría", Icono.SUBCATEGORIA, "Subcategoria"));
        grid.add(crearTarjeta("Detalle de presupuesto", "Montos por categoría", Icono.DETALLE, "PresupuestoDetalle"));
        grid.add(crearTarjeta("Obligaciones fijas", "Pagos recurrentes", Icono.OBLIGACION, "ObligacionFija"));
        grid.add(crearTarjeta("Transacciones", "Ingresos y gastos", Icono.TRANSACCION, "Transaccion"));
        grid.add(crearTarjeta("Reportería", "Genera reportes y estadísticas", Icono.REPORTERIA, "Reporteria"));

        seccion.add(grid, BorderLayout.CENTER);
        return seccion;
    }

    private TarjetaModulo crearTarjeta(String titulo, String descripcion, Icono icono, String nombrePanel) {
        return new TarjetaModulo(titulo, descripcion, icono, () -> mostrarPanel(nombrePanel));
    }


    private JPanel crearPanelPendiente(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO_PRINCIPAL);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font(FUENTE, Font.BOLD, 26));
        labelTitulo.setForeground(TEXTO_PRIMARIO);
        encabezado.add(labelTitulo, BorderLayout.WEST);

        JPanel contenedorVolver = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        contenedorVolver.setOpaque(false);
        contenedorVolver.add(crearBotonSecundario("←  Volver al menú", () -> mostrarPanel("Menu")));
        encabezado.add(contenedorVolver, BorderLayout.EAST);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(FONDO_TARJETA);
        tarjeta.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        JLabel mensaje = new JLabel("Esta pantalla está en construcción");
        mensaje.setFont(new Font(FUENTE, Font.PLAIN, 15));
        mensaje.setForeground(TEXTO_SECUNDARIO);
        tarjeta.add(mensaje);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(tarjeta, BorderLayout.CENTER);
        return panel;
    }


    private static Border bordeBotonSecundario(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        );
    }

    private JButton crearBotonSecundario(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(FUENTE, Font.BOLD, 13));
        boton.setForeground(TEXTO_SECUNDARIO);
        boton.setBackground(FONDO_TARJETA);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorder(bordeBotonSecundario(COLOR_BORDE));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setForeground(COLOR_BOTON_HOVER);
                boton.setBorder(bordeBotonSecundario(COLOR_BOTON));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setForeground(TEXTO_SECUNDARIO);
                boton.setBorder(bordeBotonSecundario(COLOR_BORDE));
            }
        });

        boton.addActionListener(e -> accion.run());
        return boton;
    }

    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(panelContenedor, nombrePanel);
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }


    private static class TarjetaModulo extends JPanel {

        private static final int PADDING = 20;
        private static final int TAM_ICONO = 40;

        private final String titulo;
        private final String descripcion;
        private final Icono icono;
        private boolean hover = false;

        TarjetaModulo(String titulo, String descripcion, Icono icono, Runnable accion) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icono = icono;

            setOpaque(true);
            setBackground(FONDO_TARJETA);
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

            g2.setColor(FONDO_TARJETA);
            g2.fillRect(0, 0, w, h);
            g2.setColor(hover ? COLOR_BOTON : COLOR_BORDE);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(0, 0, w - 1, h - 1);
            if (hover) {
                g2.fillRect(0, 0, w, 3);
            }

            Font fuenteTitulo = new Font(FUENTE, Font.BOLD, 14);
            Font fuenteDescripcion = new Font(FUENTE, Font.PLAIN, 12);
            FontMetrics fmTitulo = g2.getFontMetrics(fuenteTitulo);
            FontMetrics fmDesc = g2.getFontMetrics(fuenteDescripcion);

            int separacion = 14;
            int bloque = TAM_ICONO + separacion + fmTitulo.getHeight() + 2 + fmDesc.getHeight();
            int y = (h - bloque) / 2;
            int anchoTexto = w - PADDING * 2;

            g2.setColor(hover ? COLOR_BOTON : COLOR_ACENTO_SUAVE);
            g2.fillRect(PADDING, y, TAM_ICONO, TAM_ICONO);
            dibujarIcono(g2, icono, PADDING + TAM_ICONO / 2.0, y + TAM_ICONO / 2.0,
                    hover ? Color.WHITE : COLOR_BOTON);

            int yTitulo = y + TAM_ICONO + separacion;
            g2.setFont(fuenteTitulo);
            g2.setColor(TEXTO_PRIMARIO);
            g2.drawString(ajustarTexto(titulo, fmTitulo, anchoTexto), PADDING, yTitulo + fmTitulo.getAscent());

            int yDesc = yTitulo + fmTitulo.getHeight() + 2;
            g2.setFont(fuenteDescripcion);
            g2.setColor(TEXTO_SECUNDARIO);
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

        private static void dibujarIcono(Graphics2D g2, Icono icono, double cx, double cy, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (icono) {
                case USUARIO:
                    g2.draw(new Ellipse2D.Double(cx - 4, cy - 9, 8, 8));
                    g2.draw(new Arc2D.Double(cx - 9, cy + 1, 18, 16, 0, 180, Arc2D.OPEN));
                    break;

                case PRESUPUESTO:
                    g2.draw(new Ellipse2D.Double(cx - 9, cy - 9, 18, 18));
                    g2.draw(new Line2D.Double(cx, cy, cx, cy - 9));
                    g2.draw(new Line2D.Double(cx, cy, cx + 7.8, cy + 4.5));
                    break;

                case CATEGORIA:
                    g2.draw(new Rectangle2D.Double(cx - 9, cy - 9, 8, 8));
                    g2.draw(new Rectangle2D.Double(cx + 1, cy - 9, 8, 8));
                    g2.draw(new Rectangle2D.Double(cx - 9, cy + 1, 8, 8));
                    g2.draw(new Rectangle2D.Double(cx + 1, cy + 1, 8, 8));
                    break;

                case SUBCATEGORIA:
                    g2.draw(new Rectangle2D.Double(cx - 4, cy - 9, 8, 6));
                    g2.draw(new Line2D.Double(cx, cy - 3, cx, cy + 1));
                    g2.draw(new Line2D.Double(cx - 6, cy + 1, cx + 6, cy + 1));
                    g2.draw(new Line2D.Double(cx - 6, cy + 1, cx - 6, cy + 4));
                    g2.draw(new Line2D.Double(cx + 6, cy + 1, cx + 6, cy + 4));
                    g2.draw(new Rectangle2D.Double(cx - 10, cy + 4, 8, 6));
                    g2.draw(new Rectangle2D.Double(cx + 2, cy + 4, 8, 6));
                    break;

                case DETALLE:
                    g2.draw(new Rectangle2D.Double(cx - 7, cy - 9, 14, 18));
                    g2.draw(new Line2D.Double(cx - 3, cy - 4, cx + 3, cy - 4));
                    g2.draw(new Line2D.Double(cx - 3, cy, cx + 3, cy));
                    g2.draw(new Line2D.Double(cx - 3, cy + 4, cx + 1, cy + 4));
                    break;

                case OBLIGACION:
                    g2.draw(new Rectangle2D.Double(cx - 9, cy - 7, 18, 16));
                    g2.draw(new Line2D.Double(cx - 9, cy - 2, cx + 9, cy - 2));
                    g2.draw(new Line2D.Double(cx - 4, cy - 10, cx - 4, cy - 5));
                    g2.draw(new Line2D.Double(cx + 4, cy - 10, cx + 4, cy - 5));
                    break;

                case TRANSACCION:
                    g2.draw(new Line2D.Double(cx - 9, cy - 4, cx + 9, cy - 4));
                    g2.draw(flecha(cx + 5, cy - 8, cx + 9, cy - 4, cx + 5, cy));
                    g2.draw(new Line2D.Double(cx + 9, cy + 4, cx - 9, cy + 4));
                    g2.draw(flecha(cx - 5, cy, cx - 9, cy + 4, cx - 5, cy + 8));
                    break;

                case REPORTERIA:
                    g2.draw(new Line2D.Double(cx - 9, cy + 9, cx + 9, cy + 9));
                    g2.draw(new Line2D.Double(cx - 9, cy - 9, cx - 9, cy + 9));
                    g2.draw(new Rectangle2D.Double(cx - 5, cy + 2, 3, 7));
                    g2.draw(new Rectangle2D.Double(cx - 1, cy - 3, 3, 12));
                    g2.draw(new Rectangle2D.Double(cx + 3, cy - 7, 3, 16));
                    break;
            }
        }

        private static Path2D flecha(double x1, double y1, double x2, double y2, double x3, double y3) {
            Path2D.Double path = new Path2D.Double();
            path.moveTo(x1, y1);
            path.lineTo(x2, y2);
            path.lineTo(x3, y3);
            return path;
        }
    }
}