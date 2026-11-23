package interfaz;

import java.io.IOException;
import java.util.NoSuchElementException;

import negocio.AlmacenDatos;
import negocio.GestorEntrenamiento;
import negocio.GestorNutricion;
import negocio.GestorProgreso;
import negocio.GestorUsuario;

/** Menu principal: conecta los cuatro modulos y guarda los datos al terminar cada uno. */
public class MenuPrincipal {
    private final Consola consola;
    private final AlmacenDatos datos;
    private final String rutaArchivo;
    private final GestorUsuario gestorUsuario;
    private final MenuPerfil menuPerfil;
    private final MenuEntrenamiento menuEntrenamiento;
    private final MenuNutricion menuNutricion;
    private final MenuProgreso menuProgreso;

    public MenuPrincipal(Consola consola, AlmacenDatos datos, String rutaArchivo) {
        this.consola = consola;
        this.datos = datos;
        this.rutaArchivo = rutaArchivo;
        this.gestorUsuario = new GestorUsuario(datos);
        this.menuPerfil = new MenuPerfil(consola, gestorUsuario);
        this.menuEntrenamiento = new MenuEntrenamiento(consola, new GestorEntrenamiento(datos), gestorUsuario);
        this.menuNutricion = new MenuNutricion(consola, new GestorNutricion(datos), gestorUsuario);
        this.menuProgreso = new MenuProgreso(consola, new GestorProgreso(datos));
    }

    public void iniciar() {
        consola.mostrar("====================================");
        consola.mostrar("   FITTRACK - Entrenamiento y Nutricion");
        consola.mostrar("====================================");
        try {
            int opcion;
            do {
                consola.titulo("MENU PRINCIPAL");
                consola.mostrar("1. Perfil y metas");
                consola.mostrar("2. Entrenamiento");
                consola.mostrar("3. Nutricion");
                consola.mostrar("4. Progreso corporal");
                consola.mostrar("0. Salir");
                opcion = consola.leerEntero("Opcion: ", 0, 4);
                if (opcion >= 2 && opcion <= 4 && !datos.hayUsuario()) {
                    consola.error("Primero registra tu perfil en la opcion 1.");
                    continue;
                }
                switch (opcion) {
                    case 1: menuPerfil.mostrar(); break;
                    case 2: menuEntrenamiento.mostrar(); break;
                    case 3: menuNutricion.mostrar(); break;
                    case 4: menuProgreso.mostrar(); break;
                    default: break;
                }
                guardar();
            } while (opcion != 0);
        } catch (NoSuchElementException e) {
            consola.mostrar("\nEntrada finalizada.");
            guardar();
        }
        consola.mostrar("Datos guardados. Hasta pronto!");
    }

    private void guardar() {
        try {
            datos.guardar(rutaArchivo);
        } catch (IOException e) {
            consola.error("No se pudieron guardar los datos: " + e.getMessage());
        }
    }
}
