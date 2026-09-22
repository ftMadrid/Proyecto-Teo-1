package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudTransaccion;
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

public class transaccionFrame extends JPanel {

    private static final String TAB_CONSULTAR = "Consultar";
    private static final String TAB_LISTAR = "Listar";
    private static final String TAB_INSERTAR = "Insertar";
    private static final String TAB_ACTUALIZAR = "Actualizar";
    private static final String TAB_ELIMINAR = "Eliminar";

    private CrudTransaccion crud = new CrudTransaccion();
    private hubFrame ventanaPrincipal;

    private CardLayout cardTabs;
    private JPanel panelTabs;
    private final Map<String, JButton> botonesTab = new LinkedHashMap<>();
    private String tabActiva = TAB_CONSULTAR;

    private JTextField txtIdConsultar;
    private JTextArea areaConsultar;

    private JTextField txtFiltroPresupuesto;
    private DefaultTableModel modeloListar;
    private JTable tablaListar;

    // Se eliminó txtIdtransaccionInsertar de la lista de inserción
    private JTextField txtIdusuarioInsertar, txtIdpresupuestoInsertar, txtAnioInsertar, txtMesInsertar, txtIdsubcategoriaInsertar, txtIdobligacionInsertar, txtTipotransaccionInsertar, txtDescripcionInsertar, txtMontoInsertar, txtFechaInsertar, txtMetodopagoInsertar, txtNumerofacturaInsertar, txtObservacionesInsertar;
    private JTextField txtIdtransaccionActualizar, txtIdsubcategoriaActualizar, txtTipotransaccionActualizar, txtDescripcionActualizar, txtMontoActualizar, txtFechaActualizar, txtMetodopagoActualizar, txtNumerofacturaActualizar, txtObservacionesActualizar;
    private JTextField txtIdEliminar;

