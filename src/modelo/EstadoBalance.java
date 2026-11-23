package modelo;

/** Resultado de comparar las calorias consumidas en un dia contra la meta. */
public enum EstadoBalance {
    DEFICIT("Por debajo de la meta"),
    EN_META("Dentro de la meta"),
    EXCESO("Por encima de la meta");

    private final String descripcion;

    EstadoBalance(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
