package modelo;

import java.io.Serializable;

/** Clase base abstracta de todo ejercicio del catalogo. */
public abstract class Ejercicio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private GrupoMuscular grupo;

    protected Ejercicio(String nombre, GrupoMuscular grupo) {
        setNombre(nombre);
        setGrupo(grupo);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del ejercicio es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public GrupoMuscular getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoMuscular grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo muscular es obligatorio.");
        }
        this.grupo = grupo;
    }

    /** Calorias gastadas al realizar el ejercicio durante cierto tiempo (polimorfismo). */
    public abstract double calcularGastoCalorico(double pesoKg, int minutos);

    /** Tipo legible del ejercicio ("Fuerza" o "Cardio"). */
    public abstract String getTipo();

    @Override
    public String toString() {
        return nombre + " [" + getTipo() + " - " + grupo + "]";
    }
}
