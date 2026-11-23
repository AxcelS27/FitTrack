package interfaz;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import modelo.MedicionCorporal;
import negocio.GestorProgreso;

/** Menu del modulo 4: progreso corporal. */
public class MenuProgreso {
    private final Consola consola;
    private final GestorProgreso gestor;

    public MenuProgreso(Consola consola, GestorProgreso gestor) {
        this.consola = consola;
        this.gestor = gestor;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("PROGRESO CORPORAL");
            consola.mostrar("1. Registrar medicion");
            consola.mostrar("2. Ver historial de mediciones");
            consola.mostrar("3. Generar reporte de progreso");
            consola.mostrar("0. Volver");
            opcion = consola.leerEntero("Opcion: ", 0, 3);
            try {
                switch (opcion) {
                    case 1: registrar(); break;
                    case 2: verHistorial(); break;
                    case 3: consola.mostrar(gestor.generarReporte()); break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void registrar() {
        LocalDate fecha = consola.leerFecha("Fecha de la medicion");
        double peso = consola.leerDecimal("Peso en kg (30-300): ", 30, 300);
        double cintura = consola.leerDecimal("Cintura en cm (40-200): ", 40, 200);
        gestor.registrarMedicion(fecha, peso, cintura);
        consola.mostrar("Medicion registrada.");
    }

    private void verHistorial() {
        List<MedicionCorporal> lista = gestor.listarMediciones();
        if (lista.isEmpty()) {
            consola.mostrar("Aun no hay mediciones.");
            return;
        }
        for (MedicionCorporal m : lista) {
            consola.mostrar(String.format(Locale.US, "%s | peso %.1f kg | cintura %.1f cm",
                    m.getFecha(), m.getPesoKg(), m.getCinturaCm()));
        }
    }
}
