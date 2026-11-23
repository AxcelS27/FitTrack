package negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import modelo.Alimento;
import modelo.DiaNutricional;
import modelo.EstadoBalance;
import modelo.MetaNutricional;
import modelo.RegistroComida;
import modelo.TipoComida;
import modelo.Usuario;

/** Modulo 3: catalogo de alimentos, registro de comidas y balance diario. */
public class GestorNutricion {
    private final AlmacenDatos datos;

    public GestorNutricion(AlmacenDatos datos) {
        this.datos = datos;
    }

    public List<Alimento> listarAlimentos() {
        return Collections.unmodifiableList(datos.getAlimentos());
    }

    /** RF8: agrega un alimento personalizado al catalogo. */
    public Alimento agregarAlimento(String nombre, double kcal, double proteina, double carbos, double grasas) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del alimento es obligatorio.");
        }
        for (Alimento a : datos.getAlimentos()) {
            if (a.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new IllegalArgumentException("Ya existe un alimento llamado '" + a.getNombre() + "'.");
            }
        }
        Alimento alimento = new Alimento(nombre, kcal, proteina, carbos, grasas);
        datos.getAlimentos().add(alimento);
        return alimento;
    }

    public List<Alimento> buscarAlimentos(String texto) throws ElementoNoEncontradoException {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        List<Alimento> resultado = new ArrayList<>();
        for (Alimento a : datos.getAlimentos()) {
            if (a.getNombre().toLowerCase().contains(filtro)) {
                resultado.add(a);
            }
        }
        if (resultado.isEmpty()) {
            throw new ElementoNoEncontradoException("No se encontraron alimentos con '" + texto + "'.");
        }
        return resultado;
    }

    /** RF6: registra una porcion de alimento en un dia (crea el dia si no existe). */
    public RegistroComida registrarComida(LocalDate fecha, TipoComida tipo, Alimento alimento, double gramos) {
        Usuario usuario = datos.exigirUsuario();
        validarFecha(fecha);
        RegistroComida registro = new RegistroComida(tipo, alimento, gramos);
        DiaNutricional dia = usuario.buscarDia(fecha);
        if (dia == null) {
            dia = new DiaNutricional(fecha);
            usuario.agregarDia(dia);
        }
        dia.agregarRegistro(registro);
        return registro;
    }

    public DiaNutricional obtenerDia(LocalDate fecha) throws ElementoNoEncontradoException {
        DiaNutricional dia = datos.exigirUsuario().buscarDia(fecha);
        if (dia == null) {
            throw new ElementoNoEncontradoException("No hay comidas registradas el " + fecha + ".");
        }
        return dia;
    }

    public void eliminarRegistro(LocalDate fecha, int indice) throws ElementoNoEncontradoException {
        DiaNutricional dia = obtenerDia(fecha);
        dia.eliminarRegistro(indice);
    }

    /**
     * RF7 (regla de negocio): compara lo consumido en el dia contra la meta y da el estado
     * (deficit / en meta / exceso) con un consejo sobre la proteina.
     */
    public String generarBalance(LocalDate fecha) throws ElementoNoEncontradoException {
        Usuario usuario = datos.exigirUsuario();
        DiaNutricional dia = obtenerDia(fecha);
        MetaNutricional meta = CalculadoraNutricional.calcular(usuario);
        EstadoBalance estado = dia.evaluar(meta);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "Balance del %s%n", fecha));
        sb.append(String.format(Locale.US, "  Calorias:      %7.0f / %.0f kcal (restan %.0f)%n",
                dia.calcularCalorias(), meta.getCalorias(), meta.getCalorias() - dia.calcularCalorias()));
        sb.append(String.format(Locale.US, "  Proteina:      %7.1f / %.1f g%n",
                dia.calcularProteina(), meta.getProteinaG()));
        sb.append(String.format(Locale.US, "  Carbohidratos: %7.1f / %.1f g%n",
                dia.calcularCarbos(), meta.getCarbosG()));
        sb.append(String.format(Locale.US, "  Grasas:        %7.1f / %.1f g%n",
                dia.calcularGrasas(), meta.getGrasasG()));
        sb.append("  Estado: ").append(estado);
        if (dia.calcularProteina() < meta.getProteinaG() * 0.9) {
            sb.append(String.format(Locale.US, "%n  Consejo: te faltan %.0f g de proteina para tu meta.",
                    meta.getProteinaG() - dia.calcularProteina()));
        }
        return sb.toString();
    }

    private void validarFecha(LocalDate fecha) {
        if (fecha == null || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura.");
        }
    }
}
