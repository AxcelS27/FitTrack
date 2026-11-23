package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Persona que usa FitTrack. Concentra su perfil fisico y su historial
 * (rutinas, sesiones, dias de alimentacion y mediciones corporales).
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private Sexo sexo;
    private int edad;
    private double estaturaCm;
    private double pesoKg;
    private NivelActividad nivel;
    private Objetivo objetivo;

    private final List<Rutina> rutinas = new ArrayList<>();
    private final List<SesionEntrenamiento> sesiones = new ArrayList<>();
    private final List<DiaNutricional> dias = new ArrayList<>();
    private final List<MedicionCorporal> mediciones = new ArrayList<>();

    public Usuario(String nombre, Sexo sexo, int edad, double estaturaCm, double pesoKg,
                   NivelActividad nivel, Objetivo objetivo) {
        setNombre(nombre);
        setSexo(sexo);
        setEdad(edad);
        setEstaturaCm(estaturaCm);
        setPesoKg(pesoKg);
        setNivel(nivel);
        setObjetivo(objetivo);
    }

    // ---------- Getters y setters con validacion (encapsulamiento) ----------

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        if (sexo == null) {
            throw new IllegalArgumentException("El sexo es obligatorio.");
        }
        this.sexo = sexo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < 14 || edad > 100) {
            throw new IllegalArgumentException("La edad debe estar entre 14 y 100 anios.");
        }
        this.edad = edad;
    }

    public double getEstaturaCm() {
        return estaturaCm;
    }

    public void setEstaturaCm(double estaturaCm) {
        if (estaturaCm < 120 || estaturaCm > 230) {
            throw new IllegalArgumentException("La estatura debe estar entre 120 y 230 cm.");
        }
        this.estaturaCm = estaturaCm;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        if (pesoKg < 30 || pesoKg > 300) {
            throw new IllegalArgumentException("El peso debe estar entre 30 y 300 kg.");
        }
        this.pesoKg = pesoKg;
    }

    public NivelActividad getNivel() {
        return nivel;
    }

    public void setNivel(NivelActividad nivel) {
        if (nivel == null) {
            throw new IllegalArgumentException("El nivel de actividad es obligatorio.");
        }
        this.nivel = nivel;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(Objetivo objetivo) {
        if (objetivo == null) {
            throw new IllegalArgumentException("El objetivo es obligatorio.");
        }
        this.objetivo = objetivo;
    }

    // ---------- Calculos del perfil ----------

    /** Metabolismo basal con la ecuacion de Mifflin-St Jeor (kcal/dia). */
    public double calcularTMB() {
        double base = 10 * pesoKg + 6.25 * estaturaCm - 5 * edad;
        return sexo == Sexo.MASCULINO ? base + 5 : base - 161;
    }

    /** Gasto energetico total diario = TMB x factor de actividad. */
    public double calcularGastoTotal() {
        return calcularTMB() * nivel.getFactor();
    }

    public double calcularIMC() {
        double metros = estaturaCm / 100.0;
        return pesoKg / (metros * metros);
    }

    public String clasificarIMC() {
        double imc = calcularIMC();
        if (imc < 18.5) {
            return "Bajo peso";
        }
        if (imc < 25) {
            return "Peso normal";
        }
        if (imc < 30) {
            return "Sobrepeso";
        }
        return "Obesidad";
    }

    // ---------- Historial ----------

    public List<Rutina> getRutinas() {
        return Collections.unmodifiableList(rutinas);
    }

    public void agregarRutina(Rutina rutina) {
        if (rutina == null) {
            throw new IllegalArgumentException("La rutina no puede ser nula.");
        }
        rutinas.add(rutina);
    }

    public List<SesionEntrenamiento> getSesiones() {
        return Collections.unmodifiableList(sesiones);
    }

    public void agregarSesion(SesionEntrenamiento sesion) {
        if (sesion == null) {
            throw new IllegalArgumentException("La sesion no puede ser nula.");
        }
        sesiones.add(sesion);
    }

    public List<DiaNutricional> getDias() {
        return Collections.unmodifiableList(dias);
    }

    public void agregarDia(DiaNutricional dia) {
        if (dia == null) {
            throw new IllegalArgumentException("El dia no puede ser nulo.");
        }
        dias.add(dia);
    }

    public DiaNutricional buscarDia(LocalDate fecha) {
        for (DiaNutricional d : dias) {
            if (d.getFecha().equals(fecha)) {
                return d;
            }
        }
        return null;
    }

    /** Devuelve las mediciones ordenadas de la mas antigua a la mas reciente. */
    public List<MedicionCorporal> getMediciones() {
        return Collections.unmodifiableList(mediciones);
    }

    public void agregarMedicion(MedicionCorporal medicion) {
        if (medicion == null) {
            throw new IllegalArgumentException("La medicion no puede ser nula.");
        }
        mediciones.add(medicion);
        mediciones.sort(Comparator.comparing(MedicionCorporal::getFecha));
    }
}
