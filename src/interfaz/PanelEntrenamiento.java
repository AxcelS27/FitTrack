package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelo.Ejercicio;
import modelo.EjercicioFuerza;
import modelo.GrupoMuscular;
import modelo.Rutina;
import modelo.SerieRealizada;
import modelo.SesionEntrenamiento;
import negocio.GestorEntrenamiento;
import negocio.GestorUsuario;

/** Pestana del modulo 2: rutinas, sesiones y progresion. */
public class PanelEntrenamiento extends JPanel {
    private final GestorEntrenamiento gestor;
    private final GestorUsuario gestorUsuario;

    private final JComboBox<Rutina> cbRutina = new JComboBox<>();
    private final JComboBox<EjercicioFuerza> cbEjercicio = new JComboBox<>();
    private final JTextField tfFecha = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField tfDuracion = new JTextField(5);
    private final JTextField tfReps = new JTextField(4);
    private final JTextField tfPeso = new JTextField(5);
    private final DefaultTableModel modeloSeries =
            new DefaultTableModel(new Object[] {"Ejercicio", "Repeticiones", "Peso (kg)"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
    private final List<SerieRealizada> pendientes = new ArrayList<>();
    private final JTextArea salida = new JTextArea();

    public PanelEntrenamiento(GestorEntrenamiento gestor, GestorUsuario gestorUsuario) {
        this.gestor = gestor;
        this.gestorUsuario = gestorUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Gestion de rutinas y ejercicios ---
        JButton btnRutina = new JButton("Nueva rutina");
        JButton btnAgregar = new JButton("Agregar ejercicio a rutina");
        JButton btnEjercicio = new JButton("Nuevo ejercicio");
        JButton btnVer = new JButton("Ver rutinas");
        btnRutina.addActionListener(e -> Utilidades.ejecutar(this, this::nuevaRutina));
        btnAgregar.addActionListener(e -> Utilidades.ejecutar(this, this::agregarEjercicioARutina));
        btnEjercicio.addActionListener(e -> Utilidades.ejecutar(this, this::nuevoEjercicio));
        btnVer.addActionListener(e -> Utilidades.ejecutar(this, this::verRutinas));
        JPanel gestionRutinas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gestionRutinas.setBorder(BorderFactory.createTitledBorder("Rutinas y ejercicios"));
        gestionRutinas.add(btnRutina);
        gestionRutinas.add(btnAgregar);
        gestionRutinas.add(btnEjercicio);
        gestionRutinas.add(btnVer);

        // --- Registro de sesion ---
        cbRutina.addActionListener(e -> cargarEjerciciosDeRutina());
        JButton btnSerie = new JButton("Agregar serie");
        JButton btnQuitar = new JButton("Quitar ultima serie");
        JButton btnRegistrar = new JButton("Registrar sesion");
        btnSerie.addActionListener(e -> Utilidades.ejecutar(this, this::agregarSerie));
        btnQuitar.addActionListener(e -> quitarUltimaSerie());
        btnRegistrar.addActionListener(e -> Utilidades.ejecutar(this, this::registrarSesion));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new javax.swing.JLabel("Rutina:"));
        fila1.add(cbRutina);
        fila1.add(new javax.swing.JLabel("Fecha:"));
        fila1.add(tfFecha);
        fila1.add(new javax.swing.JLabel("Duracion (min):"));
        fila1.add(tfDuracion);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new javax.swing.JLabel("Ejercicio:"));
        fila2.add(cbEjercicio);
        fila2.add(new javax.swing.JLabel("Reps:"));
        fila2.add(tfReps);
        fila2.add(new javax.swing.JLabel("Peso kg:"));
        fila2.add(tfPeso);
        fila2.add(btnSerie);
        fila2.add(btnQuitar);
        JPanel sesionArriba = new JPanel(new BorderLayout());
        sesionArriba.add(fila1, BorderLayout.NORTH);
        sesionArriba.add(fila2, BorderLayout.SOUTH);

        JTable tabla = new JTable(modeloSeries);
        tabla.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new java.awt.Dimension(400, 120));

        JButton btnProgresion = new JButton("Progresion y estancamiento");
        JButton btnHistorial = new JButton("Historial de sesiones");
        btnProgresion.addActionListener(e -> Utilidades.ejecutar(this, this::verProgresion));
        btnHistorial.addActionListener(e -> Utilidades.ejecutar(this, this::verHistorial));
        JPanel sesionAbajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sesionAbajo.add(btnRegistrar);
        sesionAbajo.add(btnProgresion);
        sesionAbajo.add(btnHistorial);

