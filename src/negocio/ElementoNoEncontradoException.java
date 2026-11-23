package negocio;

/** Se lanza cuando una busqueda no encuentra el elemento pedido (alimento, dia, registro...). */
public class ElementoNoEncontradoException extends Exception {
    private static final long serialVersionUID = 1L;

    public ElementoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
