package interfaz;

import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import negocio.AlmacenDatos;

/** Punto de entrada de la aplicacion con interfaz grafica. */
public class MainGUI {
    private static final String ARCHIVO_DATOS = "fittrack.dat";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, se usa el aspecto por defecto de Swing.
        }
        AlmacenDatos datos;
        try {
            datos = AlmacenDatos.cargar(ARCHIVO_DATOS);
        } catch (IOException | ClassNotFoundException e) {
            datos = new AlmacenDatos();
        }
        final AlmacenDatos almacen = datos;
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(almacen, ARCHIVO_DATOS).setVisible(true));
    }
}
