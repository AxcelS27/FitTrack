package interfaz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import modelo.Ejercicio;
import modelo.EjercicioFuerza;
import modelo.GrupoMuscular;
import modelo.Rutina;
import modelo.SerieRealizada;
import modelo.SesionEntrenamiento;
import negocio.GestorEntrenamiento;
import negocio.GestorUsuario;

/** Menu del modulo 2: rutinas, sesiones y progresion. */
public class MenuEntrenamiento {
    private final Consola consola;
    private final GestorEntrenamiento gestor;
    private final GestorUsuario gestorUsuario;

    public MenuEntrenamiento(Consola consola, GestorEntrenamiento gestor, GestorUsuario gestorUsuario) {
        this.consola = consola;
        this.gestor = gestor;
        this.gestorUsuario = gestorUsuario;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("ENTRENAMIENTO");
            consola.mostrar("1. Crear rutina");
            consola.mostrar("2. Agregar ejercicio a una rutina");
            consola.mostrar("3. Crear ejercicio nuevo");
            consola.mostrar("4. Ver rutinas");
            consola.mostrar("5. Registrar sesion de entrenamiento");
            consola.mostrar("6. Progresion y estancamiento de un ejercicio");
            consola.mostrar("7. Historial de sesiones");
            consola.mostrar("0. Volver");
            opcion = consola.leerEntero("Opcion: ", 0, 7);
            try {
                switch (opcion) {
                    case 1: crearRutina(); break;
                    case 2: agregarEjercicio(); break;
                    case 3: crearEjercicio(); break;
                    case 4: verRutinas(); break;
                    case 5: registrarSesion(); break;
                    case 6: verProgresion(); break;
                    case 7: verHistorial(); break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void crearRutina() {
        gestorUsuario.consultarPerfil();
        String nombre = consola.leerTexto("Nombre de la rutina (ej. Pecho y triceps): ");
        int series = consola.leerEntero("Series por ejercicio (1-10): ", 1, 10);
        int repsMin = consola.leerEntero("Repeticiones minimas (1-30): ", 1, 30);
        int repsMax = consola.leerEntero("Repeticiones maximas (" + repsMin + "-50): ", repsMin, 50);
        Rutina rutina = gestor.crearRutina(nombre, series, repsMin, repsMax);
        consola.mostrar("Rutina creada: " + rutina);
    }

    private void agregarEjercicio() {
        Rutina rutina = elegirRutina();
        Ejercicio ejercicio = consola.elegir("Ejercicios disponibles:", gestor.listarEjercicios());
        gestor.agregarEjercicioARutina(rutina, ejercicio);
        consola.mostrar("Agregado '" + ejercicio.getNombre() + "' a la rutina '" + rutina.getNombre() + "'.");
    }

    private void crearEjercicio() {
        int tipo = consola.leerEntero("Tipo (1 = Fuerza, 2 = Cardio): ", 1, 2);
        String nombre = consola.leerTexto("Nombre del ejercicio: ");
        if (tipo == 1) {
            List<GrupoMuscular> grupos = new ArrayList<>();
            for (GrupoMuscular g : GrupoMuscular.values()) {
                if (g != GrupoMuscular.CARDIO) {
                    grupos.add(g);
                }
            }
            GrupoMuscular grupo = consola.elegir("Grupo muscular:", grupos);
            double peso = consola.leerDecimal("Peso inicial de trabajo en kg (0-500): ", 0, 500);
            gestor.crearEjercicioFuerza(nombre, grupo, peso);
        } else {
            double met = consola.leerDecimal("Valor MET (1-20, ej. caminata 3.5): ", 1, 20);
            gestor.crearEjercicioCardio(nombre, met);
        }
        consola.mostrar("Ejercicio creado.");
    }

    private void verRutinas() {
        List<Rutina> rutinas = gestor.listarRutinas();
        if (rutinas.isEmpty()) {
            consola.mostrar("Aun no tienes rutinas.");
            return;
        }
        for (Rutina r : rutinas) {
            consola.mostrar("- " + r);
            for (Ejercicio e : r.getEjercicios()) {
                String extra = e instanceof EjercicioFuerza
                        ? String.format(Locale.US, " (peso actual: %.1f kg)", ((EjercicioFuerza) e).getPesoActualKg())
                        : "";
                consola.mostrar("    * " + e + extra);
            }
        }
    }

    private void registrarSesion() {
        Rutina rutina = elegirRutina();
        if (rutina.getEjercicios().isEmpty()) {
            throw new IllegalStateException("La rutina no tiene ejercicios. Agrega al menos uno primero.");
        }
        LocalDate fecha = consola.leerFecha("Fecha de la sesion");
        int duracion = consola.leerEntero("Duracion total en minutos (5-300): ", 5, 300);

        List<SerieRealizada> series = new ArrayList<>();
        for (Ejercicio e : rutina.getEjercicios()) {
            if (!(e instanceof EjercicioFuerza)) {
                continue;
            }
            EjercicioFuerza fuerza = (EjercicioFuerza) e;
            consola.mostrar(String.format(Locale.US, "%n%s (peso de trabajo: %.1f kg)", fuerza.getNombre(), fuerza.getPesoActualKg()));
            int cantidad = consola.leerEntero("  Series realizadas (0 = omitir, max 10): ", 0, 10);
            for (int i = 1; i <= cantidad; i++) {
                int reps = consola.leerEntero("  Serie " + i + " - repeticiones: ", 1, 100);
                double peso = consola.leerDecimal("  Serie " + i + " - peso kg: ", 0, 500);
                series.add(new SerieRealizada(fuerza, reps, peso));
            }
        }
        SesionEntrenamiento sesion = gestor.registrarSesion(fecha, rutina, duracion, series);
        double pesoUsuario = gestorUsuario.consultarPerfil().getPesoKg();
        consola.mostrar(String.format(Locale.US, "%nSesion guardada. Volumen total: %.0f kg | Gasto estimado: %.0f kcal",
                sesion.calcularVolumenTotal(), sesion.calcularGastoCalorico(pesoUsuario)));
        consola.mostrar("--- Progresion ---");
        for (EjercicioFuerza e : gestor.ejerciciosEntrenados(sesion)) {
            consola.mostrar(gestor.evaluarProgresion(e));
            if (gestor.detectarEstancamiento(e)) {
                consola.mostrar("  [!] Estancamiento detectado en " + e.getNombre()
                        + ": considera descansar, bajar 10% la carga o cambiar el estimulo.");
            }
        }
    }

    private void verProgresion() {
        List<EjercicioFuerza> lista = gestor.ejerciciosConHistorial();
        if (lista.isEmpty()) {
            consola.mostrar("Aun no hay sesiones registradas.");
            return;
        }
        EjercicioFuerza e = consola.elegir("Ejercicio a evaluar:", lista);
        consola.mostrar(gestor.evaluarProgresion(e));
        consola.mostrar(gestor.detectarEstancamiento(e)
                ? "Estado: ESTANCADO en las ultimas 3 sesiones."
                : "Estado: sin estancamiento.");
    }

    private void verHistorial() {
        List<SesionEntrenamiento> sesiones = gestor.listarSesiones();
        if (sesiones.isEmpty()) {
            consola.mostrar("Aun no hay sesiones registradas.");
            return;
        }
        double pesoUsuario = gestorUsuario.consultarPerfil().getPesoKg();
        for (SesionEntrenamiento s : sesiones) {
            consola.mostrar(String.format(Locale.US, "%s | %s | %d min | volumen %.0f kg | %.0f kcal",
                    s.getFecha(), s.getRutina().getNombre(), s.getDuracionMin(),
                    s.calcularVolumenTotal(), s.calcularGastoCalorico(pesoUsuario)));
        }
    }

    private Rutina elegirRutina() {
        List<Rutina> rutinas = gestor.listarRutinas();
        if (rutinas.isEmpty()) {
            throw new IllegalStateException("Aun no tienes rutinas. Crea una primero.");
        }
        return consola.elegir("Rutinas:", rutinas);
    }
}
