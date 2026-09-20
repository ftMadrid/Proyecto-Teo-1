package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudUsuario;
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

public class usuarioFrame extends JPanel {

    private static final String TAB_CONSULTAR = "Consultar";
    private static final String TAB_LISTAR = "Listar";
    private static final String TAB_INSERTAR = "Insertar";
    private static final String TAB_ACTUALIZAR = "Actualizar";
    private static final String TAB_ELIMINAR = "Eliminar";

    private CrudUsuario crud = new CrudUsuario();
    private hubFrame ventanaPrincipal;

    private CardLayout cardTabs;
    private JPanel panelTabs;
    private final Map<String, JButton> botonesTab = new LinkedHashMap<>();
    private String tabActiva = TAB_CONSULTAR;

    private JTextField txtIdConsultar;
    private JTextArea areaConsultar;

    private DefaultTableModel modeloListar;
    private JTable tablaListar;

    private JTextField txtIdInsertar, txtNombresInsertar, txtApellidosInsertar, txtCorreoInsertar, txtSalarioInsertar;

    private JTextField txtIdActualizar, txtNombresActualizar, txtApellidosActualizar, txtCorreoActualizar, txtSalarioActualizar;

    private JTextField txtIdEliminar;

    public usuarioFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                "Usuarios",
                "Consulta, registra y administra las cuentas",
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

