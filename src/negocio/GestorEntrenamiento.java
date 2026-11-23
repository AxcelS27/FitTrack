package negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import modelo.Ejercicio;
import modelo.EjercicioCardio;
import modelo.EjercicioFuerza;
import modelo.GrupoMuscular;
import modelo.Rutina;
import modelo.SerieRealizada;
import modelo.SesionEntrenamiento;
import modelo.Usuario;

/** Modulo 2: rutinas, sesiones de entrenamiento y progresion de carga. */
public class GestorEntrenamiento {
    private final AlmacenDatos datos;

    public GestorEntrenamiento(AlmacenDatos datos) {
        this.datos = datos;
    }

    // ---------- Catalogo de ejercicios ----------

    public List<Ejercicio> listarEjercicios() {
        return Collections.unmodifiableList(datos.getEjercicios());
    }

    public List<EjercicioFuerza> listarEjerciciosFuerza() {
        List<EjercicioFuerza> resultado = new ArrayList<>();
        for (Ejercicio e : datos.getEjercicios()) {
            if (e instanceof EjercicioFuerza) {
                resultado.add((EjercicioFuerza) e);
            }
        }
        return resultado;
    }

    public EjercicioFuerza crearEjercicioFuerza(String nombre, GrupoMuscular grupo, double pesoInicialKg) {
        validarNombreEjercicioNuevo(nombre);
        if (grupo == GrupoMuscular.CARDIO) {
            throw new IllegalArgumentException("Un ejercicio de fuerza no puede ser del grupo CARDIO.");
        }
        EjercicioFuerza ejercicio = new EjercicioFuerza(nombre, grupo, pesoInicialKg);
        datos.getEjercicios().add(ejercicio);
        return ejercicio;
    }

    public EjercicioCardio crearEjercicioCardio(String nombre, double met) {
        validarNombreEjercicioNuevo(nombre);
        EjercicioCardio ejercicio = new EjercicioCardio(nombre, met);
        datos.getEjercicios().add(ejercicio);
        return ejercicio;
    }

