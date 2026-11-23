package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/** Medicion corporal (peso y cintura) tomada en una fecha. */
public class MedicionCorporal implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate fecha;
    private final double pesoKg;
    private final double cinturaCm;

    public MedicionCorporal(LocalDate fecha, double pesoKg, double cinturaCm) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (pesoKg < 30 || pesoKg > 300) {
            throw new IllegalArgumentException("El peso debe estar entre 30 y 300 kg.");
        }
        if (cinturaCm < 40 || cinturaCm > 200) {
            throw new IllegalArgumentException("La cintura debe estar entre 40 y 200 cm.");
        }
        this.fecha = fecha;
        this.pesoKg = pesoKg;
        this.cinturaCm = cinturaCm;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public double getCinturaCm() {
        return cinturaCm;
    }
}
