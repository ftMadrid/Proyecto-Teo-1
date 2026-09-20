package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudPresupuesto;
import dev.presupuesto.frontend.utils.Estilo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class presupuestoFrame extends JPanel {

    private static final String TAB_CONSULTAR = "Consultar";
    private static final String TAB_LISTAR = "Listar";
    private static final String TAB_INSERTAR = "Insertar";
    private static final String TAB_ACTUALIZAR = "Actualizar";
    private static final String TAB_ELIMINAR = "Eliminar";

    private CrudPresupuesto crud = new CrudPresupuesto();
    private hubFrame ventanaPrincipal;

    private CardLayout cardTabs;
    private JPanel panelTabs;
    private final Map<String, JButton> botonesTab = new LinkedHashMap<>();
    private String tabActiva = TAB_CONSULTAR;

    private JTextField txtIdConsultar;
    private JTextArea areaConsultar;

    private JTextField txtIdUsuarioListar;
    private DefaultTableModel modeloListar;
    private JTable tablaListar;

    private JTextField txtIdPresupuestoInsertar, txtIdUsuarioInsertar, txtNombreInsertar;
    private JTextField txtAnioInicioInsertar, txtMesInicioInsertar, txtAnioFinInsertar, txtMesFinInsertar;

    private JTextField txtIdPresupuestoActualizar, txtNombreActualizar;
    private JTextField txtAnioInicioActualizar, txtMesInicioActualizar, txtAnioFinActualizar, txtMesFinActualizar, txtEstadoActualizar;

    private JTextField txtIdEliminar;

    public presupuestoFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Estilo.FONDO_PRINCIPAL);
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Presupuestos",
                "Planifica y organiza tus presupuestos",
                "←  Volver al menú",
                () -> {
                    limpiarTodo();
                    ventanaPrincipal.mostrarPanel("Menu");
                }), BorderLayout.NORTH);

        add(crearTarjetaPrincipal(), BorderLayout.CENTER);
    }

    private JPanel crearTarjetaPrincipal() {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Estilo.FONDO_TARJETA);
        tarjeta.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_BORDE, 1));

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
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.COLOR_BORDE),
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
                    boton.setForeground(Estilo.TEXTO_PRIMARIO);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!nombre.equals(tabActiva)) {
                    boton.setForeground(Estilo.TEXTO_SECUNDARIO);
                }
            }
        });
        return boton;
    }

    private void estilizarTab(JButton boton, boolean activo) {
        boton.setForeground(activo ? Estilo.COLOR_BOTON : Estilo.TEXTO_SECUNDARIO);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, activo ? Estilo.COLOR_BOTON : Estilo.FONDO_TARJETA),
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

        panel.add(crearFilaBusqueda("ID Presupuesto", txtIdConsultar, Estilo.botonPrimario("Buscar", this::consultar)), BorderLayout.NORTH);

        areaConsultar = new JTextArea();
        areaConsultar.setEditable(false);
        areaConsultar.setFont(new Font("Monospaced", Font.PLAIN, 15));
        areaConsultar.setForeground(Estilo.TEXTO_PRIMARIO);
        areaConsultar.setMargin(new Insets(15, 15, 15, 15));

        panel.add(Estilo.crearScroll(areaConsultar), BorderLayout.CENTER);
        return panel;
    }

    private void consultar() {
        String id = txtIdConsultar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del presupuesto que quieres buscar.");
            return;
        }

        String resultado = crud.consultarPresupuesto(id);
        if (resultado != null && !resultado.isEmpty()) {
            areaConsultar.setText("--------------------------\nBúsqueda Completada\n--------------------------\n\n" + resultado);
        } else {
            areaConsultar.setText("[!] No se encontró el presupuesto con ID: " + id);
        }
    }

    private JPanel crearTabListar() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        
        txtIdUsuarioListar = Estilo.crearCampo();
        txtIdUsuarioListar.setPreferredSize(new Dimension(150, 40));
        txtIdUsuarioListar.addActionListener(e -> cargarLista());

        barra.add(Estilo.crearGrupo("ID Usuario", txtIdUsuarioListar));
        barra.add(Box.createHorizontalStrut(10));
        barra.add(Estilo.crearGrupo(" ", Estilo.botonPrimario("Cargar presupuestos", this::cargarLista)));

        JLabel pista = new JLabel("Doble clic en una fila para editar");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Estilo.TEXTO_SECUNDARIO);
        pista.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        barra.add(Estilo.crearGrupo(" ", pista));

        String[] columnas = {"ID Pres.", "ID Usu.", "Nombre", "Año Ini", "Mes Ini", "Año Fin", "Mes Fin", "Estado"};
        modeloListar = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaListar = new JTable(modeloListar);
        Estilo.estilizarTabla(tablaListar);

        int[] anchos = {70, 70, 150, 60, 60, 60, 60, 80};
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
        String idUsuario = txtIdUsuarioListar.getText().trim();
        if (idUsuario.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de Usuario para cargar sus presupuestos.");
            return;
        }

        modeloListar.setRowCount(0);
        ArrayList<String> lista = crud.listarPresupuestosUsuario(idUsuario);

        if (lista != null && !lista.isEmpty()) {
            for (String fila : lista) {
                String[] datos = fila.split(",", -1);
                if (datos.length >= 8) {
                    modeloListar.addRow(new Object[]{
                            datos[0], datos[1], datos[2], datos[3], datos[4], datos[5], datos[6], datos[7]
                    });
                } else {
                    modeloListar.addRow(new Object[]{fila, "", "", "", "", "", "", ""});
                }
            }
        } else {
            modeloListar.addRow(new Object[]{"Sin datos", "No hay presupuestos.", "", "", "", "", "", ""});
        }
    }

    private void editarDesdeTabla(int fila) {
        if (valorTabla(fila, 7).isEmpty()) {
            return;
        }

        txtIdPresupuestoActualizar.setText(valorTabla(fila, 0));
        txtNombreActualizar.setText(valorTabla(fila, 2));
        txtAnioInicioActualizar.setText(valorTabla(fila, 3));
        txtMesInicioActualizar.setText(valorTabla(fila, 4));
        txtAnioFinActualizar.setText(valorTabla(fila, 5));
        txtMesFinActualizar.setText(valorTabla(fila, 6));
        txtEstadoActualizar.setText(valorTabla(fila, 7));

        seleccionarTab(TAB_ACTUALIZAR);
        txtNombreActualizar.requestFocusInWindow();
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloListar.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private JPanel crearTabInsertar() {
        txtIdPresupuestoInsertar = Estilo.crearCampo();
        txtIdUsuarioInsertar = Estilo.crearCampo();
        txtNombreInsertar = Estilo.crearCampo();
        txtAnioInicioInsertar = Estilo.crearCampo();
        txtMesInicioInsertar = Estilo.crearCampo();
        txtAnioFinInsertar = Estilo.crearCampo();
        txtMesFinInsertar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(4, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Presupuesto", txtIdPresupuestoInsertar));
        form.add(Estilo.crearGrupo("ID Usuario", txtIdUsuarioInsertar));
        form.add(Estilo.crearGrupo("Nombre", txtNombreInsertar));
        form.add(new JLabel());
        form.add(Estilo.crearGrupo("Año Inicio", txtAnioInicioInsertar));
        form.add(Estilo.crearGrupo("Mes Inicio (1-12)", txtMesInicioInsertar));
        form.add(Estilo.crearGrupo("Año Fin", txtAnioFinInsertar));
        form.add(Estilo.crearGrupo("Mes Fin (1-12)", txtMesFinInsertar));

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar presupuesto", this::insertar),
                Estilo.botonSecundario("Limpiar", this::limpiarInsertar));

        return crearFormulario(form, botones);
    }

    private void insertar() {
        String idPres = txtIdPresupuestoInsertar.getText().trim();
        String idUsu = txtIdUsuarioInsertar.getText().trim();
        String nombre = txtNombreInsertar.getText().trim();
        String anioIniStr = txtAnioInicioInsertar.getText().trim();
        String mesIniStr = txtMesInicioInsertar.getText().trim();
        String anioFinStr = txtAnioFinInsertar.getText().trim();
        String mesFinStr = txtMesFinInsertar.getText().trim();

        if (idPres.isEmpty() || idUsu.isEmpty() || nombre.isEmpty()) {
            Estilo.mostrarAviso(this, "Completa los campos obligatorios.");
            return;
        }

        try {
            int anioIni = Integer.parseInt(anioIniStr);
            int mesIni = Integer.parseInt(mesIniStr);
            int anioFin = Integer.parseInt(anioFinStr);
            int mesFin = Integer.parseInt(mesFinStr);

            boolean ok = crud.insertarPresupuesto(idPres, idUsu, nombre, anioIni, mesIni, anioFin, mesFin, usuarioActual());
            if (ok) {
                Estilo.mostrarInfo(this, "Presupuesto registrado correctamente.");
                limpiarInsertar();
            } else {
                Estilo.mostrarError(this, "No se pudo registrar el presupuesto.");
            }
        } catch (NumberFormatException e) {
            Estilo.mostrarAviso(this, "Los años y meses deben ser números enteros.");
        }
    }

    private void limpiarInsertar() {
        txtIdPresupuestoInsertar.setText("");
        txtIdUsuarioInsertar.setText("");
        txtNombreInsertar.setText("");
        txtAnioInicioInsertar.setText("");
        txtMesInicioInsertar.setText("");
        txtAnioFinInsertar.setText("");
        txtMesFinInsertar.setText("");
    }

    private JPanel crearTabActualizar() {
        txtIdPresupuestoActualizar = Estilo.crearCampo();
        txtIdPresupuestoActualizar.setPreferredSize(new Dimension(240, 40));
        
        txtNombreActualizar = Estilo.crearCampo();
        txtAnioInicioActualizar = Estilo.crearCampo();
        txtMesInicioActualizar = Estilo.crearCampo();
        txtAnioFinActualizar = Estilo.crearCampo();
        txtMesFinActualizar = Estilo.crearCampo();
        txtEstadoActualizar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(3, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("Nombre", txtNombreActualizar));
        form.add(Estilo.crearGrupo("Estado", txtEstadoActualizar));
        form.add(Estilo.crearGrupo("Año Inicio", txtAnioInicioActualizar));
        form.add(Estilo.crearGrupo("Mes Inicio", txtMesInicioActualizar));
        form.add(Estilo.crearGrupo("Año Fin", txtAnioFinActualizar));
        form.add(Estilo.crearGrupo("Mes Fin", txtMesFinActualizar));

        JPanel superior = new JPanel(new BorderLayout(0, 18));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda("ID Presupuesto", txtIdPresupuestoActualizar, new JLabel(" ")),
                BorderLayout.NORTH);
        superior.add(form, BorderLayout.CENTER);

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar cambios", this::actualizar),
                Estilo.botonSecundario("Limpiar", this::limpiarActualizar));

        return crearFormulario(superior, botones);
    }

    private void actualizar() {
        String idPres = txtIdPresupuestoActualizar.getText().trim();
        String nombre = txtNombreActualizar.getText().trim();
        String anioIniStr = txtAnioInicioActualizar.getText().trim();
        String mesIniStr = txtMesInicioActualizar.getText().trim();
        String anioFinStr = txtAnioFinActualizar.getText().trim();
        String mesFinStr = txtMesFinActualizar.getText().trim();
        String estado = txtEstadoActualizar.getText().trim();

        if (idPres.isEmpty() || nombre.isEmpty() || estado.isEmpty()) {
            Estilo.mostrarAviso(this, "Completa los campos obligatorios.");
            return;
        }

        try {
            int anioIni = Integer.parseInt(anioIniStr);
            int mesIni = Integer.parseInt(mesIniStr);
            int anioFin = Integer.parseInt(anioFinStr);
            int mesFin = Integer.parseInt(mesFinStr);

            boolean ok = crud.actualizarPresupuesto(idPres, nombre, anioIni, mesIni, anioFin, mesFin, estado, usuarioActual());
            if (ok) {
                Estilo.mostrarInfo(this, "Presupuesto actualizado correctamente.");
                limpiarActualizar();
            } else {
                Estilo.mostrarError(this, "No se pudo actualizar el presupuesto.");
            }
        } catch (NumberFormatException e) {
            Estilo.mostrarAviso(this, "Los años y meses deben ser números enteros.");
        }
    }

    private void limpiarActualizar() {
        txtIdPresupuestoActualizar.setText("");
        txtNombreActualizar.setText("");
        txtAnioInicioActualizar.setText("");
        txtMesInicioActualizar.setText("");
        txtAnioFinActualizar.setText("");
        txtMesFinActualizar.setText("");
        txtEstadoActualizar.setText("");
    }

    private JPanel crearTabEliminar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdEliminar = Estilo.crearCampo();
        txtIdEliminar.setPreferredSize(new Dimension(240, 40));
        txtIdEliminar.addActionListener(e -> eliminar());

        panel.add(crearFilaBusqueda("ID Presupuesto", txtIdEliminar, Estilo.botonPeligro("Eliminar", this::eliminar)),
                BorderLayout.NORTH);

        JLabel aviso = new JLabel("Escribe el ID del presupuesto que quieres eliminar.");
        aviso.setFont(Estilo.fuente(Font.PLAIN, 13));
        aviso.setForeground(Estilo.TEXTO_SECUNDARIO);
        aviso.setVerticalAlignment(SwingConstants.TOP);
        panel.add(aviso, BorderLayout.CENTER);

        return panel;
    }

    private void eliminar() {
        String id = txtIdEliminar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del presupuesto.");
            return;
        }

        boolean confirmado = Estilo.confirmar(this,
                "¿Seguro que quieres eliminar el presupuesto con ID: " + id + "?",
                "Confirmar",
                "Sí, eliminar");
        if (!confirmado) {
            return;
        }

        boolean ok = crud.eliminarPresupuesto(id);
        if (ok) {
            Estilo.mostrarInfo(this, "Presupuesto eliminado correctamente.");
            txtIdEliminar.setText("");
            if (!txtIdUsuarioListar.getText().isEmpty()) {
                cargarLista();
            }
        } else {
            Estilo.mostrarError(this, "No se pudo eliminar el presupuesto.");
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
