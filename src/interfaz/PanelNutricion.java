package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelo.Alimento;
import modelo.DiaNutricional;
import modelo.MetaNutricional;
import modelo.RegistroComida;
import modelo.TipoComida;
import negocio.ElementoNoEncontradoException;
import negocio.GestorNutricion;
import negocio.GestorUsuario;

/** Pestana del modulo 3: alimentacion. */
public class PanelNutricion extends JPanel {
    private final GestorNutricion gestor;
    private final GestorUsuario gestorUsuario;

    private final JTextField tfBuscar = new JTextField(12);
    private final JComboBox<Alimento> cbAlimento = new JComboBox<>();
    private final JTextField tfGramos = new JTextField(6);
    private final JComboBox<TipoComida> cbTipo = new JComboBox<>(TipoComida.values());
    private final JTextField tfFecha = new JTextField(LocalDate.now().toString(), 10);
    private final DefaultTableModel modeloDia =
            new DefaultTableModel(new Object[] {"Tipo", "Alimento", "Gramos", "kcal", "Prot (g)", "Carb (g)", "Grasa (g)"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
    private final JTable tabla = new JTable(modeloDia);
    private final JTextArea salida = new JTextArea();

    public PanelNutricion(GestorNutricion gestor, GestorUsuario gestorUsuario) {
        this.gestor = gestor;
        this.gestorUsuario = gestorUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnBuscar = new JButton("Buscar");
        JButton btnRegistrar = new JButton("Registrar comida");
        JButton btnNuevo = new JButton("Nuevo alimento");
        btnBuscar.addActionListener(e -> Utilidades.ejecutar(this, this::buscar));
        btnRegistrar.addActionListener(e -> Utilidades.ejecutar(this, this::registrar));
        btnNuevo.addActionListener(e -> Utilidades.ejecutar(this, this::nuevoAlimento));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Buscar alimento:"));
        fila1.add(tfBuscar);
        fila1.add(btnBuscar);
        fila1.add(cbAlimento);
        fila1.add(btnNuevo);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Gramos:"));
        fila2.add(tfGramos);
        fila2.add(new JLabel("Tipo:"));
        fila2.add(cbTipo);
        fila2.add(new JLabel("Fecha:"));
        fila2.add(tfFecha);
        fila2.add(btnRegistrar);
        JPanel registro = new JPanel(new BorderLayout());
        registro.setBorder(BorderFactory.createTitledBorder("Registrar comida"));
        registro.add(fila1, BorderLayout.NORTH);
        registro.add(fila2, BorderLayout.SOUTH);

        JButton btnDia = new JButton("Ver dia");
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnBalance = new JButton("Balance del dia");
        JButton btnMeta = new JButton("Mi meta diaria");
        btnDia.addActionListener(e -> Utilidades.ejecutar(this, this::cargarDia));
        btnEliminar.addActionListener(e -> Utilidades.ejecutar(this, this::eliminar));
        btnBalance.addActionListener(e -> Utilidades.ejecutar(this, this::balance));
        btnMeta.addActionListener(e -> Utilidades.ejecutar(this, this::meta));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnDia);
        acciones.add(btnEliminar);
        acciones.add(btnBalance);
        acciones.add(btnMeta);

        tabla.setFillsViewportHeight(true);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createTitledBorder("Comidas del dia"));
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        centro.add(acciones, BorderLayout.SOUTH);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(registro, BorderLayout.NORTH);
        arriba.add(centro, BorderLayout.CENTER);

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollSalida = new JScrollPane(salida);
        scrollSalida.setPreferredSize(new java.awt.Dimension(400, 140));
        add(arriba, BorderLayout.CENTER);
        add(scrollSalida, BorderLayout.SOUTH);
    }