    public transaccionFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Transacciones",
                "Gestiona ingresos, gastos y ahorros del presupuesto",
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
                if (!nombre.equals(tabActiva)) boton.setForeground(Tema.textoPrimario());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!nombre.equals(tabActiva)) boton.setForeground(Tema.textoSecundario());
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

        panel.add(crearFilaBusqueda("ID Transacción", txtIdConsultar, Estilo.botonPrimario("Buscar", this::consultar)), BorderLayout.NORTH);

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
            Estilo.mostrarAviso(this, "Ingresa el ID de la transacción a buscar.");
            return;
        }

        String resultado = crud.consultarTransaccion(id);
        if (resultado != null && !resultado.isEmpty()) {
            areaConsultar.setText("--------------------------\nBúsqueda Completada\n--------------------------\n\n" + resultado);
        } else {
            areaConsultar.setText("[!] No se encontró la transacción con ID: " + id);
        }
    }

    private JPanel crearTabListar() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        
        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraSuperior.setOpaque(false);
        
        txtFiltroPresupuesto = Estilo.crearCampo();
        txtFiltroPresupuesto.setPreferredSize(new Dimension(180, 40));
        
        JPanel filaBusqueda = new JPanel(new BorderLayout(12, 0));
        filaBusqueda.setOpaque(false);
        filaBusqueda.add(txtFiltroPresupuesto, BorderLayout.CENTER);
        filaBusqueda.add(Estilo.botonPrimario("Cargar Datos", this::cargarLista), BorderLayout.EAST);
        
        barraSuperior.add(Estilo.crearGrupo("Filtrar por ID Presupuesto (Requerido)", filaBusqueda));

        String[] columnas = {"ID", "Presupuesto", "Subcategoría", "Obligación", "Tipo", "Monto", "Fecha"};
        modeloListar = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };

        tablaListar = new JTable(modeloListar);
        Estilo.estilizarTabla(tablaListar);
        
        int[] anchos = {80, 100, 100, 80, 70, 80, 120};
        for (int i = 0; i < anchos.length; i++) {
            if (i < tablaListar.getColumnModel().getColumnCount())
                tablaListar.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        panel.add(barraSuperior, BorderLayout.NORTH);
        panel.add(Estilo.crearScroll(tablaListar), BorderLayout.CENTER);
        return panel;
    }

    private void cargarLista() {
        String idPresupuesto = txtFiltroPresupuesto.getText().trim();
        if (idPresupuesto.isEmpty()) {
            Estilo.mostrarAviso(this, "Debe ingresar el ID del presupuesto para listar las transacciones.");
            return;
        }

        modeloListar.setRowCount(0);
        ArrayList<String> lista = crud.listarTransaccionesPresupuesto(idPresupuesto, "");

        if (lista != null && !lista.isEmpty()) {
            for (String fila : lista) {
                String[] datos = fila.split(",", -1);
                if (datos.length >= 7) {
                    modeloListar.addRow(new Object[]{
                            datos[0], datos[1], datos[2], datos[3], datos[4], datos[5], datos[6]
                    });
                } else {
                    modeloListar.addRow(new Object[]{fila, "", "", "", "", "", ""});
                }
            }
        } else {
            modeloListar.addRow(new Object[]{"Sin datos", "No hay transacciones registradas en este presupuesto.", "", "", "", "", ""});
        }
    }

    private JComponent crearTabInsertar() {
        txtIdusuarioInsertar = Estilo.crearCampo();
        txtIdpresupuestoInsertar = Estilo.crearCampo();
        txtAnioInsertar = Estilo.crearCampo();
        txtMesInsertar = Estilo.crearCampo();
        txtIdsubcategoriaInsertar = Estilo.crearCampo();
        txtIdobligacionInsertar = Estilo.crearCampo();
        txtTipotransaccionInsertar = Estilo.crearCampo();
        txtDescripcionInsertar = Estilo.crearCampo();
        txtMontoInsertar = Estilo.crearCampo();
        txtFechaInsertar = Estilo.crearCampo();
        txtMetodopagoInsertar = Estilo.crearCampo();
        txtNumerofacturaInsertar = Estilo.crearCampo();
        txtObservacionesInsertar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(7, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Usuario", txtIdusuarioInsertar));
        form.add(Estilo.crearGrupo("ID Presupuesto", txtIdpresupuestoInsertar));
        form.add(Estilo.crearGrupo("ID Subcategoría", txtIdsubcategoriaInsertar));
        form.add(Estilo.crearGrupo("Año", txtAnioInsertar));
        form.add(Estilo.crearGrupo("Mes", txtMesInsertar));
        form.add(Estilo.crearGrupo("Tipo (Ingreso/Gasto/Ahorro)", txtTipotransaccionInsertar));
        form.add(Estilo.crearGrupo("Monto (L.)", txtMontoInsertar));
        form.add(Estilo.crearGrupo("Fecha (AAAA-MM-DD)", txtFechaInsertar));
        form.add(Estilo.crearGrupo("Método de Pago", txtMetodopagoInsertar));
        form.add(Estilo.crearGrupo("Descripción", txtDescripcionInsertar));
        form.add(Estilo.crearGrupo("Observaciones", txtObservacionesInsertar));
        form.add(Estilo.crearGrupo("ID Obligación (Opcional)", txtIdobligacionInsertar));
        form.add(Estilo.crearGrupo("No. Factura (Opcional)", txtNumerofacturaInsertar));
        form.add(new JLabel()); // Relleno para balancear la cuadrícula de 7x2

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar transacción", this::insertar),
                Estilo.botonSecundario("Limpiar", this::limpiarInsertar));

        JScrollPane scroll = Estilo.crearScroll(crearFormulario(form, botones));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private void insertar() {
        try {
            if (txtIdpresupuestoInsertar.getText().trim().isEmpty()) {
                Estilo.mostrarAviso(this, "Revisa los campos obligatorios antes de continuar.");
                return;
            }

            boolean res = crud.insertarTransaccion(
                txtIdusuarioInsertar.getText().trim(),
                txtIdpresupuestoInsertar.getText().trim(),
                Integer.parseInt(txtAnioInsertar.getText().trim()),
                Integer.parseInt(txtMesInsertar.getText().trim()),
                txtIdsubcategoriaInsertar.getText().trim(),
                txtIdobligacionInsertar.getText().trim().isEmpty() ? null : txtIdobligacionInsertar.getText().trim(),
                txtTipotransaccionInsertar.getText().trim(),
                txtDescripcionInsertar.getText().trim(),
                Double.parseDouble(txtMontoInsertar.getText().trim()),
                txtFechaInsertar.getText().trim(),
                txtMetodopagoInsertar.getText().trim(),
                txtNumerofacturaInsertar.getText().trim().isEmpty() ? null : txtNumerofacturaInsertar.getText().trim(),
                txtObservacionesInsertar.getText().trim().isEmpty() ? null : txtObservacionesInsertar.getText().trim(),
                usuarioActual()
            );

            if (res) {
                Estilo.mostrarInfo(this, "Transacción registrada correctamente.");
                limpiarInsertar();
            } else {
                Estilo.mostrarError(this, "No se pudo guardar la transacción.");
            }
        } catch(Exception e) {
            Estilo.mostrarError(this, "Error de formato: " + e.getMessage());
        }
    }

    private void limpiarInsertar() {
        txtIdusuarioInsertar.setText("");
        txtIdpresupuestoInsertar.setText("");
        txtAnioInsertar.setText("");
        txtMesInsertar.setText("");
        txtIdsubcategoriaInsertar.setText("");
        txtIdobligacionInsertar.setText("");
        txtTipotransaccionInsertar.setText("");
        txtDescripcionInsertar.setText("");
        txtMontoInsertar.setText("");
        txtFechaInsertar.setText("");
        txtMetodopagoInsertar.setText("");
        txtNumerofacturaInsertar.setText("");
        txtObservacionesInsertar.setText("");
    }

    private JComponent crearTabActualizar() {
        txtIdtransaccionActualizar = Estilo.crearCampo();
        txtIdtransaccionActualizar.setPreferredSize(new Dimension(240, 40));
        txtIdtransaccionActualizar.addActionListener(e -> cargarParaActualizar());
        
        txtIdsubcategoriaActualizar = Estilo.crearCampo();
        txtTipotransaccionActualizar = Estilo.crearCampo();
        txtDescripcionActualizar = Estilo.crearCampo();
        txtMontoActualizar = Estilo.crearCampo();
        txtFechaActualizar = Estilo.crearCampo();
        txtMetodopagoActualizar = Estilo.crearCampo();
        txtNumerofacturaActualizar = Estilo.crearCampo();
        txtObservacionesActualizar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(4, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID Subcategoría", txtIdsubcategoriaActualizar));
        form.add(Estilo.crearGrupo("Tipo", txtTipotransaccionActualizar));
        form.add(Estilo.crearGrupo("Monto (L.)", txtMontoActualizar));
        form.add(Estilo.crearGrupo("Fecha (AAAA-MM-DD)", txtFechaActualizar));
        form.add(Estilo.crearGrupo("Método de Pago", txtMetodopagoActualizar));
        form.add(Estilo.crearGrupo("Descripción", txtDescripcionActualizar));
        form.add(Estilo.crearGrupo("No. Factura", txtNumerofacturaActualizar));
        form.add(Estilo.crearGrupo("Observaciones", txtObservacionesActualizar));

        JLabel pista = new JLabel("Ingresa el ID de la transacción y presiona Cargar Datos para validar su existencia.");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Tema.textoSecundario());
        pista.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JPanel superior = new JPanel(new BorderLayout(0, 8));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda("ID Transacción", txtIdtransaccionActualizar, 
                Estilo.botonSecundario("Cargar datos", this::cargarParaActualizar)), BorderLayout.NORTH);
        superior.add(pista, BorderLayout.CENTER);

        JPanel contenedorSuperior = new JPanel(new BorderLayout(0, 18));
        contenedorSuperior.setOpaque(false);
        contenedorSuperior.add(superior, BorderLayout.NORTH);
        contenedorSuperior.add(form, BorderLayout.CENTER);

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar Cambios", this::actualizar),
                Estilo.botonSecundario("Limpiar", this::limpiarActualizar));

        JScrollPane scroll = Estilo.crearScroll(crearFormulario(contenedorSuperior, botones));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private void cargarParaActualizar() {
        String id = txtIdtransaccionActualizar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la transacción a actualizar.");
            return;
        }

        String resultado = crud.consultarTransaccion(id);
        if (resultado == null || resultado.isEmpty()) {
            Estilo.mostrarAviso(this, "No se encontró la transacción con ID: " + id);
            return;
        }

        try {
            String[] lineas = resultado.split("\n");
            String tipo = lineas[1].substring("Tipo: ".length());
            String monto = lineas[2].substring("Monto: L. ".length());
            String fechaCompleta = lineas[3].substring("Fecha: ".length());
            String fecha = fechaCompleta.split(" ")[0]; // AAAA-MM-DD
            String idSubcategoria = lineas[6].substring("ID Subcategoria: ".length());
            String metodoPago = lineas[7].substring("Metodo Pago: ".length());
            String descripcion = lineas[8].substring("Descripcion: ".length());
            String factura = lineas[9].substring("Factura: ".length());
            String observaciones = lineas[10].substring("Observaciones: ".length());

            txtTipotransaccionActualizar.setText(tipo);
            txtMontoActualizar.setText(monto);
            txtFechaActualizar.setText(fecha);
            txtIdsubcategoriaActualizar.setText(idSubcategoria);
            txtMetodopagoActualizar.setText(metodoPago);
            txtDescripcionActualizar.setText(descripcion);
            txtNumerofacturaActualizar.setText(factura);
            txtObservacionesActualizar.setText(observaciones);
        } catch (Exception e) {
            Estilo.mostrarError(this, "Error al procesar los datos.");
        }
        
        txtIdsubcategoriaActualizar.requestFocusInWindow();
    }

    private void actualizar() {
        try {
            if (txtIdtransaccionActualizar.getText().trim().isEmpty()) {
                Estilo.mostrarAviso(this, "Ingresa el ID de la transacción a actualizar.");
                return;
            }

            boolean res = crud.actualizarTransaccion(
                txtIdtransaccionActualizar.getText().trim(),
                txtIdsubcategoriaActualizar.getText().trim(),
                txtTipotransaccionActualizar.getText().trim(),
                txtDescripcionActualizar.getText().trim(),
                Double.parseDouble(txtMontoActualizar.getText().trim()),
                txtFechaActualizar.getText().trim(),
                txtMetodopagoActualizar.getText().trim(),
                txtNumerofacturaActualizar.getText().trim(),
                txtObservacionesActualizar.getText().trim(),
                usuarioActual()
            );

            if (res) {
                Estilo.mostrarInfo(this, "Transacción actualizada correctamente.");
                limpiarActualizar();
            } else {
                Estilo.mostrarError(this, "No se pudo actualizar el registro.");
            }
        } catch(Exception e) {
            Estilo.mostrarError(this, "Error de formato: " + e.getMessage());
        }
    }

    private void limpiarActualizar() {
        txtIdtransaccionActualizar.setText("");
        txtIdsubcategoriaActualizar.setText("");
        txtTipotransaccionActualizar.setText("");
        txtDescripcionActualizar.setText("");
        txtMontoActualizar.setText("");
        txtFechaActualizar.setText("");
        txtMetodopagoActualizar.setText("");
        txtNumerofacturaActualizar.setText("");
        txtObservacionesActualizar.setText("");
    }

    private JPanel crearTabEliminar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdEliminar = Estilo.crearCampo();
        txtIdEliminar.setPreferredSize(new Dimension(240, 40));
        txtIdEliminar.addActionListener(e -> eliminar());

        panel.add(crearFilaBusqueda("ID Transacción", txtIdEliminar, Estilo.botonPeligro("Eliminar transacción", this::eliminar)), BorderLayout.NORTH);
        
        JLabel aviso = new JLabel("Escribe el ID del registro que quieres eliminar de la base de datos.");
        aviso.setFont(Estilo.fuente(Font.PLAIN, 13));
        aviso.setForeground(Tema.textoSecundario());
        aviso.setVerticalAlignment(SwingConstants.TOP);
        panel.add(aviso, BorderLayout.CENTER);

        return panel;
    }

    private void eliminar() {
        String id = txtIdEliminar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID de la transacción que quieres eliminar.");
            return;
        }

        boolean confirmado = Estilo.confirmar(this, "¿Seguro que quieres eliminar la transacción " + id + "?", "Confirmar eliminación", "Sí, eliminar");
        if (!confirmado) return;

        boolean res = crud.eliminarTransaccion(id);
        if (res) {
            Estilo.mostrarInfo(this, "Transacción eliminada correctamente.");
            txtIdEliminar.setText("");
        } else {
            Estilo.mostrarError(this, "No se pudo eliminar el registro.");
        }
    }

    private String usuarioActual() {
        return ventanaPrincipal.getNombreCuenta();
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
            if (i > 0) panel.add(Box.createHorizontalStrut(12));
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

    private void limpiarTodo() {
        txtIdConsultar.setText("");
        areaConsultar.setText("");
        txtFiltroPresupuesto.setText("");
        if(modeloListar != null) modeloListar.setRowCount(0);
        limpiarInsertar();
        limpiarActualizar();
        txtIdEliminar.setText("");
        seleccionarTab(TAB_CONSULTAR);
    }
}