package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Sesion de entrenamiento realizada en una fecha, basada en una rutina. */
public class SesionEntrenamiento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate fecha;
    private final Rutina rutina;
    private final int duracionMin;
    private final List<SerieRealizada> series = new ArrayList<>();

    public SesionEntrenamiento(LocalDate fecha, Rutina rutina, int duracionMin) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (rutina == null) {
            throw new IllegalArgumentException("Debes elegir una rutina.");
        }
        if (duracionMin < 5 || duracionMin > 300) {
            throw new IllegalArgumentException("La duracion debe estar entre 5 y 300 minutos.");
        }
        this.fecha = fecha;
        this.rutina = rutina;
        this.duracionMin = duracionMin;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Rutina getRutina() {
        return rutina;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public List<SerieRealizada> getSeries() {
        return Collections.unmodifiableList(series);
    }

    public void agregarSerie(SerieRealizada serie) {
        if (serie == null) {
            throw new IllegalArgumentException("La serie no puede ser nula.");
        }
        series.add(serie);
    }

    /** Series de la sesion que corresponden a un ejercicio concreto. */
    public List<SerieRealizada> getSeriesDe(EjercicioFuerza ejercicio) {
        List<SerieRealizada> resultado = new ArrayList<>();
        for (SerieRealizada s : series) {
            if (s.getEjercicio() == ejercicio) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    public double calcularVolumenDe(EjercicioFuerza ejercicio) {
        double total = 0;
        for (SerieRealizada s : getSeriesDe(ejercicio)) {
            total += s.calcularVolumen();
        }
        return total;
    }

    public double calcularVolumenTotal() {
        double total = 0;
        for (SerieRealizada s : series) {
            total += s.calcularVolumen();
        }
        return total;
    }

    /** Reparte el tiempo en partes iguales entre los ejercicios de la rutina y suma su gasto. */
    public double calcularGastoCalorico(double pesoUsuarioKg) {
        List<Ejercicio> lista = rutina.getEjercicios();
        if (lista.isEmpty()) {
            return 0;
        }
        int minutosPorEjercicio = Math.max(1, duracionMin / lista.size());
        double total = 0;
        for (Ejercicio e : lista) {
            total += e.calcularGastoCalorico(pesoUsuarioKg, minutosPorEjercicio);
        }
        return total;
    }
}
