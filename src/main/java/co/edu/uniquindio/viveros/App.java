package co.edu.uniquindio.viveros;

import co.edu.uniquindio.viveros.vista.AgregarPedidoVista;
import co.edu.uniquindio.viveros.vista.ClientesVista;
import co.edu.uniquindio.viveros.vista.PedidosVista;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Ventana principal: barra de la marca arriba y las pantallas en pestañas. */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        PedidosVista pedidosVista = new PedidosVista();

        Tab pedido = new Tab("Agregar pedido", new AgregarPedidoVista().getVista());
        Tab pedidos = new Tab("Pedidos", pedidosVista.getVista());
        Tab clientes = new Tab("Clientes", new ClientesVista().getVista());

        TabPane pestanas = new TabPane(pedido, pedidos, clientes);
        pestanas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Cada vez que se abre la pestaña Pedidos se recarga, así aparecen los pedidos recién confirmados
        pestanas.getSelectionModel().selectedItemProperty().addListener((o, antes, ahora) -> {
            if (ahora == pedidos) {
                pedidosVista.refrescar();
            }
        });

        BorderPane raiz = new BorderPane(pestanas);
        raiz.setTop(barraMarca());

        Scene escena = new Scene(raiz, 1250, 820);
        escena.getStylesheets().add(App.class.getResource("/estilos.css").toExternalForm());

        stage.setTitle("Viveros Ropa Deportiva");
        stage.setMinWidth(1050);
        stage.setMinHeight(650);
        stage.setScene(escena);
        stage.show();
    }

    /** Barra verde superior con el nombre de la empresa y la fecha de hoy. */
    private HBox barraMarca() {
        Label logo = new Label("V");
        logo.getStyleClass().add("marca-logo");

        Label nombre = new Label("Viveros Ropa Deportiva");
        nombre.getStyleClass().add("marca-titulo");
        Label lema = new Label("Uniformes deportivos personalizados");
        lema.getStyleClass().add("marca-sub");
        VBox textos = new VBox(0, nombre, lema);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy",
                Locale.forLanguageTag("es-CO")));
        Label fecha = new Label(hoy.substring(0, 1).toUpperCase() + hoy.substring(1));
        fecha.getStyleClass().add("marca-fecha");

        HBox barra = new HBox(12, logo, textos, espacio, fecha);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.getStyleClass().add("barra-marca");
        return barra;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
