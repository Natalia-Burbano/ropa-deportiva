package co.edu.uniquindio.viveros;

import co.edu.uniquindio.viveros.vista.AgregarPedidoVista;
import co.edu.uniquindio.viveros.vista.ClientesVista;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/** Ventana principal con las dos pantallas del MVP en pestañas. */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        Tab pedido = new Tab("Agregar pedido", new AgregarPedidoVista().getVista());
        Tab clientes = new Tab("Clientes", new ClientesVista().getVista());

        TabPane pestanas = new TabPane(pedido, clientes);
        pestanas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene escena = new Scene(pestanas, 1150, 800);
        escena.getStylesheets().add(App.class.getResource("/estilos.css").toExternalForm());

        stage.setTitle("Viveros Ropa Deportiva");
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
