package dev.presupuesto.frontend.frames;

import javax.swing.*;
import java.awt.*;

public class hubFrame extends JFrame {
    
    public hubFrame(String nombreCuenta) {
        setTitle("Presupuesto Personal");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel labelBienvenida = new JLabel("Bienvenido \"" + nombreCuenta + "\"", SwingConstants.CENTER);
        labelBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        labelBienvenida.setForeground(new Color(0, 102, 204)); 

        add(labelBienvenida, BorderLayout.CENTER);

        setVisible(true);
    }
}
