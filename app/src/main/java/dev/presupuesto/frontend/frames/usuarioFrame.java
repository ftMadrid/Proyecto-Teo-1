package dev.presupuesto.frontend.frames;

import dev.presupuesto.backend.cruds.CrudUsuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class usuarioFrame extends JPanel {
    
    private CrudUsuario crud = new CrudUsuario();
    private hubFrame ventanaPrincipal;
    
    private JTextField txtId;
    private JTextArea areaConsultar;
    private DefaultTableModel modeloListar;

    public usuarioFrame(hubFrame ventana) {
        this.ventanaPrincipal = ventana;
        setLayout(new BorderLayout());

        txtId = new JTextField(15);
        areaConsultar = new JTextArea();
        String[] columnasListar = {"ID", "Nombres", "Apellidos", "Correo", "Fecha Registro", "Salario", "Estado"};
        modeloListar = new DefaultTableModel(null, columnasListar);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(new Color(235, 238, 242));
        JButton btnVolver = new JButton("← Volver al Menú");
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVolver.setFocusPainted(false);
        
        btnVolver.addActionListener(e -> {
            txtId.setText("");
            areaConsultar.setText("");
            modeloListar.setRowCount(0);
            ventanaPrincipal.mostrarPanel("Menu");
        });
        
        panelSuperior.add(btnVolver);
        add(panelSuperior, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelConsultar = new JPanel(new BorderLayout(10, 10));
        panelConsultar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel topConsultar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topConsultar.add(new JLabel("ID de Usuario:"));
        topConsultar.add(txtId);
        JButton btnConsultar = new JButton("Buscar");
        topConsultar.add(btnConsultar);
        
        areaConsultar.setEditable(false);
        areaConsultar.setFont(new Font("Monospaced", Font.PLAIN, 16));
        areaConsultar.setMargin(new Insets(15, 15, 15, 15));
        
        panelConsultar.add(topConsultar, BorderLayout.NORTH);
        panelConsultar.add(new JScrollPane(areaConsultar), BorderLayout.CENTER);
        
        btnConsultar.addActionListener(e -> {
            String id = txtId.getText();
            String resultado = crud.consultarUsuario(id);
            if(resultado != null && !resultado.isEmpty()){
                areaConsultar.setText("--------------------------\nBusqueda Completada\n--------------------------\n\n" + resultado);
            } else {
                areaConsultar.setText("[!] No se encontro el usuario con ID: " + id);
            }
        });

        JPanel panelListar = new JPanel(new BorderLayout(10, 10));
        panelListar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btnListar = new JButton("Cargar Todos los Usuarios");
        
        JTable tablaListar = new JTable(modeloListar);
        tablaListar.setRowHeight(25);
        tablaListar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaListar.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        panelListar.add(btnListar, BorderLayout.NORTH);
        panelListar.add(new JScrollPane(tablaListar), BorderLayout.CENTER);
        
        btnListar.addActionListener(e -> {
            modeloListar.setRowCount(0);
            ArrayList<String> lista = crud.listarUsuarios();
            if(lista != null && !lista.isEmpty()){
                for(String fila : lista){
                    String[] datos = fila.split(",");
                    if(datos.length >= 7){
                        modeloListar.addRow(datos);
                    } else {
                        modeloListar.addRow(new Object[]{fila, "", "", "", "", "", ""});
                    }
                }
            } else {
                modeloListar.addRow(new Object[]{"Sin datos", "No hay usuarios registrados.", "", "", "", "", ""});
            }
        });

        tabs.addTab("Consultar Usuario", panelConsultar);
        tabs.addTab("Listar Usuarios", panelListar);
        add(tabs, BorderLayout.CENTER);
    }
}