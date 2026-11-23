package negocio;

import modelo.MetaNutricional;
import modelo.Usuario;

/** Regla de negocio: calcula la meta diaria de calorias y macronutrientes de un usuario. */
public class CalculadoraNutricional {
    private static final double GRASA_G_POR_KG = 0.8;
    private static final double KCAL_POR_G_PROTEINA = 4;
    private static final double KCAL_POR_G_CARBO = 4;
    private static final double KCAL_POR_G_GRASA = 9;

    private CalculadoraNutricional() {
    }

    /**
     * Calorias = gasto total x (1 + ajuste del objetivo).
     * Proteina = peso x g/kg del objetivo. Grasas = 0.8 g/kg. Carbohidratos = el resto de calorias.
     */
    public static MetaNutricional calcular(Usuario usuario) {
        double calorias = usuario.calcularGastoTotal() * (1 + usuario.getObjetivo().getAjusteCalorico());
        double proteina = usuario.getPesoKg() * usuario.getObjetivo().getProteinaPorKg();
        double grasas = usuario.getPesoKg() * GRASA_G_POR_KG;
        double kcalRestantes = calorias - proteina * KCAL_POR_G_PROTEINA - grasas * KCAL_POR_G_GRASA;
        double carbos = Math.max(0, kcalRestantes / KCAL_POR_G_CARBO);
        return new MetaNutricional(calorias, proteina, carbos, grasas);
    }
}
