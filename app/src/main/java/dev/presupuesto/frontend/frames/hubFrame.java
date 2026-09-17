package dev.presupuesto.frontend.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class hubFrame extends JFrame {
    
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

        setContentPane(panelContenedor);
        setVisible(true);
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(235, 238, 242));

        JLabel labelBienvenida = new JLabel("¡Bienvenido, " + nombreCuenta + "!");
        labelBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
        labelBienvenida.setForeground(new Color(40, 40, 40));
        labelBienvenida.setBounds(40, 40, 600, 40);
        panel.add(labelBienvenida);

        JButton btnUsuarios = new JButton("Opciones de Usuario");
        btnUsuarios.setBounds(40, 110, 250, 45);
        btnUsuarios.setBackground(new Color(0, 110, 255));
        btnUsuarios.setForeground(Color.WHITE);
        btnUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        btnUsuarios.setOpaque(true);
        btnUsuarios.setContentAreaFilled(true);
        btnUsuarios.setFocusPainted(false);
        btnUsuarios.setBorderPainted(false);
        btnUsuarios.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnUsuarios.setBackground(new Color(0, 90, 215));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnUsuarios.setBackground(new Color(0, 110, 255));
            }
        });
        
        btnUsuarios.addActionListener(e -> mostrarPanel("Usuario"));
        panel.add(btnUsuarios);

        return panel;
    }

    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(panelContenedor, nombrePanel);
    }
}