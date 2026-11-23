package interfaz;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import modelo.Alimento;
import modelo.DiaNutricional;
import modelo.MetaNutricional;
import modelo.RegistroComida;
import modelo.TipoComida;
import negocio.ElementoNoEncontradoException;
import negocio.GestorNutricion;
import negocio.GestorUsuario;

/** Menu del modulo 3: alimentacion. */
public class MenuNutricion {
    private final Consola consola;
    private final GestorNutricion gestor;
    private final GestorUsuario gestorUsuario;

    public MenuNutricion(Consola consola, GestorNutricion gestor, GestorUsuario gestorUsuario) {
        this.consola = consola;
        this.gestor = gestor;
        this.gestorUsuario = gestorUsuario;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("NUTRICION");
            consola.mostrar("1. Ver mi meta diaria");
            consola.mostrar("2. Registrar comida");
            consola.mostrar("3. Eliminar un registro de comida");
            consola.mostrar("4. Ver balance de un dia");
            consola.mostrar("5. Agregar alimento al catalogo");
            consola.mostrar("6. Buscar alimentos");
            consola.mostrar("0. Volver");
            opcion = consola.leerEntero("Opcion: ", 0, 6);
            try {
                switch (opcion) {
                    case 1: verMeta(); break;
                    case 2: registrarComida(); break;
                    case 3: eliminarRegistro(); break;
                    case 4: verBalance(); break;
                    case 5: agregarAlimento(); break;
                    case 6: buscar(); break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException | ElementoNoEncontradoException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void verMeta() {
        MetaNutricional meta = gestorUsuario.calcularMeta();
        consola.mostrar(String.format(Locale.US, "Meta diaria: %.0f kcal | P %.1f g | C %.1f g | G %.1f g",
                meta.getCalorias(), meta.getProteinaG(), meta.getCarbosG(), meta.getGrasasG()));
    }

    private void registrarComida() throws ElementoNoEncontradoException {
        gestorUsuario.consultarPerfil();
        String texto = consola.leerTexto("Buscar alimento (ej. arroz): ");
        List<Alimento> encontrados = gestor.buscarAlimentos(texto);
        Alimento alimento = consola.elegir("Resultados:", encontrados);
        double gramos = consola.leerDecimal("Gramos consumidos (1-5000): ", 1, 5000);
        TipoComida tipo = consola.elegirEnum("Tipo de comida:", TipoComida.values());
        LocalDate fecha = consola.leerFecha("Fecha");
        RegistroComida registro = gestor.registrarComida(fecha, tipo, alimento, gramos);
        consola.mostrar("Registrado: " + registro);
    }

    private void eliminarRegistro() throws ElementoNoEncontradoException {
        LocalDate fecha = consola.leerFecha("Fecha del dia");
        DiaNutricional dia = gestor.obtenerDia(fecha);
        if (dia.getRegistros().isEmpty()) {
            consola.mostrar("Ese dia no tiene registros.");
            return;
        }
        RegistroComida elegido = consola.elegir("Registros del dia:", dia.getRegistros());
        if (consola.confirmar("Eliminar este registro")) {
            gestor.eliminarRegistro(fecha, dia.getRegistros().indexOf(elegido));
            consola.mostrar("Registro eliminado.");
        }
    }

    private void verBalance() throws ElementoNoEncontradoException {
        LocalDate fecha = consola.leerFecha("Fecha del dia");
        DiaNutricional dia = gestor.obtenerDia(fecha);
        for (RegistroComida r : dia.getRegistros()) {
            consola.mostrar("  - " + r);
        }
        consola.mostrar(gestor.generarBalance(fecha));
    }

    private void agregarAlimento() {
        String nombre = consola.leerTexto("Nombre del alimento: ");
        double kcal = consola.leerDecimal("Calorias por 100 g (0-900): ", 0, 900);
        double prot = consola.leerDecimal("Proteina por 100 g (0-100): ", 0, 100);
        double carb = consola.leerDecimal("Carbohidratos por 100 g (0-100): ", 0, 100);
        double gras = consola.leerDecimal("Grasas por 100 g (0-100): ", 0, 100);
        Alimento a = gestor.agregarAlimento(nombre, kcal, prot, carb, gras);
        consola.mostrar("Alimento agregado: " + a);
    }

    private void buscar() throws ElementoNoEncontradoException {
        String texto = consola.leerTexto("Texto a buscar: ");
        for (Alimento a : gestor.buscarAlimentos(texto)) {
            consola.mostrar("  - " + a);
        }
    }
}
