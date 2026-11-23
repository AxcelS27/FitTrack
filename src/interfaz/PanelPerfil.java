package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import modelo.MetaNutricional;
import modelo.NivelActividad;
import modelo.Objetivo;
import modelo.Sexo;
import modelo.Usuario;
import negocio.GestorUsuario;

/** Pestana del modulo 1: perfil y meta nutricional. */
public class PanelPerfil extends JPanel {
    private final GestorUsuario gestor;
    private final JTextField nombre = new JTextField(18);
    private final JComboBox<Sexo> sexo = new JComboBox<>(Sexo.values());
    private final JTextField edad = new JTextField(6);
    private final JTextField estatura = new JTextField(6);
    private final JTextField peso = new JTextField(6);
    private final JComboBox<NivelActividad> nivel = new JComboBox<>(NivelActividad.values());
    private final JComboBox<Objetivo> objetivo = new JComboBox<>(Objetivo.values());
    private final JTextArea salida = new JTextArea();

    public PanelPerfil(GestorUsuario gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = Utilidades.formulario(
                "Nombre:", nombre, "Sexo:", sexo, "Edad (14-100):", edad,
                "Estatura cm (120-230):", estatura, "Peso kg (30-300):", peso,
                "Nivel de actividad:", nivel, "Objetivo:", objetivo);
        JButton btnRegistrar = new JButton("Registrar perfil");
        JButton btnActualizar = new JButton("Actualizar datos");
        JButton btnMeta = new JButton("Ver perfil y meta");
        btnRegistrar.addActionListener(e -> Utilidades.ejecutar(this, this::registrar));
        btnActualizar.addActionListener(e -> Utilidades.ejecutar(this, this::actualizar));
        btnMeta.addActionListener(e -> Utilidades.ejecutar(this, this::mostrar));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(btnRegistrar);
        botones.add(btnActualizar);
        botones.add(btnMeta);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(form, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(salida), BorderLayout.CENTER);
    }

    private void registrar() {
        gestor.registrarPerfil(Utilidades.texto(nombre, "Nombre"), (Sexo) sexo.getSelectedItem(),
                Utilidades.entero(edad, "Edad", 14, 100),
                Utilidades.decimal(estatura, "Estatura", 120, 230),
                Utilidades.decimal(peso, "Peso", 30, 300),
                (NivelActividad) nivel.getSelectedItem(), (Objetivo) objetivo.getSelectedItem());
        mostrar();
    }

    private void actualizar() {
        gestor.actualizarDatos(Utilidades.entero(edad, "Edad", 14, 100),
                Utilidades.decimal(estatura, "Estatura", 120, 230),
                Utilidades.decimal(peso, "Peso", 30, 300),
                (NivelActividad) nivel.getSelectedItem(), (Objetivo) objetivo.getSelectedItem());
        mostrar();
    }

    private void mostrar() {
        Usuario u = gestor.consultarPerfil();
        MetaNutricional m = gestor.calcularMeta();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "Nombre: %s | %s | %d anios%n", u.getNombre(), u.getSexo(), u.getEdad()));
        sb.append(String.format(Locale.US, "Estatura: %.1f cm | Peso: %.1f kg%n", u.getEstaturaCm(), u.getPesoKg()));
        sb.append(String.format(Locale.US, "IMC: %.1f (%s)%n", u.calcularIMC(), u.clasificarIMC()));
        sb.append("Actividad: ").append(u.getNivel()).append('\n');
        sb.append("Objetivo: ").append(u.getObjetivo()).append("\n\n");
        sb.append(String.format(Locale.US, "TMB: %.0f kcal | Gasto total: %.0f kcal%n", u.calcularTMB(), u.calcularGastoTotal()));
        sb.append("--- Meta diaria ---\n");
        sb.append(String.format(Locale.US, "Calorias: %.0f kcal%n", m.getCalorias()));
        sb.append(String.format(Locale.US, "Proteina: %.1f g | Carbohidratos: %.1f g | Grasas: %.1f g",
                m.getProteinaG(), m.getCarbosG(), m.getGrasasG()));
        salida.setText(sb.toString());
    }

    /** Llena los campos con el perfil guardado (si existe). */
    public void refrescar() {
        if (!gestor.existePerfil()) {
            return;
        }
        Usuario u = gestor.consultarPerfil();
        nombre.setText(u.getNombre());
        nombre.setEditable(false);
        sexo.setSelectedItem(u.getSexo());
        sexo.setEnabled(false);
        edad.setText(String.valueOf(u.getEdad()));
        estatura.setText(String.valueOf(u.getEstaturaCm()));
        peso.setText(String.valueOf(u.getPesoKg()));
        nivel.setSelectedItem(u.getNivel());
        objetivo.setSelectedItem(u.getObjetivo());
        mostrar();
    }
}
