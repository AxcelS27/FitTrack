package modelo;

/** Nivel de actividad fisica diaria y su factor multiplicador sobre el metabolismo basal. */
public enum NivelActividad {
    SEDENTARIO(1.2, "Sedentario (poco o nada de ejercicio)"),
    LIGERO(1.375, "Ligero (1-3 dias de entrenamiento por semana)"),
    MODERADO(1.55, "Moderado (3-5 dias de entrenamiento por semana)"),
    INTENSO(1.725, "Intenso (6-7 dias de entrenamiento por semana)");

    private final double factor;
    private final String descripcion;

    NivelActividad(double factor, String descripcion) {
        this.factor = factor;
        this.descripcion = descripcion;
    }

    public double getFactor() {
        return factor;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