    private void validarNombreEjercicioNuevo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del ejercicio es obligatorio.");
        }
        for (Ejercicio e : datos.getEjercicios()) {
            if (e.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new IllegalArgumentException("Ya existe un ejercicio llamado '" + e.getNombre() + "'.");
            }
        }
    }

    // ---------- Rutinas ----------

    /** RF3: crea una rutina nueva (sin ejercicios todavia). */
    public Rutina crearRutina(String nombre, int series, int repsMin, int repsMax) {
        Usuario usuario = datos.exigirUsuario();
        for (Rutina r : usuario.getRutinas()) {
            if (r.getNombre().equalsIgnoreCase(nombre == null ? "" : nombre.trim())) {
                throw new IllegalArgumentException("Ya existe una rutina llamada '" + r.getNombre() + "'.");
            }
        }
        Rutina rutina = new Rutina(nombre, series, repsMin, repsMax);
        usuario.agregarRutina(rutina);
        return rutina;
    }

    /** RF3: agrega un ejercicio del catalogo a una rutina. */
    public void agregarEjercicioARutina(Rutina rutina, Ejercicio ejercicio) {
        datos.exigirUsuario();
        if (rutina == null) {
            throw new IllegalArgumentException("Debes elegir una rutina.");
        }
        rutina.agregarEjercicio(ejercicio);
    }

    public List<Rutina> listarRutinas() {
        return datos.exigirUsuario().getRutinas();
    }

    // ---------- Sesiones ----------

    /** RF4: registra una sesion con sus series y actualiza el peso de trabajo de cada ejercicio. */
    public SesionEntrenamiento registrarSesion(LocalDate fecha, Rutina rutina, int duracionMin,
                                               List<SerieRealizada> series) {
        Usuario usuario = datos.exigirUsuario();
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura.");
        }
        if (rutina == null) {
            throw new IllegalArgumentException("Debes elegir una rutina.");
        }
        if (rutina.getEjercicios().isEmpty()) {
            throw new IllegalArgumentException("La rutina no tiene ejercicios. Agrega al menos uno.");
        }
        if (series == null || series.isEmpty()) {
            throw new IllegalArgumentException("Debes registrar al menos una serie.");
        }
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha, rutina, duracionMin);
        for (SerieRealizada serie : series) {
            if (!rutina.contiene(serie.getEjercicio())) {
                throw new IllegalArgumentException(
                        "El ejercicio '" + serie.getEjercicio().getNombre() + "' no pertenece a la rutina.");
            }
            sesion.agregarSerie(serie);
        }
        usuario.agregarSesion(sesion);
        for (EjercicioFuerza ejercicio : ejerciciosEntrenados(sesion)) {
            ejercicio.setPesoActualKg(pesoMaximo(sesion.getSeriesDe(ejercicio)));
        }
        return sesion;
    }

    public List<SesionEntrenamiento> listarSesiones() {
        return datos.exigirUsuario().getSesiones();
    }

    public List<EjercicioFuerza> ejerciciosEntrenados(SesionEntrenamiento sesion) {
        List<EjercicioFuerza> lista = new ArrayList<>();
        for (SerieRealizada s : sesion.getSeries()) {
            if (!lista.contains(s.getEjercicio())) {
                lista.add(s.getEjercicio());
            }
        }
        return lista;
    }

    // ---------- Progresion ----------

    /**
     * RF5 (regla de negocio): doble progresion. Si en la ultima sesion se alcanzaron las repeticiones
     * maximas de la rutina en todas las series, se sube el peso de trabajo del ejercicio.
     * Devuelve un mensaje con la recomendacion.
     */
    public String evaluarProgresion(EjercicioFuerza ejercicio) {
        List<SesionEntrenamiento> historial = sesionesCon(ejercicio);
        if (historial.isEmpty()) {
            return "Aun no tienes sesiones registradas con " + ejercicio.getNombre() + ".";
        }
        SesionEntrenamiento ultima = historial.get(historial.size() - 1);
        Rutina rutina = ultima.getRutina();
        List<SerieRealizada> series = ultima.getSeriesDe(ejercicio);
        double pesoUsado = pesoMaximo(series);

        if (ejercicio.getPesoActualKg() > pesoUsado) {
            return String.format(Locale.US,
                    "Tu peso de trabajo en %s ya fue subido a %.1f kg. Usalo en la proxima sesion.",
                    ejercicio.getNombre(), ejercicio.getPesoActualKg());
        }
        if (series.size() < rutina.getSeriesObjetivo()) {
            return String.format(Locale.US,
                    "%s: completa las %d series objetivo con %.1f kg antes de subir de carga.",
                    ejercicio.getNombre(), rutina.getSeriesObjetivo(), pesoUsado);
        }
        boolean todasEnElTecho = true;
        boolean algunaDebajo = false;
        for (SerieRealizada s : series) {
            if (s.getRepeticiones() < rutina.getRepsMax()) {
                todasEnElTecho = false;
            }
            if (s.getRepeticiones() < rutina.getRepsMin()) {
                algunaDebajo = true;
            }
        }
        if (todasEnElTecho) {
            ejercicio.subirCarga();
            return String.format(Locale.US,
                    "%s: llegaste a %d repeticiones en todas las series. SUBE a %.1f kg.",
                    ejercicio.getNombre(), rutina.getRepsMax(), ejercicio.getPesoActualKg());
        }
        if (algunaDebajo) {
            return String.format(Locale.US,
                    "%s: alguna serie quedo por debajo de %d repeticiones. Manten %.1f kg hasta entrar al rango %d-%d.",
                    ejercicio.getNombre(), rutina.getRepsMin(), pesoUsado,
                    rutina.getRepsMin(), rutina.getRepsMax());
        }
        return String.format(Locale.US,
                "%s: manten %.1f kg y busca llegar a %d repeticiones en todas las series.",
                ejercicio.getNombre(), pesoUsado, rutina.getRepsMax());
    }

    /**
     * RF5 (regla de negocio): hay estancamiento si en las ultimas 3 sesiones con el ejercicio
     * el volumen (repeticiones x peso) de las dos mas recientes no supero al de la mas antigua.
     */
    public boolean detectarEstancamiento(EjercicioFuerza ejercicio) {
        List<SesionEntrenamiento> historial = sesionesCon(ejercicio);
        if (historial.size() < 3) {
            return false;
        }
        int n = historial.size();
        double v1 = historial.get(n - 3).calcularVolumenDe(ejercicio);
        double v2 = historial.get(n - 2).calcularVolumenDe(ejercicio);
        double v3 = historial.get(n - 1).calcularVolumenDe(ejercicio);
        return v2 <= v1 && v3 <= v1;
    }

    /** Ejercicios de fuerza que tienen al menos una sesion registrada. */
    public List<EjercicioFuerza> ejerciciosConHistorial() {
        List<EjercicioFuerza> lista = new ArrayList<>();
        for (EjercicioFuerza e : listarEjerciciosFuerza()) {
            if (!sesionesCon(e).isEmpty()) {
                lista.add(e);
            }
        }
        return lista;
    }

    private List<SesionEntrenamiento> sesionesCon(EjercicioFuerza ejercicio) {
        List<SesionEntrenamiento> resultado = new ArrayList<>();
        for (SesionEntrenamiento s : datos.exigirUsuario().getSesiones()) {
            if (!s.getSeriesDe(ejercicio).isEmpty()) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    private double pesoMaximo(List<SerieRealizada> series) {
        double max = 0;
        for (SerieRealizada s : series) {
            max = Math.max(max, s.getPesoKg());
        }
        return max;
    }
}
