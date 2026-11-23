package interfaz;

import java.io.IOException;

import negocio.AlmacenDatos;

/** Punto de entrada de la aplicacion de consola. */
public class Main {
    private static final String ARCHIVO_DATOS = "fittrack.dat";

    public static void main(String[] args) {
        Consola consola = new Consola();
        AlmacenDatos datos;
        try {
            datos = AlmacenDatos.cargar(ARCHIVO_DATOS);
        } catch (IOException | ClassNotFoundException e) {
            consola.error("No se pudo leer el archivo de datos (" + e.getMessage() + "). Se inicia vacio.");
            datos = new AlmacenDatos();
        }
        new MenuPrincipal(consola, datos, ARCHIVO_DATOS).iniciar();
    }
}
