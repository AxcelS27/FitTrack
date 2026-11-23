package modelo;

/** Ejercicio cardiovascular. Su gasto calorico depende del valor MET asignado. */
public class EjercicioCardio extends Ejercicio {
    private static final long serialVersionUID = 1L;

    private double met;

    public EjercicioCardio(String nombre, double met) {
        super(nombre, GrupoMuscular.CARDIO);
        setMet(met);
    }

    public double getMet() {
        return met;
    }

    public void setMet(double met) {
        if (met < 1 || met > 20) {
            throw new IllegalArgumentException("El MET debe estar entre 1 y 20.");
        }
        this.met = met;
    }

    @Override
    public double calcularGastoCalorico(double pesoKg, int minutos) {
        return met * pesoKg * (minutos / 60.0);
    }

    @Override
    public String getTipo() {
        return "Cardio";
    }
}
