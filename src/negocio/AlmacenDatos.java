package negocio;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.Alimento;
import modelo.Ejercicio;
import modelo.EjercicioCardio;
import modelo.EjercicioFuerza;
import modelo.GrupoMuscular;
import modelo.Usuario;

/**
 * Contenedor de todos los datos de la aplicacion (usuario, catalogo de alimentos y de ejercicios).
 * Se guarda y se carga completo en un archivo mediante serializacion.
 */
public class AlmacenDatos implements Serializable {
    private static final long serialVersionUID = 1L;

    private Usuario usuario;
    private final List<Alimento> alimentos = new ArrayList<>();
    private final List<Ejercicio> ejercicios = new ArrayList<>();

    public AlmacenDatos() {
        sembrarAlimentos();
        sembrarEjercicios();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean hayUsuario() {
        return usuario != null;
    }

    /** Devuelve el usuario o lanza IllegalStateException si aun no se registra el perfil. */
    public Usuario exigirUsuario() {
        if (usuario == null) {
            throw new IllegalStateException("Primero debes registrar tu perfil (menu Perfil y metas).");
        }
        return usuario;
    }

    public List<Alimento> getAlimentos() {
        return alimentos;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    // ---------- Persistencia ----------

    public void guardar(String ruta) throws IOException {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta))) {
            salida.writeObject(this);
        }
    }

    /** Carga los datos guardados; si el archivo no existe devuelve un almacen nuevo. */
    public static AlmacenDatos cargar(String ruta) throws IOException, ClassNotFoundException {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return new AlmacenDatos();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            return (AlmacenDatos) entrada.readObject();
        }
    }

    // ---------- Datos iniciales (valores referenciales por 100 g) ----------

    private void sembrarAlimentos() {
        Collections.addAll(alimentos,
                new Alimento("Arroz blanco cocido", 130, 2.7, 28, 0.3),
                new Alimento("Pechuga de pollo cocida", 165, 31, 0, 3.6),
                new Alimento("Huevo entero", 143, 12.6, 0.7, 9.5),
                new Alimento("Atun en agua", 116, 26, 0, 1),
                new Alimento("Avena en hojuelas", 389, 16.9, 66.3, 6.9),
                new Alimento("Platano maduro", 89, 1.1, 22.8, 0.3),
                new Alimento("Platano verde cocido", 122, 1.3, 31.9, 0.4),
                new Alimento("Lenteja cocida", 116, 9, 20, 0.4),
                new Alimento("Papa cocida", 87, 1.9, 20, 0.1),
                new Alimento("Aguacate", 160, 2, 8.5, 14.7),
                new Alimento("Leche entera", 61, 3.2, 4.8, 3.3),
                new Alimento("Carne molida de res cocida", 217, 26, 0, 11.8),
                new Alimento("Pan blanco", 265, 9, 49, 3.2),
                new Alimento("Yogur griego natural", 59, 10, 3.6, 0.4),
                new Alimento("Queso fresco", 260, 18, 3, 20));
    }

    private void sembrarEjercicios() {
        Collections.addAll(ejercicios,
                new EjercicioFuerza("Press de banca", GrupoMuscular.PECHO, 20),
                new EjercicioFuerza("Press inclinado con mancuernas", GrupoMuscular.PECHO, 10),
                new EjercicioFuerza("Sentadilla", GrupoMuscular.PIERNA, 20),
                new EjercicioFuerza("Peso muerto rumano", GrupoMuscular.PIERNA, 20),
                new EjercicioFuerza("Prensa de piernas", GrupoMuscular.PIERNA, 40),
                new EjercicioFuerza("Remo con barra", GrupoMuscular.ESPALDA, 20),
                new EjercicioFuerza("Jalon al pecho", GrupoMuscular.ESPALDA, 30),
                new EjercicioFuerza("Press militar", GrupoMuscular.HOMBRO, 15),
                new EjercicioFuerza("Elevaciones laterales", GrupoMuscular.HOMBRO, 5),
                new EjercicioFuerza("Curl de biceps", GrupoMuscular.BRAZO, 10),
                new EjercicioFuerza("Extension de triceps", GrupoMuscular.BRAZO, 10),
                new EjercicioCardio("Caminata", 3.5),
                new EjercicioCardio("Trote", 7.0),
                new EjercicioCardio("Bicicleta", 6.8));
    }
}
