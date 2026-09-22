package dev.presupuesto.frontend.frames;

import dev.presupuesto.frontend.utils.Estilo;
import dev.presupuesto.frontend.utils.Tema;

import javax.swing.*;
import java.awt.*;

public class reportePendienteFrame extends JPanel {

    public reportePendienteFrame(reporteriaFrame menuReportes, String titulo, String objetivo) {
        setLayout(new BorderLayout());
        setBackground(Tema.fondoPrincipal());
        setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        add(Estilo.crearEncabezado(
                titulo,
                "Reporte pendiente de implementación",
                "←  Volver a reportería",
                menuReportes::volverAlMenu), BorderLayout.NORTH);

        add(crearTarjetaObjetivo(objetivo), BorderLayout.CENTER);
    }

    private JPanel crearTarjetaObjetivo(String objetivo) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Tema.fondoTarjeta());
        tarjeta.setBorder(BorderFactory.createLineBorder(Tema.borde(), 1));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel etiqueta = new JLabel("OBJETIVO");
        etiqueta.setFont(Estilo.fuente(Font.BOLD, 11));
        etiqueta.setForeground(Tema.textoSecundario());
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel texto = new JLabel("<html><body style='width: 440px'>" + objetivo + "</body></html>");
        texto.setFont(Estilo.fuente(Font.PLAIN, 15));
        texto.setForeground(Tema.textoPrimario());
        texto.setBorder(BorderFactory.createEmptyBorder(8, 0, 20, 0));
        texto.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel estado = new JLabel("Falta conectar este reporte con su función correspondiente en el backend.");
        estado.setFont(Estilo.fuente(Font.PLAIN, 13));
        estado.setForeground(Tema.textoSecundario());
        estado.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenido.add(etiqueta);
        contenido.add(texto);
        contenido.add(estado);

        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setOpaque(false);
        centrado.add(contenido);

        tarjeta.add(centrado, BorderLayout.CENTER);
        return tarjeta;
    }
}
