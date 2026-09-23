
package dev.presupuesto.frontend.utils;
 
import java.awt.Color;
import java.util.prefs.Preferences;

public final class Tema {
 
    private static final class Paleta {
        final Color fondoPrincipal;
        final Color fondoTarjeta;
        final Color textoPrimario;
        final Color textoSecundario;
        final Color borde;
        final Color boton;
        final Color botonHover;
        final Color acentoSuave;
        final Color input;
        final Color bordeInput;
        final Color peligro;
        final Color peligroHover;
        final Color fondoConsola;
        final Color textoConsola;
 
        Paleta(Color fondoPrincipal, Color fondoTarjeta, Color textoPrimario, Color textoSecundario,
               Color borde, Color boton, Color botonHover, Color acentoSuave,
               Color input, Color bordeInput, Color peligro, Color peligroHover,
               Color fondoConsola, Color textoConsola) {
            this.fondoPrincipal = fondoPrincipal;
            this.fondoTarjeta = fondoTarjeta;
            this.textoPrimario = textoPrimario;
            this.textoSecundario = textoSecundario;
            this.borde = borde;
            this.boton = boton;
            this.botonHover = botonHover;
            this.acentoSuave = acentoSuave;
            this.input = input;
            this.bordeInput = bordeInput;
            this.peligro = peligro;
            this.peligroHover = peligroHover;
            this.fondoConsola = fondoConsola;
            this.textoConsola = textoConsola;
        }
    }
 
    private static final Paleta CLARO = new Paleta(
            new Color(235, 238, 242),
            new Color(255, 255, 255),
            new Color(40, 40, 40),
            new Color(120, 120, 120),
            new Color(220, 225, 230),
            new Color(0, 110, 255),
            new Color(0, 90, 215),
            new Color(230, 240, 255),
            new Color(248, 250, 252),
            new Color(210, 215, 220),
            new Color(214, 48, 49),
            new Color(184, 36, 37),
            new Color(245, 247, 250),
            new Color(0, 0, 0)
    );
 
    private static final Paleta OSCURO = new Paleta(
            new Color(24, 26, 31),
            new Color(36, 39, 46),
            new Color(235, 237, 240),
            new Color(150, 156, 166),
            new Color(58, 62, 72),
            new Color(64, 145, 255),
            new Color(110, 170, 255),
            new Color(38, 58, 92),
            new Color(28, 30, 36),
            new Color(75, 80, 92),
            new Color(235, 77, 75),
            new Color(205, 57, 55),
            new Color(45, 49, 58),
            new Color(255, 255, 255)
    );
 
    private static final String CLAVE_OSCURO = "modoOscuro";
    private static final Preferences PREFS = Preferences.userNodeForPackage(Tema.class);
 
    private static boolean oscuro = PREFS.getBoolean(CLAVE_OSCURO, false);
 
    private Tema() {
    }
 
    public static boolean esOscuro() {
        return oscuro;
    }
 
    public static void setOscuro(boolean valor) {
        oscuro = valor;
        PREFS.putBoolean(CLAVE_OSCURO, valor);
    }
 
    private static Paleta actual() {
        return oscuro ? OSCURO : CLARO;
    }
 
    public static Color fondoPrincipal() { return actual().fondoPrincipal; }
    public static Color fondoTarjeta() { return actual().fondoTarjeta; }
    public static Color textoPrimario() { return actual().textoPrimario; }
    public static Color textoSecundario() { return actual().textoSecundario; }
    public static Color borde() { return actual().borde; }
    public static Color boton() { return actual().boton; }
    public static Color botonHover() { return actual().botonHover; }
    public static Color acentoSuave() { return actual().acentoSuave; }
    public static Color input() { return actual().input; }
    public static Color bordeInput() { return actual().bordeInput; }
    public static Color peligro() { return actual().peligro; }
    public static Color peligroHover() { return actual().peligroHover; }
    public static Color fondoConsola() { return actual().fondoConsola; }
    public static Color textoConsola() { return actual().textoConsola; }
}
