package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.PedidoControlador;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Pantalla 1: Agregar pedido de cliente. Responsable: Integrante 2.
 * Debe tener: buscar cliente, agregar uniformes, cotización, abono,
 * foto del diseño, fecha de entrega automática y botón Confirmar.
 */
public class AgregarPedidoVista {

    private final PedidoControlador controlador = new PedidoControlador();
    private final VBox raiz = new VBox(10);

    public AgregarPedidoVista() {
        raiz.setPadding(new Insets(20));
        // TODO Integrante 2: armar el formulario según el prototipo de Figma
        raiz.getChildren().add(new Label("Agregar pedido de cliente (en construcción). Pedidos guardados: "
                + controlador.listarPedidos().size()));
    }

    public Parent getVista() {
        return raiz;
    }
}
