package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Conjunto de comidas registradas en un dia. */
public class DiaNutricional implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate fecha;
    private final List<RegistroComida> registros = new ArrayList<>();

    public DiaNutricional(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        this.fecha = fecha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public List<RegistroComida> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    public void agregarRegistro(RegistroComida registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo.");
        }
        registros.add(registro);
    }

    public void eliminarRegistro(int indice) {
        if (indice < 0 || indice >= registros.size()) {
            throw new IllegalArgumentException("El registro indicado no existe.");
        }
        registros.remove(indice);
    }

    public double calcularCalorias() {
        double total = 0;
        for (RegistroComida r : registros) {
            total += r.calcularCalorias();
        }
        return total;
    }

    public double calcularProteina() {
        double total = 0;
        for (RegistroComida r : registros) {
            total += r.calcularProteina();
        }
        return total;
    }

    public double calcularCarbos() {
        double total = 0;
        for (RegistroComida r : registros) {
            total += r.calcularCarbos();
        }
        return total;
    }

    public double calcularGrasas() {
        double total = 0;
        for (RegistroComida r : registros) {
            total += r.calcularGrasas();
        }
        return total;
    }

    /** Compara las calorias del dia contra la meta: menos de 90% = deficit, mas de 110% = exceso. */
    public EstadoBalance evaluar(MetaNutricional meta) {
        double proporcion = calcularCalorias() / meta.getCalorias();
        if (proporcion < 0.90) {
            return EstadoBalance.DEFICIT;
        }
        if (proporcion > 1.10) {
            return EstadoBalance.EXCESO;
        }
        return EstadoBalance.EN_META;
    }
}
