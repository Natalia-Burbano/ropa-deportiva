package co.edu.uniquindio.viveros.controlador;

import java.util.Map;

/**
 * Se lanza cuando un pedido no cumple las reglas de negocio.
 * Trae los errores por campo para mostrarlos junto a cada campo en la pantalla.
 */
public class ReglaNegocioException extends Exception {

    private final Map<String, String> errores;

    public ReglaNegocioException(Map<String, String> errores) {
        super(String.join(" ", errores.values()));
        this.errores = errores;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
