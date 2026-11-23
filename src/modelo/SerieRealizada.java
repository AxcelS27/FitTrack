package modelo;

import java.io.Serializable;

/** Una serie ejecutada durante una sesion: ejercicio, repeticiones y peso usado. */
public class SerieRealizada implements Serializable {
    private static final long serialVersionUID = 1L;

    private final EjercicioFuerza ejercicio;
    private final int repeticiones;
    private final double pesoKg;

    public SerieRealizada(EjercicioFuerza ejercicio, int repeticiones, double pesoKg) {
        if (ejercicio == null) {
            throw new IllegalArgumentException("La serie necesita un ejercicio.");
        }
        if (repeticiones < 1 || repeticiones > 100) {
            throw new IllegalArgumentException("Las repeticiones deben estar entre 1 y 100.");
        }
        if (pesoKg < 0 || pesoKg > 500) {
            throw new IllegalArgumentException("El peso debe estar entre 0 y 500 kg.");
        }
        this.ejercicio = ejercicio;
        this.repeticiones = repeticiones;
        this.pesoKg = pesoKg;
    }

    public EjercicioFuerza getEjercicio() {
        return ejercicio;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    /** Volumen de la serie = repeticiones x peso. */
    public double calcularVolumen() {
        return repeticiones * pesoKg;
    }
}
