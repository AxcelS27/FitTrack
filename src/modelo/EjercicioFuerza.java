package modelo;

/** Ejercicio de fuerza con peso de trabajo actual e incremento de carga. */
public class EjercicioFuerza extends Ejercicio {
    private static final long serialVersionUID = 1L;
    private static final double MET_FUERZA = 5.0;

    private double pesoActualKg;
    private double incrementoKg;

    public EjercicioFuerza(String nombre, GrupoMuscular grupo, double pesoActualKg) {
        super(nombre, grupo);
        setPesoActualKg(pesoActualKg);
        this.incrementoKg = 2.5;
    }

    public double getPesoActualKg() {
        return pesoActualKg;
    }

    public void setPesoActualKg(double pesoActualKg) {
        if (pesoActualKg < 0 || pesoActualKg > 500) {
            throw new IllegalArgumentException("El peso de trabajo debe estar entre 0 y 500 kg.");
        }
        this.pesoActualKg = pesoActualKg;
    }

    public double getIncrementoKg() {
        return incrementoKg;
    }

    /** Sube el peso de trabajo segun el incremento configurado. */
    public void subirCarga() {
        pesoActualKg += incrementoKg;
    }

    @Override
    public double calcularGastoCalorico(double pesoKg, int minutos) {
        return MET_FUERZA * pesoKg * (minutos / 60.0);
    }

    @Override
    public String getTipo() {
        return "Fuerza";
    }
}
