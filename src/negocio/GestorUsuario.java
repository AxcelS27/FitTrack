package negocio;

import modelo.MetaNutricional;
import modelo.NivelActividad;
import modelo.Objetivo;
import modelo.Sexo;
import modelo.Usuario;

/** Modulo 1: perfil del usuario y meta nutricional. */
public class GestorUsuario {
    private final AlmacenDatos datos;

    public GestorUsuario(AlmacenDatos datos) {
        this.datos = datos;
    }

    public boolean existePerfil() {
        return datos.hayUsuario();
    }

    /** RF1: registra el perfil. Solo se permite un perfil por instalacion. */
    public Usuario registrarPerfil(String nombre, Sexo sexo, int edad, double estaturaCm,
                                   double pesoKg, NivelActividad nivel, Objetivo objetivo) {
        if (datos.hayUsuario()) {
            throw new IllegalStateException("Ya existe un perfil. Usa la opcion de actualizar datos.");
        }
        Usuario usuario = new Usuario(nombre, sexo, edad, estaturaCm, pesoKg, nivel, objetivo);
        datos.setUsuario(usuario);
        return usuario;
    }

    /** RF1: actualiza los datos cambiantes del perfil. */
    public Usuario actualizarDatos(int edad, double estaturaCm, double pesoKg,
                                   NivelActividad nivel, Objetivo objetivo) {
        Usuario usuario = datos.exigirUsuario();
        usuario.setEdad(edad);
        usuario.setEstaturaCm(estaturaCm);
        usuario.setPesoKg(pesoKg);
        usuario.setNivel(nivel);
        usuario.setObjetivo(objetivo);
        return usuario;
    }

    public Usuario consultarPerfil() {
        return datos.exigirUsuario();
    }

    /** RF2: calcula la meta diaria de calorias y macros segun el perfil y el objetivo. */
    public MetaNutricional calcularMeta() {
        return CalculadoraNutricional.calcular(datos.exigirUsuario());
    }
}
