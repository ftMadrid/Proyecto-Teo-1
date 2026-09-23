package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudPresupuestoDetalle;
import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class presupuestoDetalleFrame extends JPanel {

    private static final String TAB_CONSULTAR = "Consultar";
    private static final String TAB_LISTAR = "Listar";
    private static final String TAB_INSERTAR = "Insertar";
    private static final String TAB_ACTUALIZAR = "Actualizar";
    private static final String TAB_ELIMINAR = "Eliminar";

    private CrudPresupuestoDetalle crud = new CrudPresupuestoDetalle();
    private hubFrame ventanaPrincipal;

    private CardLayout cardTabs;
    private JPanel panelTabs;
    private final Map<String, JButton> botonesTab = new LinkedHashMap<>();
    private String tabActiva = TAB_CONSULTAR;

    private JTextField txtIdConsultar;
    private JTextArea areaConsultar;

    private JTextField txtIdPresupuestoListar;
    private DefaultTableModel modeloListar;
    private JTable tablaListar;

    private JTextField txtIdPresupuestoInsertar, txtIdSubcategoriaInsertar,
            txtMontoMensualInsertar, txtObservacionesInsertar;

    private JTextField txtIdDetalleActualizar, txtMontoMensualActualizar,
            txtObservacionesActualizar;

    private JTextField txtIdEliminar;

    public presupuestoDetalleFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Detalles de Presupuesto",
                "Administra los montos por categoría de cada presupuesto",
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

        panel.add(crearFilaBusqueda("ID Detalle", txtIdConsultar, Estilo.botonPrimario("Buscar", this::consultar)),
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
            Estilo.mostrarAviso(this, "Ingresa el ID del detalle que quieres buscar.");
            return;
        }

        String resultado = crud.consultarPresupuestoDetalle(id);
        if (resultado != null && !resultado.isEmpty()) {
            areaConsultar.setText("--------------------------\nBusqueda Completada\n--------------------------\n\n" + resultado);
        } else {
            areaConsultar.setText("[!] No se encontro el detalle con ID: " + id);
        }
    }

    private JPanel crearTabListar() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        txtIdPresupuestoListar = Estilo.crearCampo();
        txtIdPresupuestoListar.setPreferredSize(new Dimension(240, 40));
        txtIdPresupuestoListar.addActionListener(e -> cargarLista());

        JPanel superior = new JPanel(new BorderLayout(0, 8));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda("ID Presupuesto", txtIdPresupuestoListar,
                Estilo.botonPrimario("Cargar detalles", this::cargarLista)), BorderLayout.NORTH);

        JLabel pista = new JLabel("Doble clic en una fila para editar ese detalle");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Tema.textoSecundario());
        pista.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        superior.add(pista, BorderLayout.SOUTH);

        String[] columnas = {"ID", "Subcategoría", "Monto"};
        modeloListar = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaListar = new JTable(modeloListar);
        Estilo.estilizarTabla(tablaListar);

        int[] anchos = {90, 220, 120};
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
        String idPresupuesto = txtIdPresupuestoListar.getText().trim();
        if (idPresupuesto.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del presupuesto para cargar sus detalles.");
            return;
        }

        modeloListar.setRowCount(0);
        ArrayList<String> lista = crud.listarDetallesPresupuesto(idPresupuesto);

        if (lista != null && !lista.isEmpty()) {
            for (String fila : lista) {
                String[] datos = parsearFilaListado(fila);
                if (datos != null) {
                    modeloListar.addRow(new Object[]{datos[0], datos[1], formatearMonto(datos[2])});
                } else {
                    modeloListar.addRow(new Object[]{fila, "", ""});
                }
            }
        } else {
            modeloListar.addRow(new Object[]{"Sin datos", "No hay detalles registrados para ese presupuesto.", ""});
        }
    }

    private String[] parsearFilaListado(String fila) {
        int idxSeparador = fila.indexOf(" - ");
        if (idxSeparador == -1) {
            return null;
        }
        String id = fila.substring(0, idxSeparador).trim();
        String resto = fila.substring(idxSeparador + 3);

        int idxInicioMonto = resto.indexOf(" [L.");
        if (idxInicioMonto == -1) {
            return null;
        }
        String subcategoria = resto.substring(0, idxInicioMonto).trim();

        String monto = resto.substring(idxInicioMonto + 4).trim();
        if (monto.endsWith("]")) {
            monto = monto.substring(0, monto.length() - 1);
        }

        return new String[]{id, subcategoria, monto};
    }

    private void editarDesdeTabla(int fila) {
        String id = valorTabla(fila, 0);
        if (id.isEmpty() || id.equals("Sin datos")) {
            return;
        }

        txtIdDetalleActualizar.setText(id);
        seleccionarTab(TAB_ACTUALIZAR);
        cargarParaActualizar();
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloListar.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private String usuarioActual() {
        return ventanaPrincipal.getNombreCuenta();
    }

    private JPanel crearTabInsertar() {
        txtIdPresupuestoInsertar = Estilo.crearCampo();
        txtIdSubcategoriaInsertar = Estilo.crearCampo();
        txtMontoMensualInsertar = Estilo.crearCampo();
        txtObservacionesInsertar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(2, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Presupuesto", txtIdPresupuestoInsertar));
        form.add(Estilo.crearGrupo("ID Subcategoría", txtIdSubcategoriaInsertar));
        form.add(Estilo.crearGrupo("Monto mensual (L.)", txtMontoMensualInsertar));
        form.add(Estilo.crearGrupo("Observaciones", txtObservacionesInsertar));

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar detalle", this::insertar),
                Estilo.botonSecundario("Limpiar", this::limpiarInsertar));

        return crearFormulario(form, botones);
    }

    private void insertar() {
        String idPresupuesto = txtIdPresupuestoInsertar.getText().trim();
        String idSubcategoria = txtIdSubcategoriaInsertar.getText().trim();
        String monto = txtMontoMensualInsertar.getText().trim();
        String observaciones = txtObservacionesInsertar.getText().trim();
        String creadoPor = usuarioActual();

        String error = validarDatosInsertar(idPresupuesto, idSubcategoria, monto, creadoPor);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        boolean ok = crud.insertarPresupuestoDetalle(idPresupuesto, idSubcategoria,
                Double.parseDouble(monto), observaciones, creadoPor);
        if (ok) {
            Estilo.mostrarInfo(this, "Detalle registrado correctamente.");
            limpiarInsertar();
            refrescarListaSiHayCargada();
        } else {
            Estilo.mostrarError(this, "No se pudo registrar el detalle. Revisa la consola para ver el detalle.");
        }
    }

    private void limpiarInsertar() {
        txtIdPresupuestoInsertar.setText("");
        txtIdSubcategoriaInsertar.setText("");
        txtMontoMensualInsertar.setText("");
        txtObservacionesInsertar.setText("");
    }

    private JPanel crearTabActualizar() {
        txtIdDetalleActualizar = Estilo.crearCampo();
        txtIdDetalleActualizar.setPreferredSize(new Dimension(240, 40));
        txtIdDetalleActualizar.addActionListener(e -> cargarParaActualizar());

        txtMontoMensualActualizar = Estilo.crearCampo();
        txtObservacionesActualizar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(1, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("Monto mensual (L.)", txtMontoMensualActualizar));
        form.add(Estilo.crearGrupo("Observaciones", txtObservacionesActualizar));

        JPanel superior = new JPanel(new BorderLayout(0, 18));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda("ID Detalle", txtIdDetalleActualizar,
                Estilo.botonSecundario("Cargar datos", this::cargarParaActualizar)), BorderLayout.NORTH);
        superior.add(form, BorderLayout.CENTER);

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar cambios", this::actualizar),
                Estilo.botonSecundario("Limpiar", this::limpiarActualizar));

        return crearFormulario(superior, botones);
    }

    private void cargarParaActualizar() {
        String id = txtIdDetalleActualizar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del detalle que quieres actualizar.");
            return;
        }

        String resultado = crud.consultarPresupuestoDetalle(id);
        if (resultado == null || resultado.isEmpty()) {
            Estilo.mostrarAviso(this, "No se encontro el detalle con ID: " + id);
            return;
        }

        try {
            String[] lineas = resultado.split("\n");
            String monto = lineas[3].substring("Monto: L. ".length());
            String observaciones = lineas[4].substring("Observación: ".length());

            txtMontoMensualActualizar.setText(montoPlano(monto));
            txtObservacionesActualizar.setText(observaciones);
        } catch (Exception e) {
            Estilo.mostrarError(this, "Error al procesar los datos.");
        }
        
        txtMontoMensualActualizar.requestFocusInWindow();
    }

    private void actualizar() {
        String id = txtIdDetalleActualizar.getText().trim();
        String monto = txtMontoMensualActualizar.getText().trim();
        String observaciones = txtObservacionesActualizar.getText().trim();
        String modificadoPor = usuarioActual();

        String error = validarDatosActualizar(id, monto, modificadoPor);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        if (crud.consultarPresupuestoDetalle(id) == null) {
            Estilo.mostrarAviso(this, "No existe un detalle con el ID: " + id);
            return;
        }

        boolean ok = crud.actualizarPresupuestoDetalle(id, Double.parseDouble(monto), observaciones, modificadoPor);
        if (ok) {
            Estilo.mostrarInfo(this, "Detalle actualizado correctamente.");
            limpiarActualizar();
            refrescarListaSiHayCargada();
        } else {
            Estilo.mostrarError(this, "No se pudo actualizar el detalle. Revisa la consola para ver el detalle.");
        }
    }

    private void limpiarActualizar() {
        txtIdDetalleActualizar.setText("");
        txtMontoMensualActualizar.setText("");
        txtObservacionesActualizar.setText("");
    }

    private JPanel crearTabEliminar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdEliminar = Estilo.crearCampo();
        txtIdEliminar.setPreferredSize(new Dimension(240, 40));
        txtIdEliminar.addActionListener(e -> eliminar());

        panel.add(crearFilaBusqueda("ID Detalle", txtIdEliminar, Estilo.botonPeligro("Eliminar detalle", this::eliminar)),
                BorderLayout.NORTH);

        JLabel aviso = new JLabel("Escribe el ID del detalle que quieres eliminar. Antes de continuar se te pedirá confirmación.");
        aviso.setFont(Estilo.fuente(Font.PLAIN, 13));
        aviso.setForeground(Tema.textoSecundario());
        aviso.setVerticalAlignment(SwingConstants.TOP);
        panel.add(aviso, BorderLayout.CENTER);

        return panel;
    }

    private void eliminar() {
        String id = txtIdEliminar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del detalle que quieres eliminar.");
            return;
        }

        if (crud.consultarPresupuestoDetalle(id) == null) {
            Estilo.mostrarAviso(this, "No se encontro el detalle con ID: " + id);
            return;
        }

        boolean confirmado = Estilo.confirmar(this,
                "¿Seguro que quieres eliminar el detalle con ID: " + id + "?",
                "Confirmar eliminación",
                "Sí, eliminar");
        if (!confirmado) {
            return;
        }

        boolean ok = crud.eliminarPresupuestoDetalle(id);
        if (ok) {
            Estilo.mostrarInfo(this, "Detalle eliminado correctamente.");
            txtIdEliminar.setText("");
            refrescarListaSiHayCargada();
        } else {
            Estilo.mostrarError(this, "No se pudo eliminar el detalle. Revisa la consola para ver el detalle.");
        }
    }

    private void refrescarListaSiHayCargada() {
        if (txtIdPresupuestoListar != null && !txtIdPresupuestoListar.getText().trim().isEmpty()) {
            cargarLista();
        }
    }

    private String validarDatosInsertar(String idPresupuesto, String idSubcategoria,
                                         String monto, String creadoPor) {
        if (idPresupuesto.isEmpty()) {
            return "El ID de presupuesto es obligatorio.";
        }
        if (idSubcategoria.isEmpty()) {
            return "El ID de subcategoría es obligatorio.";
        }
        if (!monto.matches("\\d+(\\.\\d{1,2})?")) {
            return "El monto debe ser un número positivo, por ejemplo: 1500 o 1500.50";
        }
        if (creadoPor.isEmpty()) {
            return "El campo \"Creado por\" es obligatorio.";
        }
        return null;
    }

    private String validarDatosActualizar(String idDetalle, String monto, String modificadoPor) {
        if (idDetalle.isEmpty()) {
            return "El ID de detalle es obligatorio.";
        }
        if (!monto.matches("\\d+(\\.\\d{1,2})?")) {
            return "El monto debe ser un número positivo, por ejemplo: 1500 o 1500.50";
        }
        if (modificadoPor.isEmpty()) {
            return "El campo \"Modificado por\" es obligatorio.";
        }
        return null;
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
        txtIdEliminar.setText("");
        areaConsultar.setText("");
        if (txtIdPresupuestoListar != null) {
            txtIdPresupuestoListar.setText("");
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