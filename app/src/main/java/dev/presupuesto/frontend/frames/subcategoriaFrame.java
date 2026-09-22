package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudSubCategoria;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class subcategoriaFrame extends JPanel {

    private static final String TAB_CONSULTAR = "Consultar";
    private static final String TAB_LISTAR = "Listar";
    private static final String TAB_INSERTAR = "Insertar";
    private static final String TAB_ACTUALIZAR = "Actualizar";
    private static final String TAB_ELIMINAR = "Eliminar";

    private CrudSubCategoria crud = new CrudSubCategoria();
    private hubFrame ventanaPrincipal;

    private CardLayout cardTabs;
    private JPanel panelTabs;
    private final Map<String, JButton> botonesTab = new LinkedHashMap<>();
    private String tabActiva = TAB_CONSULTAR;

    private JTextField txtIdConsultar;
    private JTextArea areaConsultar;

    private JTextField txtIdCategoriaListar;
    private DefaultTableModel modeloListar;
    private JTable tablaListar;

    private JTextField txtIdCategoriaInsertar, txtNombreInsertar, txtDescripcionInsertar;
    private JTextField txtIdSubcategoriaActualizar, txtNombreActualizar, txtDescripcionActualizar, txtActivaActualizar;
    private JTextField txtIdEliminar;

    public subcategoriaFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Subcategorías",
                "Divide y especifica tus categorías",
                "←  Volver al menú",
                () -> {
                    limpiarTodo();
                    ventanaPrincipal.mostrarPanel("Menu");
                }), BorderLayout.NORTH);

        add(crearTarjetaPrincipal(), BorderLayout.CENTER);
    }

    private JPanel crearTarjetaPrincipal() {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createLineBorder(Tema.borde(), 1));

        cardTabs = new CardLayout();
        panelTabs = new JPanel(cardTabs);
        panelTabs.setOpaque(false);
        panelTabs.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        panelTabs.add(crearTabConsultar(), TAB_CONSULTAR);
        panelTabs.add(crearTabListar(), TAB_LISTAR);
        panelTabs.add(crearTabInsertar(), TAB_INSERTAR);
        panelTabs.add(crearTabActualizar(), TAB_ACTUALIZAR);
        panelTabs.add(crearTabEliminar(), TAB_ELIMINAR);

        tarjeta.add(crearBarraTabs(), BorderLayout.NORTH);
        tarjeta.add(panelTabs, BorderLayout.CENTER);

        seleccionarTab(TAB_CONSULTAR);
        return tarjeta;
    }

    private JPanel crearBarraTabs() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.borde()),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));

        String[] nombres = {TAB_CONSULTAR, TAB_LISTAR, TAB_INSERTAR, TAB_ACTUALIZAR, TAB_ELIMINAR};
        for (String nombre : nombres) {
            JButton boton = crearBotonTab(nombre);
            botonesTab.put(nombre, boton);
            barra.add(boton);
        }
        return barra;
    }

    private JButton crearBotonTab(String nombre) {
        JButton boton = new JButton(nombre);
        boton.setFont(Estilo.fuente(Font.BOLD, 13));
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        estilizarTab(boton, false);

        boton.addActionListener(e -> seleccionarTab(nombre));
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!nombre.equals(tabActiva)) {
                    boton.setForeground(Tema.textoPrimario());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!nombre.equals(tabActiva)) {
                    boton.setForeground(Tema.textoSecundario());
                }
            }
        });
        return boton;
    }

    private void estilizarTab(JButton boton, boolean activo) {
        boton.setForeground(activo ? Tema.boton() : Tema.textoSecundario());
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, activo ? Tema.boton() : Tema.fondoTarjeta()),
                BorderFactory.createEmptyBorder(12, 18, 9, 18)
        ));
    }

    private void seleccionarTab(String nombre) {
        tabActiva = nombre;
        for (Map.Entry<String, JButton> entrada : botonesTab.entrySet()) {
            estilizarTab(entrada.getValue(), entrada.getKey().equals(nombre));
        }
        cardTabs.show(panelTabs, nombre);
    }

    private JPanel crearTabConsultar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdConsultar = Estilo.crearCampo();
        txtIdConsultar.setPreferredSize(new Dimension(240, 40));
        txtIdConsultar.addActionListener(e -> consultar());

        panel.add(crearFilaBusqueda("ID Subcategoría", txtIdConsultar, Estilo.botonPrimario("Buscar", this::consultar)), BorderLayout.NORTH);

        areaConsultar = new JTextArea();
        areaConsultar.setEditable(false);
        areaConsultar.setOpaque(true);
        areaConsultar.setBackground(Tema.fondoConsola());
        areaConsultar.setFont(new Font("Monospaced", Font.PLAIN, 15));
        areaConsultar.setForeground(Tema.textoConsola());
        areaConsultar.setMargin(new Insets(15, 15, 15, 15));

        panel.add(Estilo.crearScroll(areaConsultar), BorderLayout.CENTER);
        return panel;
    }

    private void consultar() {
        String id = txtIdConsultar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la subcategoría que quieres buscar.");
            return;
        }

        String resultado = crud.consultarSubcategoria(id);
        if (resultado != null && !resultado.isEmpty()) {
            areaConsultar.setText("--------------------------\nBúsqueda Completada\n--------------------------\n\n" + resultado);
        } else {
            areaConsultar.setText("[!] No se encontró la subcategoría con ID: " + id);
        }
    }

    private JPanel crearTabListar() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        
        txtIdCategoriaListar = Estilo.crearCampo();
        txtIdCategoriaListar.setPreferredSize(new Dimension(150, 40));
        txtIdCategoriaListar.addActionListener(e -> cargarLista());

        barra.add(Estilo.crearGrupo("ID Categoría", txtIdCategoriaListar));
        barra.add(Box.createHorizontalStrut(10));
        barra.add(Estilo.crearGrupo(" ", Estilo.botonPrimario("Cargar", this::cargarLista)));

        JLabel pista = new JLabel("Doble clic en una fila para editar");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Tema.textoSecundario());
        pista.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        barra.add(Estilo.crearGrupo(" ", pista));

        String[] columnas = {"ID Subcategoría", "ID Categoría", "Nombre", "Descripción", "Activa"};
        modeloListar = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaListar = new JTable(modeloListar);
        Estilo.estilizarTabla(tablaListar);

        int[] anchos = {100, 100, 150, 200, 60};
        for (int i = 0; i < anchos.length; i++) {
            tablaListar.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        tablaListar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaListar.rowAtPoint(e.getPoint());
                    if (fila >= 0) {
                        editarDesdeTabla(fila);
                    }
                }
            }
        });

        panel.add(barra, BorderLayout.NORTH);
        panel.add(Estilo.crearScroll(tablaListar), BorderLayout.CENTER);
        return panel;
    }

    private void cargarLista() {
        String idCat = txtIdCategoriaListar.getText().trim();
        if (idCat.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la Categoría para ver sus subcategorías.");
            return;
        }

        modeloListar.setRowCount(0);
        ArrayList<String> lista = crud.listarSubcategoriasPorCategoria(idCat);

        if (lista != null && !lista.isEmpty()) {
            for (String fila : lista) {
                String[] datos = fila.split(",", -1);
                if (datos.length >= 5) {
                    modeloListar.addRow(new Object[]{
                            datos[0], datos[1], datos[2], datos[3], datos[4]
                    });
                } else {
                    modeloListar.addRow(new Object[]{fila, "", "", "", ""});
                }
            }
        } else {
            modeloListar.addRow(new Object[]{"Sin datos", "No hay subcategorías.", "", "", ""});
        }
    }

    private void editarDesdeTabla(int fila) {
        if (valorTabla(fila, 2).isEmpty() || valorTabla(fila, 0).equals("Sin datos")) {
            return;
        }

        txtIdSubcategoriaActualizar.setText(valorTabla(fila, 0));
        txtNombreActualizar.setText(valorTabla(fila, 2));
        txtDescripcionActualizar.setText(valorTabla(fila, 3));
        txtActivaActualizar.setText(valorTabla(fila, 4));

        seleccionarTab(TAB_ACTUALIZAR);
        txtNombreActualizar.requestFocusInWindow();
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloListar.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private JPanel crearTabInsertar() {
        txtIdCategoriaInsertar = Estilo.crearCampo();
        txtNombreInsertar = Estilo.crearCampo();
        txtDescripcionInsertar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(2, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Categoría", txtIdCategoriaInsertar));
        form.add(Estilo.crearGrupo("Nombre", txtNombreInsertar));
        form.add(Estilo.crearGrupo("Descripción", txtDescripcionInsertar));
        form.add(new JLabel()); // Relleno para que la cuadrícula no se descuadre

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar", this::insertar),
                Estilo.botonSecundario("Limpiar", this::limpiarInsertar));

        return crearFormulario(form, botones);
    }

    private void insertar() {
        String idCat = txtIdCategoriaInsertar.getText().trim();
        String nombre = txtNombreInsertar.getText().trim();
        String desc = txtDescripcionInsertar.getText().trim();

        if (idCat.isEmpty() || nombre.isEmpty()) {
            Estilo.mostrarAviso(this, "Completa los campos obligatorios (ID Categoría, Nombre).");
            return;
        }

        boolean ok = crud.insertarSubcategoria(idCat, nombre, desc, usuarioActual());
        if (ok) {
            Estilo.mostrarInfo(this, "Subcategoría registrada correctamente.");
            limpiarInsertar();
        } else {
            Estilo.mostrarError(this, "No se pudo registrar la subcategoría.");
        }
    }

    private void limpiarInsertar() {
        txtIdCategoriaInsertar.setText("");
        txtNombreInsertar.setText("");
        txtDescripcionInsertar.setText("");
    }

    private JPanel crearTabActualizar() {
        txtIdSubcategoriaActualizar = Estilo.crearCampo();
        txtIdSubcategoriaActualizar.setPreferredSize(new Dimension(240, 40));
        txtIdSubcategoriaActualizar.addActionListener(e -> cargarParaActualizar());
        
        txtNombreActualizar = Estilo.crearCampo();
        txtDescripcionActualizar = Estilo.crearCampo();
        txtActivaActualizar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(2, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("Nombre", txtNombreActualizar));
        form.add(Estilo.crearGrupo("Descripción", txtDescripcionActualizar));
        form.add(Estilo.crearGrupo("Activa (Sí / No)", txtActivaActualizar));

        JPanel superior = new JPanel(new BorderLayout(0, 18));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda("ID Subcategoría", txtIdSubcategoriaActualizar, Estilo.botonSecundario("Cargar datos", this::cargarParaActualizar)),
                BorderLayout.NORTH);
        superior.add(form, BorderLayout.CENTER);

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar cambios", this::actualizar),
                Estilo.botonSecundario("Limpiar", this::limpiarActualizar));

        return crearFormulario(superior, botones);
    }

    private void cargarParaActualizar() {
        String id = txtIdSubcategoriaActualizar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la subcategoría a actualizar.");
            return;
        }

        String resultado = crud.consultarSubcategoria(id);
        if (resultado == null || resultado.isEmpty()) {
            Estilo.mostrarAviso(this, "No se encontró la subcategoría con ID: " + id);
            return;
        }

        try {
            String[] lineas = resultado.split("\n");
            
            String nombre = lineas[1].substring("Nombre: ".length());
            String desc = lineas[3].substring("Desc: ".length());
            String activa = lineas[4].substring("Activa: ".length());

            txtNombreActualizar.setText(nombre);
            txtDescripcionActualizar.setText(desc);
            txtActivaActualizar.setText(activa);

        } catch (Exception e) {
            Estilo.mostrarError(this, "Error al procesar los datos de la subcategoría.");
        }
        
        txtNombreActualizar.requestFocusInWindow();
    }

    private void actualizar() {
        String idSub = txtIdSubcategoriaActualizar.getText().trim();
        String nombre = txtNombreActualizar.getText().trim();
        String desc = txtDescripcionActualizar.getText().trim();
        String activaStr = txtActivaActualizar.getText().trim().toLowerCase();

        if (idSub.isEmpty() || nombre.isEmpty() || activaStr.isEmpty()) {
            Estilo.mostrarAviso(this, "Completa los campos obligatorios (ID Subcategoría, Nombre, Activa).");
            return;
        }
        
        boolean activa = activaStr.equals("si") || activaStr.equals("sí") || activaStr.equals("true") || activaStr.equals("1");

        boolean ok = crud.actualizarSubcategoria(idSub, nombre, desc, activa, usuarioActual());
        if (ok) {
            Estilo.mostrarInfo(this, "Subcategoría actualizada correctamente.");
            limpiarActualizar();
            if (!txtIdCategoriaListar.getText().isEmpty()) {
                cargarLista();
            }
        } else {
            Estilo.mostrarError(this, "No se pudo actualizar la subcategoría.");
        }
    }

    private void limpiarActualizar() {
        txtIdSubcategoriaActualizar.setText("");
        txtNombreActualizar.setText("");
        txtDescripcionActualizar.setText("");
        txtActivaActualizar.setText("");
    }

    private JPanel crearTabEliminar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdEliminar = Estilo.crearCampo();
        txtIdEliminar.setPreferredSize(new Dimension(240, 40));
        txtIdEliminar.addActionListener(e -> eliminar());

        panel.add(crearFilaBusqueda("ID Subcategoría", txtIdEliminar, Estilo.botonPeligro("Eliminar", this::eliminar)),
                BorderLayout.NORTH);

        JLabel aviso = new JLabel("Escribe el ID de la subcategoría que quieres eliminar.");
        aviso.setFont(Estilo.fuente(Font.PLAIN, 13));
        aviso.setForeground(Tema.textoSecundario());
        aviso.setVerticalAlignment(SwingConstants.TOP);
        panel.add(aviso, BorderLayout.CENTER);

        return panel;
    }

    private void eliminar() {
        String id = txtIdEliminar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la subcategoría.");
            return;
        }

        boolean confirmado = Estilo.confirmar(this,
                "¿Seguro que quieres eliminar la subcategoría con ID: " + id + "?",
                "Confirmar",
                "Sí, eliminar");
        if (!confirmado) {
            return;
        }

        boolean ok = crud.eliminarSubcategoria(id);
        if (ok) {
            Estilo.mostrarInfo(this, "Subcategoría eliminada correctamente.");
            txtIdEliminar.setText("");
            if (!txtIdCategoriaListar.getText().isEmpty()) {
                cargarLista();
            }
        } else {
            Estilo.mostrarError(this, "No se pudo eliminar la subcategoría.");
        }
    }

    private String usuarioActual() {
        return ventanaPrincipal.getNombreCuenta();
    }

    private void limpiarTodo() {
        limpiarInsertar();
        limpiarActualizar();
        txtIdConsultar.setText("");
        txtIdEliminar.setText("");
        areaConsultar.setText("");
        modeloListar.setRowCount(0);
        seleccionarTab(TAB_CONSULTAR);
    }

    private JPanel crearFilaBusqueda(String etiqueta, JTextField campo, Component boton) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.add(campo, BorderLayout.CENTER);
        fila.add(boton, BorderLayout.EAST);

        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenedor.setOpaque(false);
        contenedor.add(Estilo.crearGrupo(etiqueta, fila));
        return contenedor;
    }

    private JPanel crearBotonera(JButton... botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        for (int i = 0; i < botones.length; i++) {
            if (i > 0) {
                panel.add(Box.createHorizontalStrut(12));
            }
            panel.add(botones[i]);
        }
        return panel;
    }

    private JPanel crearFormulario(JComponent form, JComponent botones) {
        JPanel contenido = new JPanel(new BorderLayout(0, 22));
        contenido.setOpaque(false);
        contenido.add(form, BorderLayout.NORTH);
        contenido.add(botones, BorderLayout.CENTER);
        return contenido;
    }
}