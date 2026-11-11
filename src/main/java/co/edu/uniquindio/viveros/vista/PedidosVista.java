package co.edu.uniquindio.viveros.vista;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Pantalla 3: lista de pedidos registrados.
 * El diseño está en resources/PedidosVista.fxml (se edita con Scene Builder)
 * y la lógica en PedidosVistaController.
 */
public class PedidosVista {

    private final Parent raiz;
    private final PedidosVistaController controlador;

    public PedidosVista() {
        try {
            FXMLLoader loader = new FXMLLoader(PedidosVista.class.getResource("/PedidosVista.fxml"));
            raiz = loader.load();
            controlador = loader.getController();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar PedidosVista.fxml", e);
        }
    }

    public Parent getVista() {
        return raiz;
    }

    /** Vuelve a leer los pedidos (por ejemplo, después de confirmar uno nuevo). */
    public void refrescar() {
        controlador.refrescar();
    }
}