        JPanel sesion = new JPanel(new BorderLayout());
        sesion.setBorder(BorderFactory.createTitledBorder("Registrar sesion de entrenamiento"));
        sesion.add(sesionArriba, BorderLayout.NORTH);
        sesion.add(scrollTabla, BorderLayout.CENTER);
        sesion.add(sesionAbajo, BorderLayout.SOUTH);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(gestionRutinas, BorderLayout.NORTH);
        arriba.add(sesion, BorderLayout.CENTER);

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollSalida = new JScrollPane(salida);
        scrollSalida.setPreferredSize(new java.awt.Dimension(400, 170));
        add(arriba, BorderLayout.CENTER);
        add(scrollSalida, BorderLayout.SOUTH);
    }

    // ---------- Rutinas y ejercicios ----------

    private void nuevaRutina() {
        gestorUsuario.consultarPerfil();
        JTextField nombre = new JTextField(15);
        JTextField series = new JTextField("3");
        JTextField min = new JTextField("8");
        JTextField max = new JTextField("12");
        if (Utilidades.confirmar(this, "Nueva rutina", Utilidades.formulario(
                "Nombre:", nombre, "Series (1-10):", series,
                "Repeticiones minimas:", min, "Repeticiones maximas:", max))) {
            Rutina r = gestor.crearRutina(Utilidades.texto(nombre, "Nombre"),
                    Utilidades.entero(series, "Series", 1, 10),
                    Utilidades.entero(min, "Repeticiones minimas", 1, 30),
                    Utilidades.entero(max, "Repeticiones maximas", 1, 50));
            refrescar();
            cbRutina.setSelectedItem(r);
            salida.setText("Rutina creada: " + r);
        }
    }

    private void agregarEjercicioARutina() {
        Rutina rutina = (Rutina) cbRutina.getSelectedItem();
        if (rutina == null) {
            throw new IllegalStateException("Primero crea y selecciona una rutina.");
        }
        JComboBox<Ejercicio> combo = new JComboBox<>();
        for (Ejercicio e : gestor.listarEjercicios()) {
            combo.addItem(e);
        }
        if (Utilidades.confirmar(this, "Agregar a '" + rutina.getNombre() + "'",
                Utilidades.formulario("Ejercicio:", combo))) {
            Ejercicio elegido = (Ejercicio) combo.getSelectedItem();
            gestor.agregarEjercicioARutina(rutina, elegido);
            cargarEjerciciosDeRutina();
            salida.setText("Agregado '" + elegido.getNombre() + "' a la rutina '" + rutina.getNombre() + "'.");
        }
    }

    private void nuevoEjercicio() {
        JComboBox<String> tipo = new JComboBox<>(new String[] {"Fuerza", "Cardio"});
        JTextField nombre = new JTextField(15);
        JComboBox<GrupoMuscular> grupo = new JComboBox<>();
        for (GrupoMuscular g : GrupoMuscular.values()) {
            if (g != GrupoMuscular.CARDIO) {
                grupo.addItem(g);
            }
        }
        JTextField valor = new JTextField("20");
        if (Utilidades.confirmar(this, "Nuevo ejercicio", Utilidades.formulario(
                "Tipo:", tipo, "Nombre:", nombre, "Grupo (solo fuerza):", grupo,
                "Peso inicial kg (fuerza) o MET (cardio):", valor))) {
            String n = Utilidades.texto(nombre, "Nombre");
            if (tipo.getSelectedIndex() == 0) {
                gestor.crearEjercicioFuerza(n, (GrupoMuscular) grupo.getSelectedItem(),
                        Utilidades.decimal(valor, "Peso inicial", 0, 500));
            } else {
                gestor.crearEjercicioCardio(n, Utilidades.decimal(valor, "MET", 1, 20));
            }
            salida.setText("Ejercicio creado: " + n);
        }
    }

    private void verRutinas() {
        List<Rutina> rutinas = gestor.listarRutinas();
        if (rutinas.isEmpty()) {
            salida.setText("Aun no tienes rutinas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Rutina r : rutinas) {
            sb.append("- ").append(r).append('\n');
            for (Ejercicio e : r.getEjercicios()) {
                sb.append("    * ").append(e);
                if (e instanceof EjercicioFuerza) {
                    sb.append(String.format(Locale.US, " (peso actual: %.1f kg)", ((EjercicioFuerza) e).getPesoActualKg()));
                }
                sb.append('\n');
            }
        }
        salida.setText(sb.toString());
    }

    // ---------- Sesion ----------

    private void cargarEjerciciosDeRutina() {
        cbEjercicio.removeAllItems();
        Rutina r = (Rutina) cbRutina.getSelectedItem();
        if (r != null) {
            for (Ejercicio e : r.getEjercicios()) {
                if (e instanceof EjercicioFuerza) {
                    cbEjercicio.addItem((EjercicioFuerza) e);
                }
            }
        }
        pendientes.clear();
        modeloSeries.setRowCount(0);
    }

    private void agregarSerie() {
        EjercicioFuerza e = (EjercicioFuerza) cbEjercicio.getSelectedItem();
        if (e == null) {
            throw new IllegalStateException("La rutina no tiene ejercicios de fuerza. Agregale uno primero.");
        }
        int reps = Utilidades.entero(tfReps, "Repeticiones", 1, 100);
        double peso = Utilidades.decimal(tfPeso, "Peso", 0, 500);
        pendientes.add(new SerieRealizada(e, reps, peso));
        modeloSeries.addRow(new Object[] {e.getNombre(), reps, peso});
        tfReps.setText("");
    }

    private void quitarUltimaSerie() {
        if (!pendientes.isEmpty()) {
            pendientes.remove(pendientes.size() - 1);
            modeloSeries.removeRow(modeloSeries.getRowCount() - 1);
        }
    }

    private void registrarSesion() {
        Rutina rutina = (Rutina) cbRutina.getSelectedItem();
        LocalDate fecha = Utilidades.fecha(tfFecha);
        int duracion = Utilidades.entero(tfDuracion, "Duracion", 5, 300);
        SesionEntrenamiento s = gestor.registrarSesion(fecha, rutina, duracion, new ArrayList<>(pendientes));
        double pesoUsuario = gestorUsuario.consultarPerfil().getPesoKg();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "Sesion guardada. Volumen total: %.0f kg | Gasto estimado: %.0f kcal%n",
                s.calcularVolumenTotal(), s.calcularGastoCalorico(pesoUsuario)));
        sb.append("--- Progresion ---\n");
        for (EjercicioFuerza e : gestor.ejerciciosEntrenados(s)) {
            sb.append(gestor.evaluarProgresion(e)).append('\n');
            if (gestor.detectarEstancamiento(e)) {
                sb.append("  [!] Estancamiento detectado en ").append(e.getNombre())
                        .append(": considera descansar, bajar 10% la carga o cambiar el estimulo.\n");
            }
        }
        salida.setText(sb.toString());
        pendientes.clear();
        modeloSeries.setRowCount(0);
    }

    private void verProgresion() {
        List<EjercicioFuerza> lista = gestor.ejerciciosConHistorial();
        if (lista.isEmpty()) {
            salida.setText("Aun no hay sesiones registradas.");
            return;
        }
        JComboBox<EjercicioFuerza> combo = new JComboBox<>(lista.toArray(new EjercicioFuerza[0]));
        if (Utilidades.confirmar(this, "Progresion", Utilidades.formulario("Ejercicio:", combo))) {
            EjercicioFuerza e = (EjercicioFuerza) combo.getSelectedItem();
            salida.setText(gestor.evaluarProgresion(e) + "\n"
                    + (gestor.detectarEstancamiento(e)
                    ? "Estado: ESTANCADO en las ultimas 3 sesiones."
                    : "Estado: sin estancamiento."));
        }
    }

    private void verHistorial() {
        List<SesionEntrenamiento> sesiones = gestor.listarSesiones();
        if (sesiones.isEmpty()) {
            salida.setText("Aun no hay sesiones registradas.");
            return;
        }
        double pesoUsuario = gestorUsuario.consultarPerfil().getPesoKg();
        StringBuilder sb = new StringBuilder();
        for (SesionEntrenamiento s : sesiones) {
            sb.append(String.format(Locale.US, "%s | %s | %d min | volumen %.0f kg | %.0f kcal%n",
                    s.getFecha(), s.getRutina().getNombre(), s.getDuracionMin(),
                    s.calcularVolumenTotal(), s.calcularGastoCalorico(pesoUsuario)));
        }
        salida.setText(sb.toString());
    }

    /** Recarga la lista de rutinas desde los datos. */
    public void refrescar() {
        Object seleccion = cbRutina.getSelectedItem();
        cbRutina.removeAllItems();
        if (gestorUsuario.existePerfil()) {
            for (Rutina r : gestor.listarRutinas()) {
                cbRutina.addItem(r);
            }
            if (seleccion != null) {
                cbRutina.setSelectedItem(seleccion);
            }
        }
        cargarEjerciciosDeRutina();
    }
}
