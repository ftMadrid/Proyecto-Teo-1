package dev.presupuesto.frontend.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;
 
public final class Estilo {
 
    private Estilo() {
    }
 
    public static final Color FONDO_PRINCIPAL = new Color(235, 238, 242);
    public static final Color FONDO_TARJETA = new Color(255, 255, 255);
    public static final Color TEXTO_PRIMARIO = new Color(40, 40, 40);
    public static final Color TEXTO_SECUNDARIO = new Color(120, 120, 120);
    public static final Color COLOR_INPUT = new Color(248, 250, 252);
    public static final Color COLOR_BORDE_INPUT = new Color(210, 215, 220);
    public static final Color COLOR_BORDE = new Color(220, 225, 230);
    public static final Color COLOR_BOTON = new Color(0, 110, 255);
    public static final Color COLOR_BOTON_HOVER = new Color(0, 90, 215);
    public static final Color COLOR_ACENTO_SUAVE = new Color(230, 240, 255);
    public static final Color COLOR_PELIGRO = new Color(214, 48, 49);
    public static final Color COLOR_PELIGRO_HOVER = new Color(184, 36, 37);
 
    public static final String FUENTE = "Segoe UI";
 
    public static Font fuente(int estilo, int tamano) {
        return new Font(FUENTE, estilo, tamano);
    }

    public static JLabel etiquetaCampo(String texto) {
        JLabel label = new JLabel(texto.toUpperCase());
        label.setFont(fuente(Font.BOLD, 11));
        label.setForeground(TEXTO_SECUNDARIO);
        return label;
    }
 
    public static JPanel crearEncabezado(String titulo, String subtitulo, String textoBoton, Runnable accionBoton) {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
 
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
 
        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(fuente(Font.BOLD, 26));
        labelTitulo.setForeground(TEXTO_PRIMARIO);
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel labelSubtitulo = new JLabel(subtitulo);
        labelSubtitulo.setFont(fuente(Font.PLAIN, 14));
        labelSubtitulo.setForeground(TEXTO_SECUNDARIO);
        labelSubtitulo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        labelSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        textos.add(labelTitulo);
        textos.add(labelSubtitulo);
        encabezado.add(textos, BorderLayout.WEST);
 
        JPanel contenedorBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        contenedorBoton.setOpaque(false);
        contenedorBoton.add(botonSecundario(textoBoton, accionBoton));
        encabezado.add(contenedorBoton, BorderLayout.EAST);
 
        return encabezado;
    }
    
    private static Border bordeCampo(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        );
    }
 
    public static JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(fuente(Font.PLAIN, 15));
        campo.setBackground(COLOR_INPUT);
        campo.setForeground(TEXTO_PRIMARIO);
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(bordeCampo(COLOR_BORDE_INPUT));
        campo.setPreferredSize(new Dimension(200, 40));
 
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(bordeCampo(COLOR_BOTON));
            }
            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(bordeCampo(COLOR_BORDE_INPUT));
            }
        });
        return campo;
    }
 
    public static JPanel crearGrupo(String etiqueta, JComponent campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 6));
        grupo.setOpaque(false);
        grupo.add(etiquetaCampo(etiqueta), BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);
        return grupo;
    }
 
    public static JButton botonPrimario(String texto, Runnable accion) {
        return crearBotonSolido(texto, COLOR_BOTON, COLOR_BOTON_HOVER, accion);
    }
 
    public static JButton botonPeligro(String texto, Runnable accion) {
        return crearBotonSolido(texto, COLOR_PELIGRO, COLOR_PELIGRO_HOVER, accion);
    }
 
    private static JButton crearBotonSolido(String texto, Color normal, Color hover, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(normal);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 22));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width, 40));
 
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(hover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(normal);
            }
        });
 
        boton.addActionListener(e -> accion.run());
        return boton;
    }
 
    private static Border bordeBotonSecundario(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        );
    }
 
    public static JButton botonSecundario(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, 13));
        boton.setForeground(TEXTO_SECUNDARIO);
        boton.setBackground(FONDO_TARJETA);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorder(bordeBotonSecundario(COLOR_BORDE));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width, 40));
 
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
 
    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(fuente(Font.PLAIN, 12));
        tabla.setForeground(TEXTO_PRIMARIO);
        tabla.setBackground(FONDO_TARJETA);
        tabla.setRowHeight(32);
        tabla.setGridColor(new Color(236, 239, 243));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(COLOR_ACENTO_SUAVE);
        tabla.setSelectionForeground(TEXTO_PRIMARIO);
        tabla.setFillsViewportHeight(true);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, columna);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setToolTipText(valor == null ? null : valor.toString()); 
                return this;
            }
        });

        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));
        DefaultTableCellRenderer rendererHeader = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
                setText(valor == null ? "" : valor.toString().toUpperCase());
                setBackground(COLOR_INPUT);
                setForeground(TEXTO_SECUNDARIO);
                setFont(fuente(Font.BOLD, 11));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)
                ));
                return this;
            }
        };
        rendererHeader.setOpaque(true);
        header.setDefaultRenderer(rendererHeader);
    }
 
    public static JScrollPane crearScroll(Component contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        scroll.getViewport().setBackground(FONDO_TARJETA);
        return scroll;
    }
 
    private static <T> T conTema(Supplier<T> dialogo) {
        String[] claves = {"Panel.background", "OptionPane.background", "OptionPane.messageForeground"};
        Object[] anteriores = new Object[claves.length];
        for (int i = 0; i < claves.length; i++) {
            anteriores[i] = UIManager.get(claves[i]);
        }
 
        UIManager.put("Panel.background", FONDO_TARJETA);
        UIManager.put("OptionPane.background", FONDO_TARJETA);
        UIManager.put("OptionPane.messageForeground", TEXTO_PRIMARIO);
 
        try {
            return dialogo.get();
        } finally {
            for (int i = 0; i < claves.length; i++) {
                UIManager.put(claves[i], anteriores[i]);
            }
        }
    }
 
    private static void mostrar(Component padre, String mensaje, String titulo, int tipo) {
        conTema(() -> {
            JOptionPane.showMessageDialog(padre, mensaje, titulo, tipo);
            return null;
        });
    }
 
    public static void mostrarInfo(Component padre, String mensaje) {
        mostrar(padre, mensaje, "Listo", JOptionPane.INFORMATION_MESSAGE);
    }
 
    public static void mostrarAviso(Component padre, String mensaje) {
        mostrar(padre, mensaje, "Revisa los datos", JOptionPane.WARNING_MESSAGE);
    }
 
    public static void mostrarError(Component padre, String mensaje) {
        mostrar(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
 
    public static boolean confirmar(Component padre, String mensaje, String titulo, String textoConfirmar) {
        Object[] opciones = {textoConfirmar, "Cancelar"};
        int respuesta = conTema(() -> JOptionPane.showOptionDialog(
                padre, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opciones, opciones[1]));
        return respuesta == 0;
    }
}
