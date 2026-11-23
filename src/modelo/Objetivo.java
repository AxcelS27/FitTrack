package modelo;

/**
 * Objetivo fisico del usuario. Define el ajuste calorico, la proteina por kg
 * y el rango de ritmo de cambio de peso saludable (% del peso corporal por semana).
 */
public enum Objetivo {
    DEFINICION("Definicion (bajar grasa)", -0.15, 2.2, -1.00, -0.25),
    MANTENIMIENTO("Mantenimiento", 0.00, 1.8, -0.25, 0.25),
    VOLUMEN("Volumen (ganar musculo)", 0.10, 2.0, 0.10, 0.50);

    private final String descripcion;
    private final double ajusteCalorico;
    private final double proteinaPorKg;
    private final double ritmoMin;
    private final double ritmoMax;

    Objetivo(String descripcion, double ajusteCalorico, double proteinaPorKg,
             double ritmoMin, double ritmoMax) {
        this.descripcion = descripcion;
        this.ajusteCalorico = ajusteCalorico;
        this.proteinaPorKg = proteinaPorKg;
        this.ritmoMin = ritmoMin;
        this.ritmoMax = ritmoMax;
    }

    public double getAjusteCalorico() {
        return ajusteCalorico;
    }

    public double getProteinaPorKg() {
        return proteinaPorKg;
    }

    public double getRitmoMin() {
        return ritmoMin;
    }

    public double getRitmoMax() {
        return ritmoMax;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
