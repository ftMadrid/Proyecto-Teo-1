package dev.presupuesto.frontend.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class loginFrame extends JFrame {

    public loginFrame() {
        setTitle("Presupuesto Personal");
        setSize(480, 540); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); 

        // --- PALETA DE COLORES (Light Theme) ---
        Color fondoPrincipal = new Color(235, 238, 242); 
        Color fondoTarjeta = new Color(255, 255, 255);   
        Color colorTextoPrimario = new Color(40, 40, 40); 
        Color colorTextoSecundario = new Color(120, 120, 120); 
        Color colorInput = new Color(248, 250, 252); 
        Color colorBorde = new Color(210, 215, 220); 
        Color colorBoton = new Color(0, 110, 255); 
        Color colorBotonHover = new Color(0, 90, 215); 

        // Panel principal
        JPanel panelFondo = new JPanel();
        panelFondo.setBackground(fondoPrincipal);
        panelFondo.setLayout(null); 
        setContentPane(panelFondo);

        // Tarjeta central
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(fondoTarjeta);
        tarjeta.setBounds(60, 45, 345, 400); 
        tarjeta.setLayout(null);
        
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230), 1));
        panelFondo.add(tarjeta);

        // --- TEXTOS ---
        JLabel titulo = new JLabel("Bienvenido");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(colorTextoPrimario);
        titulo.setBounds(40, 35, 265, 35);
        tarjeta.add(titulo);

        JLabel subtitulo = new JLabel("Inicia sesión para continuar");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(colorTextoSecundario);
        subtitulo.setBounds(40, 75, 265, 20);
        tarjeta.add(subtitulo);

        // --- CAMPOS DE TEXTO ---
        JLabel labelUsuario = new JLabel("USUARIO");
        labelUsuario.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelUsuario.setForeground(colorTextoSecundario);
        labelUsuario.setBounds(40, 125, 265, 20);
        tarjeta.add(labelUsuario);

        JTextField campoUsuario = new JTextField();
        campoUsuario.setBounds(40, 150, 265, 45); 
        campoUsuario.setBackground(colorInput);
        campoUsuario.setForeground(colorTextoPrimario);
        campoUsuario.setCaretColor(Color.BLACK); 
        campoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campoUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorBorde, 1),
            BorderFactory.createEmptyBorder(0, 15, 0, 15)
        )); 
        tarjeta.add(campoUsuario);

        JLabel labelContrasena = new JLabel("CONTRASEÑA");
        labelContrasena.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelContrasena.setForeground(colorTextoSecundario);
        labelContrasena.setBounds(40, 215, 265, 20);
        tarjeta.add(labelContrasena);

        JPasswordField campoContrasena = new JPasswordField();
        campoContrasena.setBounds(40, 240, 265, 45);
        campoContrasena.setBackground(colorInput);
        campoContrasena.setForeground(colorTextoPrimario);
        campoContrasena.setCaretColor(Color.BLACK);
        campoContrasena.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campoContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorBorde, 1),
            BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));
        tarjeta.add(campoContrasena);

        // --- BOTON ---
        JButton botonLogin = new JButton("Ingresar");
        botonLogin.setBounds(40, 320, 265, 45);
        botonLogin.setBackground(colorBoton);
        botonLogin.setForeground(Color.WHITE); 
        botonLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        // Ajustes para que el boton se vea correctamente
        botonLogin.setOpaque(true);
        botonLogin.setContentAreaFilled(true);
        botonLogin.setFocusPainted(false); 
        botonLogin.setBorderPainted(false); 
        botonLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botonLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botonLogin.setBackground(colorBotonHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botonLogin.setBackground(colorBoton);
            }
        });
        tarjeta.add(botonLogin);

        // --- ACCIoN DEL LOGIN ---
        botonLogin.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String contrasena = new String(campoContrasena.getPassword());

            if (usuario.equals("fernando_madrid") && contrasena.equals("sofia2026")) {
                dispose(); 
                new hubFrame("Fernando"); 
            } else {
                UIManager.put("Panel.background", fondoTarjeta);
                UIManager.put("OptionPane.background", fondoTarjeta);
                UIManager.put("OptionPane.messageForeground", colorTextoPrimario);
                
                JOptionPane.showMessageDialog(
                        loginFrame.this, 
                        "Usuario o contraseña incorrectos.", 
                        "Acceso Denegado", 
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        setVisible(true);
    }
}