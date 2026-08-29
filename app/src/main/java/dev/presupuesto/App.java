package dev.presupuesto;

import dev.presupuesto.frontend.frames.loginFrame;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new loginFrame());
    }
}