    private void buscar() throws ElementoNoEncontradoException {
        List<Alimento> encontrados = gestor.buscarAlimentos(tfBuscar.getText());
        cbAlimento.removeAllItems();
        for (Alimento a : encontrados) {
            cbAlimento.addItem(a);
        }
        salida.setText(encontrados.size() + " alimento(s) encontrado(s). Elige uno de la lista.");
    }

    private void registrar() throws ElementoNoEncontradoException {
        gestorUsuario.consultarPerfil();
        Alimento alimento = (Alimento) cbAlimento.getSelectedItem();
        if (alimento == null) {
            throw new IllegalArgumentException("Busca y elige un alimento primero.");
        }
        double gramos = Utilidades.decimal(tfGramos, "Gramos", 1, 5000);
        LocalDate fecha = Utilidades.fecha(tfFecha);
        RegistroComida r = gestor.registrarComida(fecha, (TipoComida) cbTipo.getSelectedItem(), alimento, gramos);
        salida.setText("Registrado: " + r);
        tfGramos.setText("");
        cargarDia();
    }

    private void cargarDia() throws ElementoNoEncontradoException {
        modeloDia.setRowCount(0);
        LocalDate fecha = Utilidades.fecha(tfFecha);
        try {
            DiaNutricional dia = gestor.obtenerDia(fecha);
            for (RegistroComida r : dia.getRegistros()) {
                modeloDia.addRow(new Object[] {r.getTipo(), r.getAlimento().getNombre(),
                        String.format(Locale.US, "%.0f", r.getGramos()),
                        String.format(Locale.US, "%.0f", r.calcularCalorias()),
                        String.format(Locale.US, "%.1f", r.calcularProteina()),
                        String.format(Locale.US, "%.1f", r.calcularCarbos()),
                        String.format(Locale.US, "%.1f", r.calcularGrasas())});
            }
        } catch (ElementoNoEncontradoException e) {
            salida.setText(e.getMessage());
        }
    }

    private void eliminar() throws ElementoNoEncontradoException {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Selecciona un registro de la tabla.");
        }
        gestor.eliminarRegistro(Utilidades.fecha(tfFecha), fila);
        salida.setText("Registro eliminado.");
        cargarDia();
    }

    private void balance() throws ElementoNoEncontradoException {
        salida.setText(gestor.generarBalance(Utilidades.fecha(tfFecha)));
    }

    private void meta() {
        MetaNutricional m = gestorUsuario.calcularMeta();
        salida.setText(String.format(Locale.US, "Meta diaria: %.0f kcal | P %.1f g | C %.1f g | G %.1f g",
                m.getCalorias(), m.getProteinaG(), m.getCarbosG(), m.getGrasasG()));
    }

    private void nuevoAlimento() {
        JTextField nombre = new JTextField(15);
        JTextField kcal = new JTextField();
        JTextField prot = new JTextField();
        JTextField carb = new JTextField();
        JTextField gras = new JTextField();
        if (Utilidades.confirmar(this, "Nuevo alimento (valores por 100 g)", Utilidades.formulario(
                "Nombre:", nombre, "Calorias (0-900):", kcal, "Proteina (g):", prot,
                "Carbohidratos (g):", carb, "Grasas (g):", gras))) {
            Alimento a = gestor.agregarAlimento(Utilidades.texto(nombre, "Nombre"),
                    Utilidades.decimal(kcal, "Calorias", 0, 900),
                    Utilidades.decimal(prot, "Proteina", 0, 100),
                    Utilidades.decimal(carb, "Carbohidratos", 0, 100),
                    Utilidades.decimal(gras, "Grasas", 0, 100));
            salida.setText("Alimento agregado: " + a);
        }
    }

    /** Recarga la tabla del dia seleccionado. */
    public void refrescar() {
        if (!gestorUsuario.existePerfil()) {
            return;
        }
        try {
            cargarDia();
        } catch (ElementoNoEncontradoException | IllegalArgumentException e) {
            modeloDia.setRowCount(0);
        }
    }
}
