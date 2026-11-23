package interfaz;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import negocio.AlmacenDatos;
import negocio.GestorEntrenamiento;
import negocio.GestorNutricion;
import negocio.GestorProgreso;
import negocio.GestorUsuario;

/** Ventana principal con una pestana por modulo. Guarda los datos al cambiar de pestana y al cerrar. */
public class VentanaPrincipal extends JFrame {
    private final AlmacenDatos datos;
    private final String rutaArchivo;
    private final GestorUsuario gestorUsuario;
    private final PanelPerfil panelPerfil;
    private final PanelEntrenamiento panelEntrenamiento;
    private final PanelNutricion panelNutricion;
    private final PanelProgreso panelProgreso;
    private final JTabbedPane pestanas = new JTabbedPane();

    public VentanaPrincipal(AlmacenDatos datos, String rutaArchivo) {
        super("FitTrack - Entrenamiento y Nutricion");
        this.datos = datos;
        this.rutaArchivo = rutaArchivo;
        this.gestorUsuario = new GestorUsuario(datos);
        panelPerfil = new PanelPerfil(gestorUsuario);
        panelEntrenamiento = new PanelEntrenamiento(new GestorEntrenamiento(datos), gestorUsuario);
        panelNutricion = new PanelNutricion(new GestorNutricion(datos), gestorUsuario);
        panelProgreso = new PanelProgreso(new GestorProgreso(datos), gestorUsuario);

        pestanas.addTab("Perfil y metas", panelPerfil);
        pestanas.addTab("Entrenamiento", panelEntrenamiento);
        pestanas.addTab("Nutricion", panelNutricion);
        pestanas.addTab("Progreso corporal", panelProgreso);
        pestanas.addChangeListener(e -> alCambiarPestana());
        add(pestanas);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardar();
                dispose();
                System.exit(0);
            }
        });
        setSize(900, 650);
        setLocationRelativeTo(null);
        panelPerfil.refrescar();
    }

    private void alCambiarPestana() {
        int indice = pestanas.getSelectedIndex();
        if (indice > 0 && !datos.hayUsuario()) {
            Utilidades.error(this, "Primero registra tu perfil en la pestana 'Perfil y metas'.");
            pestanas.setSelectedIndex(0);
            return;
        }
        guardar();
        switch (indice) {
            case 0: panelPerfil.refrescar(); break;
            case 1: panelEntrenamiento.refrescar(); break;
            case 2: panelNutricion.refrescar(); break;
            case 3: panelProgreso.refrescar(); break;
            default: break;
        }
    }

    private void guardar() {
        try {
            datos.guardar(rutaArchivo);
        } catch (IOException e) {
            Utilidades.error(this, "No se pudieron guardar los datos: " + e.getMessage());
        }
    }
}
