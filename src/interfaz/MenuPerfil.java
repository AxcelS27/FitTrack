package interfaz;

import java.util.Locale;

import modelo.MetaNutricional;
import modelo.NivelActividad;
import modelo.Objetivo;
import modelo.Sexo;
import modelo.Usuario;
import negocio.GestorUsuario;

/** Menu del modulo 1: perfil y meta nutricional. */
public class MenuPerfil {
    private final Consola consola;
    private final GestorUsuario gestor;

    public MenuPerfil(Consola consola, GestorUsuario gestor) {
        this.consola = consola;
        this.gestor = gestor;
    }

    public void mostrar() {
        int opcion;
        do {
            consola.titulo("PERFIL Y METAS");
            consola.mostrar("1. Registrar perfil");
            consola.mostrar("2. Ver perfil y meta nutricional");
            consola.mostrar("3. Actualizar datos");
            consola.mostrar("0. Volver");
            opcion = consola.leerEntero("Opcion: ", 0, 3);
            try {
                switch (opcion) {
                    case 1: registrar(); break;
                    case 2: verPerfil(); break;
                    case 3: actualizar(); break;
                    default: break;
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                consola.error(e.getMessage());
            }
        } while (opcion != 0);
    }

    private void registrar() {
        if (gestor.existePerfil()) {
            throw new IllegalStateException("Ya existe un perfil. Usa 'Actualizar datos'.");
        }
        String nombre = consola.leerTexto("Nombre: ");
        Sexo sexo = consola.elegirEnum("Sexo:", Sexo.values());
        int edad = consola.leerEntero("Edad (14-100): ", 14, 100);
        double estatura = consola.leerDecimal("Estatura en cm (120-230): ", 120, 230);
        double peso = consola.leerDecimal("Peso en kg (30-300): ", 30, 300);
        NivelActividad nivel = consola.elegirEnum("Nivel de actividad:", NivelActividad.values());
        Objetivo objetivo = consola.elegirEnum("Objetivo:", Objetivo.values());
        gestor.registrarPerfil(nombre, sexo, edad, estatura, peso, nivel, objetivo);
        consola.mostrar("Perfil registrado correctamente.");
        verPerfil();
    }

    private void verPerfil() {
        Usuario u = gestor.consultarPerfil();
        MetaNutricional meta = gestor.calcularMeta();
        consola.mostrar(String.format(Locale.US, "%nNombre: %s | %s | %d anios", u.getNombre(), u.getSexo(), u.getEdad()));
        consola.mostrar(String.format(Locale.US, "Estatura: %.1f cm | Peso: %.1f kg", u.getEstaturaCm(), u.getPesoKg()));
        consola.mostrar(String.format(Locale.US, "IMC: %.1f (%s)", u.calcularIMC(), u.clasificarIMC()));
        consola.mostrar("Actividad: " + u.getNivel());
        consola.mostrar("Objetivo: " + u.getObjetivo());
        consola.mostrar(String.format(Locale.US, "TMB: %.0f kcal | Gasto total: %.0f kcal", u.calcularTMB(), u.calcularGastoTotal()));
        consola.mostrar("--- Meta diaria ---");
        consola.mostrar(String.format(Locale.US, "Calorias: %.0f kcal", meta.getCalorias()));
        consola.mostrar(String.format(Locale.US, "Proteina: %.1f g | Carbohidratos: %.1f g | Grasas: %.1f g",
                meta.getProteinaG(), meta.getCarbosG(), meta.getGrasasG()));
    }

    private void actualizar() {
        Usuario u = gestor.consultarPerfil();
        consola.mostrar("Ingresa los nuevos valores (actual entre parentesis).");
        int edad = consola.leerEntero("Edad (" + u.getEdad() + "): ", 14, 100);
        double estatura = consola.leerDecimal("Estatura cm (" + u.getEstaturaCm() + "): ", 120, 230);
        double peso = consola.leerDecimal("Peso kg (" + u.getPesoKg() + "): ", 30, 300);
        NivelActividad nivel = consola.elegirEnum("Nivel de actividad:", NivelActividad.values());
        Objetivo objetivo = consola.elegirEnum("Objetivo:", Objetivo.values());
        gestor.actualizarDatos(edad, estatura, peso, nivel, objetivo);
        consola.mostrar("Datos actualizados.");
        verPerfil();
    }
}
