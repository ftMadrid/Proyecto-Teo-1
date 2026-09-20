package dev.presupuesto.frontend.frames;

import javax.swing.*;
import javax.swing.border.Border;

import dev.presupuesto.frontend.utils.Tema;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class hubFrame extends JFrame {

    private static final String FUENTE = "Segoe UI";

    private enum Icono {
        USUARIO, PRESUPUESTO, CATEGORIA, SUBCATEGORIA, DETALLE, OBLIGACION, TRANSACCION, REPORTERIA, AJUSTES
    }

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private String nombreCuenta;

    
    private CardLayout cardPerfil;
    private JPanel contenidoPerfil;
    private JPanel barraLateralPerfil;
    private String seccionPerfilActual = "MiPerfil";

    public hubFrame(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
        setTitle("Presupuesto Personal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        construirPaneles();

        setContentPane(panelContenedor);
        setVisible(true);
    }

    
    private void construirPaneles() {
        panelContenedor.removeAll();
        panelContenedor.setBackground(Tema.fondoPrincipal());

        panelContenedor.add(crearPanelMenu(), "Menu");
        panelContenedor.add(new usuarioFrame(this), "Usuario");
        panelContenedor.add(new presupuestoFrame(this), "Presupuesto");
        panelContenedor.add(new categoriaFrame(this), "Categoria");
        panelContenedor.add(new subcategoriaFrame(this), "Subcategoria");
        panelContenedor.add(crearPanelPendiente("Detalle de presupuesto"), "PresupuestoDetalle");
        panelContenedor.add(crearPanelPendiente("Obligaciones fijas"), "ObligacionFija");
        panelContenedor.add(crearPanelPendiente("Transacciones"), "Transaccion");
        panelContenedor.add(crearPanelPendiente("Reportería"), "Reporteria");
        panelContenedor.add(crearPanelPerfil(), "Perfil");
    }

    private void cambiarTema(boolean oscuro) {
        Tema.setOscuro(oscuro);
        construirPaneles();
        mostrarPanel("Perfil"); 
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }


    
    
    

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.fondoPrincipal());
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        panel.add(crearEncabezado(), BorderLayout.NORTH);
        panel.add(crearSeccionModulos(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        encabezado.add(crearBloquePerfil(), BorderLayout.WEST);

        return encabezado;
    }

    
    private JPanel crearBloquePerfil() {
        AvatarPerfil avatar = new AvatarPerfil(56);

        JLabel labelBienvenida = new JLabel("¡Bienvenido, " + nombreCuenta + "!");
        labelBienvenida.setFont(new Font(FUENTE, Font.BOLD, 26));
        labelBienvenida.setForeground(Tema.textoPrimario());
        labelBienvenida.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelSubtitulo = new JLabel("Selecciona un módulo para continuar");
        labelSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        labelSubtitulo.setForeground(Tema.textoSecundario());
        labelSubtitulo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        labelSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Box.createVerticalGlue());
        textos.add(labelBienvenida);
        textos.add(labelSubtitulo);
        textos.add(Box.createVerticalGlue());

        JPanel bloque = new JPanel(new BorderLayout(16, 0));
        bloque.setOpaque(false);
        bloque.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bloque.setToolTipText("Ver mi perfil");
        bloque.add(avatar, BorderLayout.WEST);
        bloque.add(textos, BorderLayout.CENTER);

        bloque.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                avatar.setHover(true);
                labelBienvenida.setForeground(Tema.boton());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                avatar.setHover(false);
                labelBienvenida.setForeground(Tema.textoPrimario());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && bloque.contains(e.getPoint())) {
                    abrirPerfil("MiPerfil");
                }
            }
        });

        return bloque;
    }

    private JPanel crearSeccionModulos() {
        JPanel seccion = new JPanel(new BorderLayout());
        seccion.setOpaque(false);

        JLabel labelModulos = new JLabel("MÓDULOS");
        labelModulos.setFont(new Font(FUENTE, Font.BOLD, 11));
        labelModulos.setForeground(Tema.textoSecundario());
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


    
    
    

    private JPanel crearPanelPerfil() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.fondoPrincipal());

        barraLateralPerfil = crearBarraLateralPerfil();
        panel.add(barraLateralPerfil, BorderLayout.WEST);
        panel.add(crearContenidoPerfil(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearBarraLateralPerfil() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.fondoTarjeta());
        barra.setPreferredSize(new Dimension(210, 0));
        barra.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.borde()));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setBorder(BorderFactory.createEmptyBorder(20, 16, 0, 16));

        JButton botonVolver = crearBotonSecundario("←  Volver al menú", () -> mostrarPanel("Menu"));
        botonVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonVolver.setMaximumSize(new Dimension(Integer.MAX_VALUE, botonVolver.getPreferredSize().height));

        JLabel labelCuenta = new JLabel("MI CUENTA");
        labelCuenta.setFont(new Font(FUENTE, Font.BOLD, 11));
        labelCuenta.setForeground(Tema.textoSecundario());
        labelCuenta.setAlignmentX(Component.LEFT_ALIGNMENT);

        superior.add(botonVolver);
        superior.add(Box.createVerticalStrut(24));
        superior.add(labelCuenta);
        superior.add(Box.createVerticalStrut(10));
        superior.add(new ItemNavegacion("Mi perfil", Icono.USUARIO, "MiPerfil"));
        superior.add(Box.createVerticalStrut(4));
        superior.add(new ItemNavegacion("Ajustes", Icono.AJUSTES, "Ajustes"));

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setOpaque(false);
        inferior.setBorder(BorderFactory.createEmptyBorder(0, 16, 20, 16));
        inferior.add(crearBotonSecundario("Cerrar sesión", () -> {
            dispose();
            new loginFrame();
        }), BorderLayout.CENTER);

        barra.add(superior, BorderLayout.NORTH);
        barra.add(inferior, BorderLayout.SOUTH);
        return barra;
    }

    private JPanel crearContenidoPerfil() {
        cardPerfil = new CardLayout();
        contenidoPerfil = new JPanel(cardPerfil);
        contenidoPerfil.setOpaque(false);

        contenidoPerfil.add(crearSeccionMiPerfil(), "MiPerfil");
        contenidoPerfil.add(crearSeccionAjustes(), "Ajustes");
        cardPerfil.show(contenidoPerfil, seccionPerfilActual);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));
        contenedor.add(contenidoPerfil, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearSeccionMiPerfil() {
        JPanel seccion = new JPanel(new BorderLayout());
        seccion.setOpaque(false);
        seccion.add(crearCabeceraSeccion("Mi perfil", "Información de tu cuenta"), BorderLayout.NORTH);

        JPanel tarjeta = crearTarjetaBase(new BorderLayout(20, 0));
        tarjeta.add(new AvatarPerfil(88), BorderLayout.WEST);

        JLabel etiqueta = new JLabel("NOMBRE DE CUENTA");
        etiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        etiqueta.setForeground(Tema.textoSecundario());
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nombre = new JLabel(nombreCuenta);
        nombre.setFont(new Font(FUENTE, Font.BOLD, 22));
        nombre.setForeground(Tema.textoPrimario());
        nombre.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        datos.add(Box.createVerticalGlue());
        datos.add(etiqueta);
        datos.add(nombre);
        datos.add(Box.createVerticalGlue());
        tarjeta.add(datos, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(tarjeta, BorderLayout.NORTH);
        seccion.add(contenido, BorderLayout.CENTER);

        return seccion;
    }

    private JPanel crearSeccionAjustes() {
        JPanel seccion = new JPanel(new BorderLayout());
        seccion.setOpaque(false);
        seccion.add(crearCabeceraSeccion("Ajustes", "Personaliza la aplicación"), BorderLayout.NORTH);

        JLabel etiqueta = new JLabel("APARIENCIA");
        etiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        etiqueta.setForeground(Tema.textoSecundario());
        etiqueta.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel tarjeta = crearTarjetaBase(new BorderLayout(16, 0));

        JLabel labelTitulo = new JLabel("Modo oscuro");
        labelTitulo.setFont(new Font(FUENTE, Font.BOLD, 14));
        labelTitulo.setForeground(Tema.textoPrimario());
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelDescripcion = new JLabel("Cambia el fondo blanco por un tema oscuro");
        labelDescripcion.setFont(new Font(FUENTE, Font.PLAIN, 12));
        labelDescripcion.setForeground(Tema.textoSecundario());
        labelDescripcion.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        labelDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(labelTitulo);
        textos.add(labelDescripcion);

        JPanel contenedorInterruptor = new JPanel(new GridBagLayout());
        contenedorInterruptor.setOpaque(false);
        contenedorInterruptor.add(new InterruptorTema(Tema.esOscuro(), this::cambiarTema));

        tarjeta.add(textos, BorderLayout.CENTER);
        tarjeta.add(contenedorInterruptor, BorderLayout.EAST);

        JPanel bloque = new JPanel(new BorderLayout());
        bloque.setOpaque(false);
        bloque.add(etiqueta, BorderLayout.NORTH);
        bloque.add(tarjeta, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(bloque, BorderLayout.NORTH);
        seccion.add(contenido, BorderLayout.CENTER);

        return seccion;
    }

    private JPanel crearCabeceraSeccion(String titulo, String subtitulo) {
        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font(FUENTE, Font.BOLD, 26));
        labelTitulo.setForeground(Tema.textoPrimario());
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelSubtitulo = new JLabel(subtitulo);
        labelSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        labelSubtitulo.setForeground(Tema.textoSecundario());
        labelSubtitulo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        labelSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        cabecera.add(labelTitulo);
        cabecera.add(labelSubtitulo);
        return cabecera;
    }

    private JPanel crearTarjetaBase(LayoutManager layout) {
        JPanel tarjeta = new JPanel(layout);
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.borde(), 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        return tarjeta;
    }

    private void abrirPerfil(String seccion) {
        mostrarSeccionPerfil(seccion);
        mostrarPanel("Perfil");
    }

    private void mostrarSeccionPerfil(String seccion) {
        seccionPerfilActual = seccion;
        cardPerfil.show(contenidoPerfil, seccion);
        barraLateralPerfil.repaint();
    }


    
    
    

    private JPanel crearPanelPendiente(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.fondoPrincipal());
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font(FUENTE, Font.BOLD, 26));
        labelTitulo.setForeground(Tema.textoPrimario());
        encabezado.add(labelTitulo, BorderLayout.WEST);

        JPanel contenedorVolver = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        contenedorVolver.setOpaque(false);
        contenedorVolver.add(crearBotonSecundario("←  Volver al menú", () -> mostrarPanel("Menu")));
        encabezado.add(contenedorVolver, BorderLayout.EAST);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createLineBorder(Tema.borde(), 1));

        JLabel mensaje = new JLabel("Esta pantalla está en construcción");
        mensaje.setFont(new Font(FUENTE, Font.PLAIN, 15));
        mensaje.setForeground(Tema.textoSecundario());
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
        boton.setForeground(Tema.textoSecundario());
        boton.setBackground(Tema.fondoTarjeta());
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorder(bordeBotonSecundario(Tema.borde()));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setForeground(Tema.botonHover());
                boton.setBorder(bordeBotonSecundario(Tema.boton()));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setForeground(Tema.textoSecundario());
                boton.setBorder(bordeBotonSecundario(Tema.borde()));
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


    
    
    

    
    private static class AvatarPerfil extends JComponent {

        private boolean hover = false;

        AvatarPerfil(int tam) {
            setPreferredSize(new Dimension(tam, tam));
        }

        void setHover(boolean hover) {
            this.hover = hover;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int d = Math.min(getWidth(), getHeight());
            double x = (getWidth() - d) / 2.0;
            double y = (getHeight() - d) / 2.0;
            double cx = x + d / 2.0;
            double cy = y + d / 2.0;
            double s = d / 30.0;

            g2.setColor(hover ? Tema.botonHover() : Tema.boton());
            g2.fill(new Ellipse2D.Double(x, y, d, d));

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke((float) (1.8 * s), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Double(cx - 4 * s, cy - 9 * s, 8 * s, 8 * s));
            g2.draw(new Arc2D.Double(cx - 9 * s, cy + 1 * s, 18 * s, 16 * s, 0, 180, Arc2D.OPEN));

            g2.dispose();
        }
    }

    
    private class ItemNavegacion extends JPanel {

        private final String texto;
        private final Icono icono;
        private final String seccion;
        private boolean hover = false;

        ItemNavegacion(String texto, Icono icono, String seccion) {
            this.texto = texto;
            this.icono = icono;
            this.seccion = seccion;

            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setPreferredSize(new Dimension(0, 44));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
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
                        mostrarSeccionPerfil(seccion);
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
            boolean activo = seccion.equals(seccionPerfilActual);

            if (activo) {
                g2.setColor(Tema.acentoSuave());
                g2.fillRect(0, 0, w, h);
                g2.setColor(Tema.boton());
                g2.fillRect(0, 0, 3, h);
            } else if (hover) {
                g2.setColor(Tema.fondoPrincipal());
                g2.fillRect(0, 0, w, h);
            }

            Color color = activo ? Tema.boton() : (hover ? Tema.textoPrimario() : Tema.textoSecundario());

            TarjetaModulo.dibujarIcono(g2, icono, 28, h / 2.0, color);

            Font fuente = new Font(FUENTE, activo ? Font.BOLD : Font.PLAIN, 13);
            FontMetrics fm = g2.getFontMetrics(fuente);
            g2.setFont(fuente);
            g2.setColor(color);
            g2.drawString(texto, 52, (h - fm.getHeight()) / 2 + fm.getAscent());

            g2.dispose();
        }
    }

    
    private static class InterruptorTema extends JComponent {

        private boolean activo;

        InterruptorTema(boolean activo, Consumer<Boolean> alCambiar) {
            this.activo = activo;
            setPreferredSize(new Dimension(48, 26));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && contains(e.getPoint())) {
                        InterruptorTema.this.activo = !InterruptorTema.this.activo;
                        repaint();
                        alCambiar.accept(InterruptorTema.this.activo);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(activo ? Tema.boton() : new Color(170, 176, 186));
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));

            int margen = 3;
            int d = h - margen * 2;
            int x = activo ? w - d - margen : margen;
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(x, margen, d, d));

            g2.dispose();
        }
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

            Font fuenteTitulo = new Font(FUENTE, Font.BOLD, 14);
            Font fuenteDescripcion = new Font(FUENTE, Font.PLAIN, 12);
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

                case AJUSTES:
                    
                    g2.draw(new Line2D.Double(cx - 9, cy - 6, cx + 9, cy - 6));
                    g2.draw(new Line2D.Double(cx - 9, cy, cx + 9, cy));
                    g2.draw(new Line2D.Double(cx - 9, cy + 6, cx + 9, cy + 6));
                    g2.fill(new Ellipse2D.Double(cx - 5.5, cy - 8.5, 5, 5));
                    g2.fill(new Ellipse2D.Double(cx + 2, cy - 2.5, 5, 5));
                    g2.fill(new Ellipse2D.Double(cx - 6.5, cy + 3.5, 5, 5));
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