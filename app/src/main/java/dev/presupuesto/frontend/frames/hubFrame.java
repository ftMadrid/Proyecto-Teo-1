package dev.presupuesto.frontend.frames;

import dev.presupuesto.conexiones.ConexionDB;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class hubFrame extends JFrame {
    
    public hubFrame(String nombreCuenta) {
        setTitle("Presupuesto Personal");
        setSize(400, 300); // Le di un poquito mas de alto para que respire el boton
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel labelBienvenida = new JLabel("Bienvenido \"" + nombreCuenta + "\"", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        labelBienvenida.setForeground(new Color(0, 102, 204)); 
        add(labelBienvenida, BorderLayout.CENTER);

        JButton btnVerUsuario = new JButton("Ver mis datos (usr_01)");
        
        btnVerUsuario.addActionListener(e -> {
            String idBuscado = "usr_01";
            String query = "SELECT * FROM usuario WHERE id_usuario = ?";

            try (Connection conn = ConexionDB.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, idBuscado);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String nombres = rs.getString("nombres");
                    String apellidos = rs.getString("apellidos");
                    String correo = rs.getString("correo");
                    java.sql.Date fecha = rs.getDate("fecha_registro");
                    double salario = rs.getDouble("salario_base");
                    String estado = rs.getString("estado");
                    
                    String mensaje = "ID: " + idBuscado + "\n"
                                   + "Nombre: " + nombres + " " + apellidos + "\n"
                                   + "Correo: " + correo + "\n"
                                   + "Fecha Registro: " + fecha + "\n"
                                   + "Salario Base: L. " + salario + "\n"
                                   + "Estado: " + estado;
                    
                    JOptionPane.showMessageDialog(this, mensaje, "Datos del Usuario", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el usuario: " + idBuscado, "Aviso", JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(btnVerUsuario, BorderLayout.SOUTH);

        setVisible(true);
    }
}
