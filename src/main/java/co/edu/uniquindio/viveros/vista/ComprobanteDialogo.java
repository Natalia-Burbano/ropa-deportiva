package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.PedidoControlador;
import co.edu.uniquindio.viveros.controlador.ReglaNegocioException;
import co.edu.uniquindio.viveros.modelo.Abono;
import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.ComprobanteAbono;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.OrdenProduccion;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.util.Formato;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Ventana "Comprobante de abono" (formulario Diálogo del prototipo).
 * Sale al confirmar el pedido. Desde aquí se puede enviar el pedido a producción.
 */
public class ComprobanteDialogo extends Dialog<ButtonType> {

    public ComprobanteDialogo(PedidoControlador controlador, Pedido pedido) {
        setTitle("Pedido confirmado");
        setHeaderText("Pedido guardado correctamente");
        getDialogPane().getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());

        ButtonType enviar = new ButtonType("Enviar a producción", ButtonBar.ButtonData.OK_DONE);
        ButtonType cerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cerrar, enviar);

        List<Abono> abonos = pedido.getCredito().getAbonos();
        Abono abono = abonos.get(abonos.size() - 1);
        ComprobanteAbono comprobante = abono.getComprobante();
        Cliente cliente = controlador.buscarClientePorId(pedido.getIdCliente());
        Encargado encargado = controlador.buscarEncargadoPorId(pedido.getIdEncargado());

        GridPane tabla = new GridPane();
        tabla.setHgap(24);
        tabla.setVgap(8);
        String[][] filas = {
                {"Comprobante N.º", String.valueOf(comprobante.getIdComprobante())},
                {"Pedido N.º", String.valueOf(pedido.getIdPedido())},
                {"Cliente", cliente == null ? "" : cliente.getNombre()},
                {"Equipo", pedido.getNombreGrupo()},
                {"Fecha de emisión", Formato.fecha(comprobante.getFechaEmision())},
                {"Valor total", Formato.pesos(pedido.getValorTotal())},
                {"Valor abonado", Formato.pesos(comprobante.getValorAbonado())},
                {"Saldo pendiente", Formato.pesos(comprobante.getSaldoPendiente())},
                {"Fecha de entrega", Formato.fecha(pedido.getFechaEntregaEstimada())},
                {"Atendido por", encargado == null ? "" : encargado.getNombre()}
        };
        for (int i = 0; i < filas.length; i++) {
            Label nombre = new Label(filas[i][0]);
            nombre.getStyleClass().add("subtitulo");
            Label valor = new Label(filas[i][1]);
            valor.getStyleClass().add("etiqueta");
            tabla.add(nombre, 0, i);
            tabla.add(valor, 1, i);
        }

        Label titulo = new Label("VIVEROS ROPA DEPORTIVA – Comprobante de abono");
        titulo.getStyleClass().add("titulo-seccion");
        VBox contenido = new VBox(14, titulo, tabla);
        contenido.setPadding(new Insets(10));
        getDialogPane().setContent(contenido);

        // Enviar a producción: si no se puede, avisa y la ventana sigue abierta
        Button btnEnviar = (Button) getDialogPane().lookupButton(enviar);
        btnEnviar.addEventFilter(ActionEvent.ACTION, e -> {
            try {
                OrdenProduccion orden = controlador.enviarAProduccion(pedido);
                new Alert(Alert.AlertType.INFORMATION,
                        "Pedido enviado a producción. Orden N.º " + orden.getIdOrden() + ".").showAndWait();
            } catch (ReglaNegocioException ex) {
                new Alert(Alert.AlertType.ERROR, String.join("\n", ex.getErrores().values())).showAndWait();
                e.consume();
            }
        });
    }
}
