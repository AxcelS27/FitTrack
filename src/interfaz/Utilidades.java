package interfaz;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Funciones de apoyo para las ventanas: lectura validada de campos, dialogos y formularios. */
final class Utilidades {

    /** Accion que puede lanzar excepciones de validacion o de negocio. */
    interface Accion {
        void ejecutar() throws Exception;
    }

    private Utilidades() {
    }

    static int entero(JTextField campo, String nombre, int min, int max) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombre + "' es obligatorio.");
        }
        try {
            int valor = Integer.parseInt(texto);
            if (valor < min || valor > max) {
                throw new IllegalArgumentException(
                        "'" + nombre + "' debe estar entre " + min + " y " + max + ".");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + nombre + "' debe ser un numero entero.");
        }
    }

    static double decimal(JTextField campo, String nombre, double min, double max) {
        String texto = campo.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombre + "' es obligatorio.");
        }
        try {
            double valor = Double.parseDouble(texto);
            if (valor < min || valor > max) {
                throw new IllegalArgumentException(
                        "'" + nombre + "' debe estar entre " + min + " y " + max + ".");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + nombre + "' debe ser un numero (ejemplo: 72.5).");
        }
    }

    static String texto(JTextField campo, String nombre) {
        String t = campo.getText().trim();
        if (t.isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombre + "' es obligatorio.");
        }
        return t;
    }

    /** Fecha con formato aaaa-mm-dd; si el campo esta vacio devuelve hoy. */
    static LocalDate fecha(JTextField campo) {
        String t = campo.getText().trim();
        if (t.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(t);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha invalida. Usa el formato aaaa-mm-dd (ejemplo: 2026-11-22).");
        }
    }

    static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "FitTrack", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Ejecuta una accion y muestra cualquier error de validacion o de negocio en un dialogo. */
    static void ejecutar(Component padre, Accion accion) {
        try {
            accion.ejecutar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(padre, e.getMessage());
        } catch (Exception e) {
            error(padre, e.getMessage() == null ? "Ocurrio un error inesperado." : e.getMessage());
        }
    }

    /** Arma un formulario de pares etiqueta / componente. */
    static JPanel formulario(Object... filas) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 3, 5);
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i + 1 < filas.length; i += 2) {
            c.gridy = i / 2;
            c.gridx = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(String.valueOf(filas[i])), c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            panel.add((JComponent) filas[i + 1], c);
        }
        return panel;
    }

    static boolean confirmar(Component padre, String titulo, JComponent contenido) {
        return JOptionPane.showConfirmDialog(padre, contenido, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