        panel.add(crearFilaBusqueda(txtIdConsultar, Estilo.botonPrimario("Buscar", this::consultar)), BorderLayout.NORTH);

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
            Estilo.mostrarAviso(this, "Ingresa el ID del usuario que quieres buscar.");
            return;
        }

        String resultado = crud.consultarUsuario(id);
        if (resultado != null && !resultado.isEmpty()) {
            areaConsultar.setText("--------------------------\nBusqueda Completada\n--------------------------\n\n" + resultado);
        } else {
            areaConsultar.setText("[!] No se encontro el usuario con ID: " + id);
        }
    }


    private JPanel crearTabListar() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        barra.add(Estilo.botonPrimario("Cargar todos los usuarios", this::cargarLista));

        JLabel pista = new JLabel("Doble clic en una fila para editar ese usuario");
        pista.setFont(Estilo.fuente(Font.PLAIN, 12));
        pista.setForeground(Tema.textoSecundario());
        pista.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        barra.add(pista);

        String[] columnas = {"ID", "Nombres", "Apellidos", "Correo", "Registro", "Salario", "Estado"};
        modeloListar = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaListar = new JTable(modeloListar);
        Estilo.estilizarTabla(tablaListar);

        int[] anchos = {65, 90, 90, 160, 92, 80, 75};
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
        modeloListar.setRowCount(0);
        ArrayList<String> lista = crud.listarUsuarios();

        if (lista != null && !lista.isEmpty()) {
            for (String fila : lista) {
                String[] datos = fila.split(",", -1);
                if (datos.length >= 7) {
                    modeloListar.addRow(new Object[]{
                            datos[0], datos[1], datos[2], datos[3], datos[4], formatearSalario(datos[5]), datos[6]
                    });
                } else {
                    modeloListar.addRow(new Object[]{fila, "", "", "", "", "", ""});
                }
            }
        } else {
            modeloListar.addRow(new Object[]{"Sin datos", "No hay usuarios registrados.", "", "", "", "", ""});
        }
    }

    private void editarDesdeTabla(int fila) {
        if (valorTabla(fila, 6).isEmpty()) {
            return;
        }

        txtIdActualizar.setText(valorTabla(fila, 0));
        txtNombresActualizar.setText(valorTabla(fila, 1));
        txtApellidosActualizar.setText(valorTabla(fila, 2));
        txtCorreoActualizar.setText(valorTabla(fila, 3));
        txtSalarioActualizar.setText(salarioPlano(valorTabla(fila, 5)));

        seleccionarTab(TAB_ACTUALIZAR);
        txtNombresActualizar.requestFocusInWindow();
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloListar.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }


    private JPanel crearTabInsertar() {
        txtIdInsertar = Estilo.crearCampo();
        txtNombresInsertar = Estilo.crearCampo();
        txtApellidosInsertar = Estilo.crearCampo();
        txtCorreoInsertar = Estilo.crearCampo();
        txtSalarioInsertar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(3, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("ID de usuario", txtIdInsertar));
        form.add(Estilo.crearGrupo("Correo", txtCorreoInsertar));
        form.add(Estilo.crearGrupo("Nombres", txtNombresInsertar));
        form.add(Estilo.crearGrupo("Apellidos", txtApellidosInsertar));
        form.add(Estilo.crearGrupo("Salario base (L.)", txtSalarioInsertar));

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar usuario", this::insertar),
                Estilo.botonSecundario("Limpiar", this::limpiarInsertar));

        return crearFormulario(form, botones);
    }

    private void insertar() {
        String id = txtIdInsertar.getText().trim();
        String nombres = txtNombresInsertar.getText().trim();
        String apellidos = txtApellidosInsertar.getText().trim();
        String correo = txtCorreoInsertar.getText().trim();
        String salario = txtSalarioInsertar.getText().trim();

        String error = validarDatos(id, nombres, apellidos, correo, salario);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        if (buscarFilaPorId(id) != null) {
            Estilo.mostrarAviso(this, "Ya existe un usuario con el ID: " + id);
            return;
        }

        boolean ok = crud.insertarUsuario(id, nombres, apellidos, correo, Double.parseDouble(salario), usuarioActual());
        if (ok) {
            Estilo.mostrarInfo(this, "Usuario registrado correctamente.");
            limpiarInsertar();
            cargarLista();
        } else {
            Estilo.mostrarError(this, "No se pudo registrar el usuario. Revisa la consola para ver el detalle.");
        }
    }

    private void limpiarInsertar() {
        txtIdInsertar.setText("");
        txtNombresInsertar.setText("");
        txtApellidosInsertar.setText("");
        txtCorreoInsertar.setText("");
        txtSalarioInsertar.setText("");
    }


    private JPanel crearTabActualizar() {
        txtIdActualizar = Estilo.crearCampo();
        txtIdActualizar.setPreferredSize(new Dimension(240, 40));
        txtIdActualizar.addActionListener(e -> cargarParaActualizar());

        txtNombresActualizar = Estilo.crearCampo();
        txtApellidosActualizar = Estilo.crearCampo();
        txtCorreoActualizar = Estilo.crearCampo();
        txtSalarioActualizar = Estilo.crearCampo();

        JPanel form = new JPanel(new GridLayout(2, 2, 20, 14));
        form.setOpaque(false);
        form.add(Estilo.crearGrupo("Nombres", txtNombresActualizar));
        form.add(Estilo.crearGrupo("Apellidos", txtApellidosActualizar));
        form.add(Estilo.crearGrupo("Correo", txtCorreoActualizar));
        form.add(Estilo.crearGrupo("Salario base (L.)", txtSalarioActualizar));

        JPanel superior = new JPanel(new BorderLayout(0, 18));
        superior.setOpaque(false);
        superior.add(crearFilaBusqueda(txtIdActualizar, Estilo.botonSecundario("Cargar datos", this::cargarParaActualizar)),
                BorderLayout.NORTH);
        superior.add(form, BorderLayout.CENTER);

        JPanel botones = crearBotonera(
                Estilo.botonPrimario("Guardar cambios", this::actualizar),
                Estilo.botonSecundario("Limpiar", this::limpiarActualizar));

        return crearFormulario(superior, botones);
    }

    private void cargarParaActualizar() {
        String id = txtIdActualizar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del usuario que quieres actualizar.");
            return;
        }

        String[] fila = buscarFilaPorId(id);
        if (fila == null) {
            Estilo.mostrarAviso(this, "No se encontro el usuario con ID: " + id);
            return;
        }

        txtIdActualizar.setText(fila[0].trim());
        txtNombresActualizar.setText(fila[1]);
        txtApellidosActualizar.setText(fila[2]);
        txtCorreoActualizar.setText(fila[3]);
        txtSalarioActualizar.setText(salarioPlano(fila[5]));
        txtNombresActualizar.requestFocusInWindow();
    }

    private void actualizar() {
        String id = txtIdActualizar.getText().trim();
        String nombres = txtNombresActualizar.getText().trim();
        String apellidos = txtApellidosActualizar.getText().trim();
        String correo = txtCorreoActualizar.getText().trim();
        String salario = txtSalarioActualizar.getText().trim();

        String error = validarDatos(id, nombres, apellidos, correo, salario);
        if (error != null) {
            Estilo.mostrarAviso(this, error);
            return;
        }

        if (buscarFilaPorId(id) == null) {
            Estilo.mostrarAviso(this, "No existe un usuario con el ID: " + id);
            return;
        }

        boolean ok = crud.actualizarUsuario(id, nombres, apellidos, correo, Double.parseDouble(salario), usuarioActual());
        if (ok) {
            Estilo.mostrarInfo(this, "Usuario actualizado correctamente.");
            limpiarActualizar();
            cargarLista();
        } else {
            Estilo.mostrarError(this, "No se pudo actualizar el usuario. Revisa la consola para ver el detalle.");
        }
    }

    private void limpiarActualizar() {
        txtIdActualizar.setText("");
        txtNombresActualizar.setText("");
        txtApellidosActualizar.setText("");
        txtCorreoActualizar.setText("");
        txtSalarioActualizar.setText("");
    }


    private JPanel crearTabEliminar() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        txtIdEliminar = Estilo.crearCampo();
        txtIdEliminar.setPreferredSize(new Dimension(240, 40));
        txtIdEliminar.addActionListener(e -> eliminar());

        panel.add(crearFilaBusqueda(txtIdEliminar, Estilo.botonPeligro("Eliminar usuario", this::eliminar)),
                BorderLayout.NORTH);

        JLabel aviso = new JLabel("Escribe el ID del usuario que quieres eliminar. Antes de continuar se te pedirá confirmación.");
        aviso.setFont(Estilo.fuente(Font.PLAIN, 13));
        aviso.setForeground(Tema.textoSecundario());
        aviso.setVerticalAlignment(SwingConstants.TOP);
        panel.add(aviso, BorderLayout.CENTER);

        return panel;
    }

    private void eliminar() {
        String id = txtIdEliminar.getText().trim();
        if (id.isEmpty()) {
            Estilo.mostrarAviso(this, "Ingresa el ID del usuario que quieres eliminar.");
            return;
        }

        String[] fila = buscarFilaPorId(id);
        if (fila == null) {
            Estilo.mostrarAviso(this, "No se encontro el usuario con ID: " + id);
            return;
        }

        String nombreCompleto = (fila[1] + " " + fila[2]).trim();
        boolean confirmado = Estilo.confirmar(this,
                "¿Seguro que quieres eliminar a " + nombreCompleto + " (ID: " + fila[0].trim() + ")?",
                "Confirmar eliminación",
                "Sí, eliminar");
        if (!confirmado) {
            return;
        }

        boolean ok = crud.eliminarUsuario(id, usuarioActual());
        if (ok) {
            Estilo.mostrarInfo(this, "Usuario eliminado correctamente.");
            txtIdEliminar.setText("");
            cargarLista();
        } else {
            Estilo.mostrarError(this, "No se pudo eliminar el usuario. Revisa la consola para ver el detalle.");
        }
    }


    private String usuarioActual() {
        return ventanaPrincipal.getNombreCuenta();
    }

    private String[] buscarFilaPorId(String id) {
        ArrayList<String> lista = crud.listarUsuarios();
        if (lista == null) {
            return null;
        }
        for (String fila : lista) {
            String[] datos = fila.split(",", -1);
            if (datos.length >= 7 && datos[0].trim().equalsIgnoreCase(id.trim())) {
                return datos;
            }
        }
        return null;
    }

    private String validarDatos(String id, String nombres, String apellidos, String correo, String salario) {
        if (id.isEmpty()) {
            return "El ID de usuario es obligatorio.";
        }
        if (nombres.isEmpty()) {
            return "Los nombres son obligatorios.";
        }
        if (apellidos.isEmpty()) {
            return "Los apellidos son obligatorios.";
        }
        if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            return "El correo no tiene un formato válido (ejemplo: nombre@correo.com).";
        }
        if (!salario.matches("\\d+(\\.\\d{1,2})?")) {
            return "El salario debe ser un número positivo, por ejemplo: 15000 o 15000.50";
        }
        if (id.contains(",") || nombres.contains(",") || apellidos.contains(",") || correo.contains(",")) {
            return "No uses comas en el ID, nombres, apellidos ni correo.";
        }
        return null;
    }

    private String formatearSalario(String bruto) {
        try {
            return String.format(Locale.US, "%,.2f", Double.parseDouble(bruto.trim()));
        } catch (NumberFormatException e) {
            return bruto;
        }
    }

    private String salarioPlano(String texto) {
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
        modeloListar.setRowCount(0);
        seleccionarTab(TAB_CONSULTAR);
    }

    private JPanel crearFilaBusqueda(JTextField campo, JButton boton) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.add(campo, BorderLayout.CENTER);
        fila.add(boton, BorderLayout.EAST);

        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenedor.setOpaque(false);
        contenedor.add(Estilo.crearGrupo("ID de usuario", fila));
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