package negocio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import modelo.MedicionCorporal;
import modelo.MetaNutricional;
import modelo.Objetivo;
import modelo.Usuario;

/** Modulo 4: mediciones corporales y reporte de progreso. */
public class GestorProgreso {
    private static final double AJUSTE_KCAL = 150;
    private static final int DIAS_MINIMOS = 7;

    private final AlmacenDatos datos;

    public GestorProgreso(AlmacenDatos datos) {
        this.datos = datos;
    }

    /**
     * RF9: registra una medicion. Si es la mas reciente, tambien actualiza el peso del perfil
     * para que la meta nutricional use el peso actual.
     */
    public MedicionCorporal registrarMedicion(LocalDate fecha, double pesoKg, double cinturaCm) {
        Usuario usuario = datos.exigirUsuario();
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura.");
        }
        for (MedicionCorporal m : usuario.getMediciones()) {
            if (m.getFecha().equals(fecha)) {
                throw new IllegalArgumentException("Ya existe una medicion registrada el " + fecha + ".");
            }
        }
        MedicionCorporal medicion = new MedicionCorporal(fecha, pesoKg, cinturaCm);
        usuario.agregarMedicion(medicion);
        List<MedicionCorporal> lista = usuario.getMediciones();
        if (lista.get(lista.size() - 1) == medicion) {
            usuario.setPesoKg(pesoKg);
        }
        return medicion;
    }

    public List<MedicionCorporal> listarMediciones() {
        return datos.exigirUsuario().getMediciones();
    }

    /**
     * RF10 (regla de negocio): calcula el ritmo de cambio de peso (% del peso por semana) entre la
     * primera y la ultima medicion y lo compara con el rango ideal del objetivo.
     * Si esta fuera del rango recomienda ajustar 150 kcal.
     */
    public String generarReporte() {
        Usuario usuario = datos.exigirUsuario();
        List<MedicionCorporal> lista = usuario.getMediciones();
        if (lista.size() < 2) {
            return "Necesitas al menos 2 mediciones para generar el reporte.";
        }
        MedicionCorporal primera = lista.get(0);
        MedicionCorporal ultima = lista.get(lista.size() - 1);
        long dias = ChronoUnit.DAYS.between(primera.getFecha(), ultima.getFecha());
        if (dias < DIAS_MINIMOS) {
            return "Las mediciones deben abarcar al menos " + DIAS_MINIMOS
                    + " dias (llevas " + dias + ").";
        }
        double cambioKg = ultima.getPesoKg() - primera.getPesoKg();
        double ritmo = (cambioKg / primera.getPesoKg() * 100.0) / (dias / 7.0);
        double cambioCintura = ultima.getCinturaCm() - primera.getCinturaCm();
        Objetivo objetivo = usuario.getObjetivo();
        MetaNutricional meta = CalculadoraNutricional.calcular(usuario);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "Periodo: %s a %s (%d dias)%n",
                primera.getFecha(), ultima.getFecha(), dias));
        sb.append(String.format(Locale.US, "Peso: %.1f kg -> %.1f kg (%+.1f kg)%n",
                primera.getPesoKg(), ultima.getPesoKg(), cambioKg));
        sb.append(String.format(Locale.US, "Cintura: %.1f cm -> %.1f cm (%+.1f cm)%n",
                primera.getCinturaCm(), ultima.getCinturaCm(), cambioCintura));
        sb.append(String.format(Locale.US, "Ritmo: %+.2f %% del peso por semana (ideal para %s: %.2f a %.2f)%n",
                ritmo, objetivo, objetivo.getRitmoMin(), objetivo.getRitmoMax()));

        if (ritmo < objetivo.getRitmoMin()) {
            sb.append(String.format(Locale.US,
                    "Diagnostico: tu peso va por debajo de lo ideal.%nRecomendacion: SUBE %.0f kcal (nueva meta: %.0f kcal).",
                    AJUSTE_KCAL, meta.getCalorias() + AJUSTE_KCAL));
        } else if (ritmo > objetivo.getRitmoMax()) {
            sb.append(String.format(Locale.US,
                    "Diagnostico: tu peso va por encima de lo ideal.%nRecomendacion: BAJA %.0f kcal (nueva meta: %.0f kcal).",
                    AJUSTE_KCAL, meta.getCalorias() - AJUSTE_KCAL));
        } else {
            sb.append("Diagnostico: vas dentro del ritmo ideal.\nRecomendacion: manten tu plan actual.");
        }
        return sb.toString();
    }
}
