package modelo;

import java.io.Serializable;

/** Alimento del catalogo con su informacion nutricional por cada 100 g. */
public class Alimento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final double kcalPor100g;
    private final double proteinaPor100g;
    private final double carbosPor100g;
    private final double grasasPor100g;

    public Alimento(String nombre, double kcalPor100g, double proteinaPor100g,
                    double carbosPor100g, double grasasPor100g) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del alimento es obligatorio.");
        }
        if (kcalPor100g < 0 || kcalPor100g > 900) {
            throw new IllegalArgumentException("Las calorias por 100 g deben estar entre 0 y 900.");
        }
        if (proteinaPor100g < 0 || carbosPor100g < 0 || grasasPor100g < 0) {
            throw new IllegalArgumentException("Los macronutrientes no pueden ser negativos.");
        }
        if (proteinaPor100g + carbosPor100g + grasasPor100g > 100) {
            throw new IllegalArgumentException(
                    "Proteina + carbohidratos + grasas no pueden superar 100 g por cada 100 g.");
        }
        this.nombre = nombre.trim();
        this.kcalPor100g = kcalPor100g;
        this.proteinaPor100g = proteinaPor100g;
        this.carbosPor100g = carbosPor100g;
        this.grasasPor100g = grasasPor100g;
    }

    public String getNombre() {
        return nombre;
    }

    public double getKcalPor100g() {
        return kcalPor100g;
    }

    public double getProteinaPor100g() {
        return proteinaPor100g;
    }

    public double getCarbosPor100g() {
        return carbosPor100g;
    }

    public double getGrasasPor100g() {
        return grasasPor100g;
    }

    public double calcularCalorias(double gramos) {
        return kcalPor100g * gramos / 100.0;
    }

    public double calcularProteina(double gramos) {
        return proteinaPor100g * gramos / 100.0;
    }

    public double calcularCarbos(double gramos) {
        return carbosPor100g * gramos / 100.0;
    }

    public double calcularGrasas(double gramos) {
        return grasasPor100g * gramos / 100.0;
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "%s (100 g: %.0f kcal | P %.1f | C %.1f | G %.1f)",
                nombre, kcalPor100g, proteinaPor100g, carbosPor100g, grasasPor100g);
    }
}
