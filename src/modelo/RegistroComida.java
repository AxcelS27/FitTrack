package modelo;

import java.io.Serializable;
import java.util.Locale;

/** Porcion de un alimento consumida en un momento del dia. */
public class RegistroComida implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TipoComida tipo;
    private final Alimento alimento;
    private final double gramos;

    public RegistroComida(TipoComida tipo, Alimento alimento, double gramos) {
        if (tipo == null) {
            throw new IllegalArgumentException("Debes indicar el tipo de comida.");
        }
        if (alimento == null) {
            throw new IllegalArgumentException("Debes elegir un alimento.");
        }
        if (gramos <= 0 || gramos > 5000) {
            throw new IllegalArgumentException("Los gramos deben estar entre 1 y 5000.");
        }
        this.tipo = tipo;
        this.alimento = alimento;
        this.gramos = gramos;
    }

    public TipoComida getTipo() {
        return tipo;
    }

    public Alimento getAlimento() {
        return alimento;
    }

    public double getGramos() {
        return gramos;
    }

    public double calcularCalorias() {
        return alimento.calcularCalorias(gramos);
    }

    public double calcularProteina() {
        return alimento.calcularProteina(gramos);
    }

    public double calcularCarbos() {
        return alimento.calcularCarbos(gramos);
    }

    public double calcularGrasas() {
        return alimento.calcularGrasas(gramos);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s: %s %.0f g -> %.0f kcal (P %.1f | C %.1f | G %.1f)",
                tipo, alimento.getNombre(), gramos, calcularCalorias(),
                calcularProteina(), calcularCarbos(), calcularGrasas());
    }
}
