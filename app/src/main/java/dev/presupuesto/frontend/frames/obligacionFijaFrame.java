package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudObligacionFija;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class obligacionFijaFrame extends JPanel {

    private static final String TAB_CONSULTAR = "Consultar";
    private static final String TAB_LISTAR = "Listar";
    private static final String TAB_INSERTAR = "Insertar";
    private static final String TAB_ACTUALIZAR = "Actualizar";
    private static final String TAB_ELIMINAR = "Eliminar";

    private CrudObligacionFija crud = new CrudObligacionFija();
    private hubFrame ventanaPrincipal;

    private CardLayout cardTabs;
    private JPanel panelTabs;
    private final Map<String, JButton> botonesTab = new LinkedHashMap<>();
    private String tabActiva = TAB_CONSULTAR;

    private JTextField txtIdConsultar;
    private JTextArea areaConsultar;

    private JTextField txtIdUsuarioListar;
    private JComboBox<String> comboVigenteListar;
    private DefaultTableModel modeloListar;
    private JTable tablaListar;

    private JTextField txtIdUsuarioInsertar, txtIdSubcategoriaInsertar, txtNombreInsertar,
            txtDescripcionInsertar, txtMontoMensualInsertar, txtDiaVencimientoInsertar,
            txtFechaInicioInsertar, txtFechaFinInsertar, txtCreadoPorInsertar;

    private JTextField txtIdObligacionActualizar, txtIdSubcategoriaActualizar, txtNombreActualizar,
            txtDescripcionActualizar, txtMontoMensualActualizar, txtDiaVencimientoActualizar,
            txtFechaInicioActualizar, txtFechaFinActualizar, txtModificadoPorActualizar;

    private JTextField txtIdEliminar;

    public obligacionFijaFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Obligaciones Fijas",
                "Administra los pagos recurrentes de cada usuario",
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


    // ---------- Consultar ----------

    private JPanel crearTabConsultar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdConsultar = Estilo.crearCampo();
        txtIdConsultar.setPreferredSize(new Dimension(240, 40));
        txtIdConsultar.addActionListener(e -> consultar());

        panel.add(crearFilaBusqueda("ID Obligación", txtIdConsultar, Estilo.botonPrimario("Buscar", this::consultar)),
                BorderLayout.NORTH);

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
            Estilo.mostrarAviso(this, "Ingresa el ID de la obligación que quieres buscar.");
            return;
        }

        String resultado = crud.consultarObligacion(id);
        if (resultado != null && !resultado.isEmpty()) {
            areaConsultar.setText("--------------------------\nBusqueda Completada\n--------------------------\n\n" + resultado);
        } else {
            areaConsultar.setText("[!] No se encontro la obligacion con ID: " + id);
        }
    }


    // ---------- Listar ----------

    private JPanel crearTabListar() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        txtIdUsuarioListar = Estilo.crearCampo();
        txtIdUsuarioListar.setPreferredSize(new Dimension(200, 40));
        txtIdUsuarioListar.addActionListener(e -> cargarLista());

        comboVigenteListar = new JComboBox<>(new String[]{"Todas", "Vigentes", "No vigentes"});
        comboVigenteListar.setFont(Estilo.fuente(Font.PLAIN, 13));
        comboVigenteListar.setPreferredSize(new Dimension(140, 40));

        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.add(txtIdUsuarioListar, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        derecha.setOpaque(false);
        derecha.add(comboVigenteListar);
        derecha.add(Estilo.botonPrimario("Cargar obligaciones", this::cargarLista));
        fila.add(derecha, BorderLayout.EAST);

        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenedor.setOpaque(false);
        contenedor.add(Estilo.crearGrupo("ID Usuario", fila));

        JPanel superior = new JPanel(new BorderLayout(0, 8));
        superior.setOpaque(false);
        superior.add(contenedor, BorderLayout.NORTH);

        JLabel pista = new JLabel("Doble clic en una fila para editar esa obligación");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Tema.textoSecundario());
        pista.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        superior.add(pista, BorderLayout.SOUTH);

        String[] columnas = {"ID", "Nombre", "Día vence", "Monto"};
        modeloListar = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaListar = new JTable(modeloListar);
        Estilo.estilizarTabla(tablaListar);

        int[] anchos = {90, 220, 90, 110};
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

        panel.add(superior, BorderLayout.NORTH);
        panel.add(Estilo.crearScroll(tablaListar), BorderLayout.CENTER);
        return panel;
    }

    private void cargarLista() {
        String idUsuario = txtIdUsuarioListar.getText().trim();
        if (idUsuario.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del usuario para cargar sus obligaciones.");
            return;
        }

        modeloListar.setRowCount(0);
        Boolean vigente = mapearVigente(comboVigenteListar.getSelectedIndex());
        ArrayList<String> lista = crud.listarObligacionesUsuario(idUsuario, vigente);

        if (lista != null && !lista.isEmpty()) {
            for (String fila : lista) {
                String[] datos = parsearFilaListado(fila);
                if (datos != null) {
                    modeloListar.addRow(new Object[]{datos[0], datos[1], datos[2], formatearMonto(datos[3])});
                } else {
                    modeloListar.addRow(new Object[]{fila, "", "", ""});
                }
            }
        } else {
            modeloListar.addRow(new Object[]{"Sin datos", "No hay obligaciones registradas para ese usuario.", "", ""});
        }
    }

    private Boolean mapearVigente(int indiceCombo) {
        if (indiceCombo == 1) {
            return Boolean.TRUE;
        }
        if (indiceCombo == 2) {
            return Boolean.FALSE;
        }
        return null;
    }

    private String[] parsearFilaListado(String fila) {
        int idxSeparador = fila.indexOf(" - ");
        if (idxSeparador == -1) {
            return null;
        }
        String id = fila.substring(0, idxSeparador).trim();
        String resto = fila.substring(idxSeparador + 3);

        int idxDia = resto.indexOf(" [Dia ");
        if (idxDia == -1) {
            return null;
        }
        String nombre = resto.substring(0, idxDia).trim();

        int idxCierreDia = resto.indexOf("] (L.", idxDia);
        if (idxCierreDia == -1) {
            return null;
        }
        String dia = resto.substring(idxDia + 6, idxCierreDia).trim();

        String monto = resto.substring(idxCierreDia + 5).trim();
        if (monto.endsWith(")")) {
            monto = monto.substring(0, monto.length() - 1);
        }

        return new String[]{id, nombre, dia, monto};
    }

    private void editarDesdeTabla(int fila) {
        String id = valorTabla(fila, 0);
        if (id.isEmpty() || id.equals("Sin datos")) {
            return;
        }

        txtIdObligacionActualizar.setText(id);
        seleccionarTab(TAB_ACTUALIZAR);
        cargarParaActualizar();
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloListar.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }


    // ---------- Insertar ----------

    private JComponent crearTabInsertar() {
        txtIdUsuarioInsertar = Estilo.crearCampo();
        txtIdSubcategoriaInsertar = Estilo.crearCampo();
        txtNombreInsertar = Estilo.crearCampo();
        txtDescripcionInsertar = Estilo.crearCampo();
        txtMontoMensualInsertar = Estilo.crearCampo();
        txtDiaVencimientoInsertar = Estilo.crearCampo();
        txtFechaInicioInsertar = Estilo.crearCampo();
        txtFechaFinInsertar = Estilo.crearCampo();
        txtCreadoPorInsertar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(5, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Usuario", txtIdUsuarioInsertar));
        form.add(Estilo.crearGrupo("ID Subcategoría", txtIdSubcategoriaInsertar));
        form.add(Estilo.crearGrupo("Nombre", txtNombreInsertar));
        form.add(Estilo.crearGrupo("Descripción", txtDescripcionInsertar));
        form.add(Estilo.crearGrupo("Monto mensual (L.)", txtMontoMensualInsertar));
        form.add(Estilo.crearGrupo("Día vencimiento (1-31)", txtDiaVencimientoInsertar));
        form.add(Estilo.crearGrupo("Fecha inicio (AAAA-MM-DD)", txtFechaInicioInsertar));
        form.add(Estilo.crearGrupo("Fecha fin (AAAA-MM-DD)", txtFechaFinInsertar));
        form.add(Estilo.crearGrupo("Creado por", txtCreadoPorInsertar));
        form.add(new JLabel()); // Relleno para balancear la cuadrícula de 5x2

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar obligación", this::insertar),
                Estilo.botonSecundario("Limpiar", this::limpiarInsertar));

        return Estilo.crearScroll(crearFormulario(form, botones));
    }

    private void insertar() {
        String idUsuario = txtIdUsuarioInsertar.getText().trim();
        String idSubcategoria = txtIdSubcategoriaInsertar.getText().trim();
        String nombre = txtNombreInsertar.getText().trim();
        String descripcion = txtDescripcionInsertar.getText().trim();
        String monto = txtMontoMensualInsertar.getText().trim();
        String dia = txtDiaVencimientoInsertar.getText().trim();
        String fechaInicio = txtFechaInicioInsertar.getText().trim();
        String fechaFin = txtFechaFinInsertar.getText().trim();
        String creadoPor = txtCreadoPorInsertar.getText().trim();

        String error = validarDatosInsertar(idUsuario, idSubcategoria, nombre, monto, dia, fechaInicio, fechaFin, creadoPor);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        boolean ok = crud.insertarObligacion(idUsuario, idSubcategoria, nombre, descripcion,
                Double.parseDouble(monto), Integer.parseInt(dia),
                timestampInicio(fechaInicio), timestampFin(fechaFin), creadoPor);
        if (ok) {
            Estilo.mostrarInfo(this, "Obligación registrada correctamente.");
            limpiarInsertar();
            refrescarListaSiHayCargada();
        } else {
            Estilo.mostrarError(this, "No se pudo registrar la obligación. Revisa la consola para ver el detalle.");
        }
    }

    private void limpiarInsertar() {
        txtIdUsuarioInsertar.setText("");
        txtIdSubcategoriaInsertar.setText("");
        txtNombreInsertar.setText("");
        txtDescripcionInsertar.setText("");
        txtMontoMensualInsertar.setText("");
        txtDiaVencimientoInsertar.setText("");
        txtFechaInicioInsertar.setText("");
        txtFechaFinInsertar.setText("");
        txtCreadoPorInsertar.setText("");
    }


    // ---------- Actualizar ----------

    private JComponent crearTabActualizar() {
        txtIdObligacionActualizar = Estilo.crearCampo();
        txtIdObligacionActualizar.setPreferredSize(new Dimension(240, 40));
        txtIdObligacionActualizar.addActionListener(e -> cargarParaActualizar());

        txtIdSubcategoriaActualizar = Estilo.crearCampo();
        txtNombreActualizar = Estilo.crearCampo();
        txtDescripcionActualizar = Estilo.crearCampo();
        txtMontoMensualActualizar = Estilo.crearCampo();
        txtDiaVencimientoActualizar = Estilo.crearCampo();
        txtFechaInicioActualizar = Estilo.crearCampo();
        txtFechaFinActualizar = Estilo.crearCampo();
        txtModificadoPorActualizar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(4, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Subcategoría", txtIdSubcategoriaActualizar));
        form.add(Estilo.crearGrupo("Nombre", txtNombreActualizar));
        form.add(Estilo.crearGrupo("Descripción", txtDescripcionActualizar));
        form.add(Estilo.crearGrupo("Monto mensual (L.)", txtMontoMensualActualizar));
        form.add(Estilo.crearGrupo("Día vencimiento (1-31)", txtDiaVencimientoActualizar));
        form.add(Estilo.crearGrupo("Fecha inicio (AAAA-MM-DD)", txtFechaInicioActualizar));
        form.add(Estilo.crearGrupo("Fecha fin (AAAA-MM-DD)", txtFechaFinActualizar));
        form.add(Estilo.crearGrupo("Modificado por", txtModificadoPorActualizar));

        JLabel pista = new JLabel("\"Cargar datos\" solo trae Nombre, Monto y Día; completa Subcategoría, Descripción y Fechas.");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Tema.textoSecundario());
        pista.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JPanel superior = new JPanel(new BorderLayout(0, 8));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda("ID Obligación", txtIdObligacionActualizar,
                Estilo.botonSecundario("Cargar datos", this::cargarParaActualizar)), BorderLayout.NORTH);
        superior.add(pista, BorderLayout.CENTER);

        JPanel contenedorSuperior = new JPanel(new BorderLayout(0, 18));
        contenedorSuperior.setOpaque(false);
        contenedorSuperior.add(superior, BorderLayout.NORTH);
        contenedorSuperior.add(form, BorderLayout.CENTER);

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar cambios", this::actualizar),
                Estilo.botonSecundario("Limpiar", this::limpiarActualizar));

        return Estilo.crearScroll(crearFormulario(contenedorSuperior, botones));
    }

    private void cargarParaActualizar() {
        String id = txtIdObligacionActualizar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la obligación que quieres actualizar.");
            return;
        }

        String resultado = crud.consultarObligacion(id);
        if (resultado == null) {
            Estilo.mostrarAviso(this, "No se encontro la obligacion con ID: " + id);
            return;
        }

        String nombre = extraerValor(resultado, "Obligacion: ", "\n");
        String monto = extraerValor(resultado, "Monto: L.", "\n");
        String dia = extraerValor(resultado, "Vence dia: ", "\n");

        txtNombreActualizar.setText(nombre);
        txtMontoMensualActualizar.setText(montoPlano(monto));
        txtDiaVencimientoActualizar.setText(dia);
        txtNombreActualizar.requestFocusInWindow();
    }

    private void actualizar() {
        String id = txtIdObligacionActualizar.getText().trim();
        String idSubcategoria = txtIdSubcategoriaActualizar.getText().trim();
        String nombre = txtNombreActualizar.getText().trim();
        String descripcion = txtDescripcionActualizar.getText().trim();
        String monto = txtMontoMensualActualizar.getText().trim();
        String dia = txtDiaVencimientoActualizar.getText().trim();
        String fechaInicio = txtFechaInicioActualizar.getText().trim();
        String fechaFin = txtFechaFinActualizar.getText().trim();
        String modificadoPor = txtModificadoPorActualizar.getText().trim();

        String error = validarDatosActualizar(id, idSubcategoria, nombre, monto, dia, fechaInicio, fechaFin, modificadoPor);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        if (crud.consultarObligacion(id) == null) {
            Estilo.mostrarAviso(this, "No existe una obligación con el ID: " + id);
            return;
        }

        boolean ok = crud.actualizarObligacion(id, idSubcategoria, nombre, descripcion,
                Double.parseDouble(monto), Integer.parseInt(dia),
                timestampInicio(fechaInicio), timestampFin(fechaFin), modificadoPor);
        if (ok) {
            Estilo.mostrarInfo(this, "Obligación actualizada correctamente.");
            limpiarActualizar();
            refrescarListaSiHayCargada();
        } else {
            Estilo.mostrarError(this, "No se pudo actualizar la obligación. Revisa la consola para ver el detalle.");
        }
    }

    private void limpiarActualizar() {
        txtIdObligacionActualizar.setText("");
        txtIdSubcategoriaActualizar.setText("");
        txtNombreActualizar.setText("");
        txtDescripcionActualizar.setText("");
        txtMontoMensualActualizar.setText("");
        txtDiaVencimientoActualizar.setText("");
        txtFechaInicioActualizar.setText("");
        txtFechaFinActualizar.setText("");
        txtModificadoPorActualizar.setText("");
    }


    // ---------- Eliminar ----------

    private JPanel crearTabEliminar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdEliminar = Estilo.crearCampo();
        txtIdEliminar.setPreferredSize(new Dimension(240, 40));
        txtIdEliminar.addActionListener(e -> eliminar());

        panel.add(crearFilaBusqueda("ID Obligación", txtIdEliminar, Estilo.botonPeligro("Eliminar obligación", this::eliminar)),
                BorderLayout.NORTH);

        JLabel aviso = new JLabel("Escribe el ID de la obligación que quieres eliminar. Antes de continuar se te pedirá confirmación.");
        aviso.setFont(Estilo.fuente(Font.PLAIN, 13));
        aviso.setForeground(Tema.textoSecundario());
        aviso.setVerticalAlignment(SwingConstants.TOP);
        panel.add(aviso, BorderLayout.CENTER);

        return panel;
    }

    private void eliminar() {
        String id = txtIdEliminar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la obligación que quieres eliminar.");
            return;
        }

        if (crud.consultarObligacion(id) == null) {
            Estilo.mostrarAviso(this, "No se encontro la obligacion con ID: " + id);
            return;
        }

        boolean confirmado = Estilo.confirmar(this,
                "¿Seguro que quieres eliminar la obligación con ID: " + id + "?",
                "Confirmar eliminación",
                "Sí, eliminar");
        if (!confirmado) {
            return;
        }

        boolean ok = crud.eliminarObligacion(id, usuarioActual());
        if (ok) {
            Estilo.mostrarInfo(this, "Obligación eliminada correctamente.");
            txtIdEliminar.setText("");
            refrescarListaSiHayCargada();
        } else {
            Estilo.mostrarError(this, "No se pudo eliminar la obligación. Revisa la consola para ver el detalle.");
        }
    }


    // ---------- Utilidades ----------

    private void refrescarListaSiHayCargada() {
        if (txtIdUsuarioListar != null && !txtIdUsuarioListar.getText().trim().isEmpty()) {
            cargarLista();
        }
    }

    private String usuarioActual() {
        return ventanaPrincipal.getNombreCuenta();
    }

    private String validarDatosInsertar(String idUsuario, String idSubcategoria, String nombre,
                                         String monto, String dia, String fechaInicio, String fechaFin, String creadoPor) {
        if (idUsuario.isEmpty()) {
            return "El ID de usuario es obligatorio.";
        }
        if (idSubcategoria.isEmpty()) {
            return "El ID de subcategoría es obligatorio.";
        }
        if (nombre.isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (!monto.matches("\\d+(\\.\\d{1,2})?")) {
            return "El monto debe ser un número positivo, por ejemplo: 1500 o 1500.50";
        }
        String errorDia = validarDia(dia);
        if (errorDia != null) {
            return errorDia;
        }
        String errorFechas = validarFechas(fechaInicio, fechaFin);
        if (errorFechas != null) {
            return errorFechas;
        }
        if (creadoPor.isEmpty()) {
            return "El campo \"Creado por\" es obligatorio.";
        }
        return null;
    }

    private String validarDatosActualizar(String idObligacion, String idSubcategoria, String nombre, String monto,
                                           String dia, String fechaInicio, String fechaFin, String modificadoPor) {
        if (idObligacion.isEmpty()) {
            return "El ID de obligación es obligatorio.";
        }
        if (idSubcategoria.isEmpty()) {
            return "El ID de subcategoría es obligatorio.";
        }
        if (nombre.isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (!monto.matches("\\d+(\\.\\d{1,2})?")) {
            return "El monto debe ser un número positivo, por ejemplo: 1500 o 1500.50";
        }
        String errorDia = validarDia(dia);
        if (errorDia != null) {
            return errorDia;
        }
        String errorFechas = validarFechas(fechaInicio, fechaFin);
        if (errorFechas != null) {
            return errorFechas;
        }
        if (modificadoPor.isEmpty()) {
            return "El campo \"Modificado por\" es obligatorio.";
        }
        return null;
    }

    private String validarDia(String texto) {
        try {
            int dia = Integer.parseInt(texto);
            if (dia < 1 || dia > 31) {
                return "El día de vencimiento debe estar entre 1 y 31.";
            }
        } catch (NumberFormatException e) {
            return "El día de vencimiento debe ser un número entre 1 y 31.";
        }
        return null;
    }

    private String validarFechas(String fechaInicio, String fechaFin) {
        LocalDate inicio, fin;
        try {
            inicio = LocalDate.parse(fechaInicio);
        } catch (DateTimeParseException e) {
            return "La fecha de inicio no es válida. Usa el formato AAAA-MM-DD.";
        }
        try {
            fin = LocalDate.parse(fechaFin);
        } catch (DateTimeParseException e) {
            return "La fecha de fin no es válida. Usa el formato AAAA-MM-DD.";
        }
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            return "Las fechas de inicio y fin son obligatorias.";
        }
        if (inicio.isAfter(fin)) {
            return "La fecha de inicio no puede ser posterior a la fecha de fin.";
        }
        return null;
    }

    private String timestampInicio(String fecha) {
        return fecha + " 00:00:00";
    }

    private String timestampFin(String fecha) {
        return fecha + " 23:59:59";
    }

    private String extraerValor(String texto, String etiqueta, String separador) {
        int idx = texto.indexOf(etiqueta);
        if (idx == -1) {
            return "";
        }
        int inicio = idx + etiqueta.length();
        int fin = separador == null ? -1 : texto.indexOf(separador, inicio);
        String valor = fin == -1 ? texto.substring(inicio) : texto.substring(inicio, fin);
        return valor.trim();
    }

    private String formatearMonto(String bruto) {
        try {
            return String.format(Locale.US, "%,.2f", Double.parseDouble(bruto.trim()));
        } catch (NumberFormatException e) {
            return bruto;
        }
    }

    private String montoPlano(String texto) {
        try {
            return String.format(Locale.US, "%.2f", Double.parseDouble(texto.replace(",", "").trim()));
        } catch (NumberFormatException e) {
            return texto;
        }
    }

    private void limpiarTodo() {
        limpiarInsertar();
        limpiarActualizar();
        txtIdConsultar.setText("");
        areaConsultar.setText("");
        txtIdEliminar.setText("");
        if (txtIdUsuarioListar != null) {
            txtIdUsuarioListar.setText("");
        }
        if (comboVigenteListar != null) {
            comboVigenteListar.setSelectedIndex(0);
        }
        modeloListar.setRowCount(0);
        seleccionarTab(TAB_CONSULTAR);
    }

    private JPanel crearFilaBusqueda(String etiqueta, JTextField campo, JButton boton) {
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