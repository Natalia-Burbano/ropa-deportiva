package co.edu.uniquindio.viveros.vista;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Pantalla 2: CRUD de Cliente. Responsable: Integrante 1.
 * El diseño está en resources/ClientesVista.fxml (se edita con Scene Builder)
 * y la lógica de la pantalla en ClientesVistaController.
 * Se deja esta clase para que App siga igual: new ClientesVista().getVista().
 */
public class ClientesVista {

    private final Parent raiz;

    public ClientesVista() {
        try {
            FXMLLoader loader = new FXMLLoader(ClientesVista.class.getResource("/ClientesVista.fxml"));
            raiz = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar ClientesVista.fxml", e);
        }
    }

    public Parent getVista() {
        return raiz;
    }
}
