package modelo;

import java.io.Serializable;

/** Meta diaria de calorias y macronutrientes calculada para un usuario. */
public class MetaNutricional implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double calorias;
    private final double proteinaG;
    private final double carbosG;
    private final double grasasG;

    public MetaNutricional(double calorias, double proteinaG, double carbosG, double grasasG) {
        this.calorias = calorias;
        this.proteinaG = proteinaG;
        this.carbosG = carbosG;
        this.grasasG = grasasG;
    }

    public double getCalorias() {
        return calorias;
    }

    public double getProteinaG() {
        return proteinaG;
    }

    public double getCarbosG() {
        return carbosG;
    }

    public double getGrasasG() {
        return grasasG;
    }
}
