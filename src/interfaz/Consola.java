package interfaz;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/** Utilidades de entrada y salida por consola con validacion de lo que escribe el usuario. */
public class Consola {
    private final Scanner entrada = new Scanner(System.in);

    public void mostrar(String texto) {
        System.out.println(texto);
    }

    public void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    public void error(String mensaje) {
        System.out.println("  [!] " + mensaje);
    }

    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            error("Este campo no puede estar vacio.");
        }
    }

    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor < min || valor > max) {
                    error("Ingresa un numero entre " + min + " y " + max + ".");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                error("Ingresa un numero entero valido.");
            }
        }
    }

    public double leerDecimal(String mensaje, double min, double max) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (valor < min || valor > max) {
                    error("Ingresa un valor entre " + min + " y " + max + ".");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                error("Ingresa un numero valido (ejemplo: 72.5).");
            }
        }
    }

    /** Lee una fecha con formato aaaa-mm-dd. Si se deja vacio devuelve la fecha de hoy. */
    public LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (aaaa-mm-dd, Enter = hoy): ");
            String texto = entrada.nextLine().trim();
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                error("Fecha invalida. Usa el formato aaaa-mm-dd (ejemplo: 2026-11-22).");
            }
        }
    }

    public boolean confirmar(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String texto = entrada.nextLine().trim().toLowerCase();
            if (texto.equals("s")) {
                return true;
            }
            if (texto.equals("n")) {
                return false;
            }
            error("Responde 's' o 'n'.");
        }
    }

    /** Muestra una lista numerada y devuelve el elemento elegido. */
    public <T> T elegir(String mensaje, List<T> opciones) {
        mostrar(mensaje);
        for (int i = 0; i < opciones.size(); i++) {
            mostrar("  " + (i + 1) + ". " + opciones.get(i));
        }
        int indice = leerEntero("Elige una opcion: ", 1, opciones.size());
        return opciones.get(indice - 1);
    }

    public <T extends Enum<T>> T elegirEnum(String mensaje, T[] valores) {
        return elegir(mensaje, Arrays.asList(valores));
    }

    public void pausar() {
        System.out.print("\nPresiona Enter para continuar...");
        entrada.nextLine();
    }
}
