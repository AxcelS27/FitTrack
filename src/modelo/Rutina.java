package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Rutina de entrenamiento: agrupa ejercicios y define el esquema de series y repeticiones. */
public class Rutina implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int seriesObjetivo;
    private final int repsMin;
    private final int repsMax;
    private final List<Ejercicio> ejercicios = new ArrayList<>();

    public Rutina(String nombre, int seriesObjetivo, int repsMin, int repsMax) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la rutina es obligatorio.");
        }
        if (seriesObjetivo < 1 || seriesObjetivo > 10) {
            throw new IllegalArgumentException("Las series objetivo deben estar entre 1 y 10.");
        }
        if (repsMin < 1 || repsMin > 30) {
            throw new IllegalArgumentException("Las repeticiones minimas deben estar entre 1 y 30.");
        }
        if (repsMax < repsMin || repsMax > 50) {
            throw new IllegalArgumentException(
                    "Las repeticiones maximas deben ser >= a las minimas y no pasar de 50.");
        }
        this.nombre = nombre.trim();
        this.seriesObjetivo = seriesObjetivo;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSeriesObjetivo() {
        return seriesObjetivo;
    }

    public int getRepsMin() {
        return repsMin;
    }

    public int getRepsMax() {
        return repsMax;
    }

    public List<Ejercicio> getEjercicios() {
        return Collections.unmodifiableList(ejercicios);
    }

    public boolean contiene(Ejercicio ejercicio) {
        return ejercicios.contains(ejercicio);
    }

    public void agregarEjercicio(Ejercicio ejercicio) {
        if (ejercicio == null) {
            throw new IllegalArgumentException("Debes seleccionar un ejercicio.");
        }
        if (contiene(ejercicio)) {
            throw new IllegalArgumentException(
                    "El ejercicio '" + ejercicio.getNombre() + "' ya esta en la rutina.");
        }
        ejercicios.add(ejercicio);
    }

    @Override
    public String toString() {
        return nombre + " (" + seriesObjetivo + "x" + repsMin + "-" + repsMax
                + ", " + ejercicios.size() + " ejercicios)";
    }
}
