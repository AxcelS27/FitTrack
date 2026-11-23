package interfaz;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelo.MedicionCorporal;
import negocio.GestorProgreso;
import negocio.GestorUsuario;

/** Pestana del modulo 4: progreso corporal. */
public class PanelProgreso extends JPanel {
    private final GestorProgreso gestor;
    private final GestorUsuario gestorUsuario;
    private final JTextField tfFecha = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField tfPeso = new JTextField(6);
    private final JTextField tfCintura = new JTextField(6);
    private final DefaultTableModel modelo =
            new DefaultTableModel(new Object[] {"Fecha", "Peso (kg)", "Cintura (cm)"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
    private final JTextArea salida = new JTextArea();

    public PanelProgreso(GestorProgreso gestor, GestorUsuario gestorUsuario) {
        this.gestor = gestor;
        this.gestorUsuario = gestorUsuario;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnRegistrar = new JButton("Registrar medicion");
        JButton btnReporte = new JButton("Generar reporte de progreso");
        btnRegistrar.addActionListener(e -> Utilidades.ejecutar(this, this::registrar));
        btnReporte.addActionListener(e -> Utilidades.ejecutar(this, this::reporte));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Nueva medicion"));
        form.add(new JLabel("Fecha:"));
        form.add(tfFecha);
        form.add(new JLabel("Peso kg:"));
        form.add(tfPeso);
        form.add(new JLabel("Cintura cm:"));
        form.add(tfCintura);
        form.add(btnRegistrar);
        form.add(btnReporte);

        JTable tabla = new JTable(modelo);
        tabla.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Historial de mediciones"));

        salida.setEditable(false);
        salida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollSalida = new JScrollPane(salida);
        scrollSalida.setPreferredSize(new java.awt.Dimension(400, 150));

        add(form, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(scrollSalida, BorderLayout.SOUTH);
    }

    private void registrar() {
        gestorUsuario.consultarPerfil();
        gestor.registrarMedicion(Utilidades.fecha(tfFecha),
                Utilidades.decimal(tfPeso, "Peso", 30, 300),
                Utilidades.decimal(tfCintura, "Cintura", 40, 200));
        salida.setText("Medicion registrada.");
        cargarTabla();
    }

    private void reporte() {
        salida.setText(gestor.generarReporte());
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        for (MedicionCorporal m : gestor.listarMediciones()) {
            modelo.addRow(new Object[] {m.getFecha(),
                    String.format(Locale.US, "%.1f", m.getPesoKg()),
                    String.format(Locale.US, "%.1f", m.getCinturaCm())});
        }
    }

    /** Recarga la tabla de mediciones. */
    public void refrescar() {
        if (gestorUsuario.existePerfil()) {
            cargarTabla();
        }
    }
}
