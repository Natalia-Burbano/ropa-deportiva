package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.PedidoControlador;
import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Disenador;
import co.edu.uniquindio.viveros.modelo.Diseno;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.util.Formato;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controlador de PedidosVista.fxml: lista de pedidos registrados con su encargado,
 * fecha de entrega, estado y la imagen del diseño aprobado.
 */
public class PedidosVistaController {

    private static final String TODOS = "TODOS";

    private final PedidoControlador controlador = new PedidoControlador();

    @FXML private TextField txtBuscar;
    @FXML private ToggleGroup grupoEstado;
    @FXML private ToggleButton tglTodos;
    @FXML private ToggleButton tglSinConfirmar;
    @FXML private ToggleButton tglConfirmado;
    @FXML private ToggleButton tglEnProduccion;
    @FXML private ToggleButton tglEntregado;
    @FXML private Label lblResumen;
    @FXML private ListView<Pedido> lstPedidos;
    @FXML private Label lblVacio;

    // Detalle
    @FXML private Label lblSinSeleccion;
    @FXML private VBox panelDetalle;
    @FXML private StackPane cajaDiseno;
    @FXML private Label lblInfoDiseno;
    @FXML private Label lblDetalleNumero;
    @FXML private Label lblDetalleEstado;
    @FXML private Label lblDetalleEquipo;
    @FXML private Label lblDetalleCliente;
    @FXML private Label lblDetalleEncargado;
    @FXML private Label lblDetalleSolicitud;
    @FXML private Label lblDetalleEntrega;
    @FXML private Label lblDetallePrenda;
    @FXML private Label lblDetalleUniformes;
    @FXML private Label lblDetalleTotal;
    @FXML private Label lblDetalleAbonado;
    @FXML private Label lblDetalleSaldo;

    @FXML
    private void initialize() {
        lstPedidos.setCellFactory(lista -> new PedidoCelda());
        lstPedidos.getSelectionModel().selectedItemProperty().addListener((o, antes, ahora) -> mostrarDetalle(ahora));

        txtBuscar.textProperty().addListener((o, a, n) -> aplicarFiltros());

        // Siempre queda un filtro escogido (no se puede "deseleccionar" todos)
        grupoEstado.selectedToggleProperty().addListener((o, antes, ahora) -> {
            if (ahora == null) {
                grupoEstado.selectToggle(antes);
            } else {
                aplicarFiltros();
            }
        });

        refrescar();
    }

    /** Vuelve a leer los pedidos. La llama App cada vez que se abre la pestaña. */
    @FXML
    public void refrescar() {
        actualizarContadores();
        aplicarFiltros();
    }

    // ---------- Filtros ----------

    private void aplicarFiltros() {
        Pedido seleccionado = lstPedidos.getSelectionModel().getSelectedItem();
        String estado = estadoEscogido();
        String texto = normalizar(txtBuscar.getText());

        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : controlador.listarPedidos()) {
            if (!TODOS.equals(estado) && !estado.equals(p.getEstado())) {
                continue;
            }
            if (!texto.isEmpty() && !coincide(p, texto)) {
                continue;
            }
            filtrados.add(p);
        }
        filtrados.sort(Comparator.comparingInt(Pedido::getIdPedido).reversed()); // los más nuevos primero

        lstPedidos.setItems(FXCollections.observableArrayList(filtrados));
        lblResumen.setText(filtrados.size() + (filtrados.size() == 1 ? " pedido" : " pedidos"));
        lblVacio.setText(controlador.listarPedidos().isEmpty()
                ? "Todavía no hay pedidos. Regístrelos en la pestaña Agregar pedido."
                : "Ningún pedido coincide con la búsqueda o el filtro.");

        // Conserva la selección si el pedido sigue en la lista
        if (seleccionado != null && filtrados.contains(seleccionado)) {
            lstPedidos.getSelectionModel().select(seleccionado);
        } else {
            mostrarDetalle(null);
        }
    }

    private boolean coincide(Pedido p, String texto) {
        Cliente cliente = controlador.buscarClientePorId(p.getIdCliente());
        return normalizar(p.getNombreGrupo()).contains(texto)
                || (cliente != null && normalizar(cliente.getNombre()).contains(texto))
                || String.valueOf(p.getIdPedido()).equals(texto.replace("#", ""));
    }

    private String estadoEscogido() {
        Toggle t = grupoEstado.getSelectedToggle();
        return t == null || t.getUserData() == null ? TODOS : t.getUserData().toString();
    }

    private void actualizarContadores() {
        int sin = 0, conf = 0, prod = 0, entr = 0;
        for (Pedido p : controlador.listarPedidos()) {
            switch (p.getEstado() == null ? "" : p.getEstado()) {
                case Pedido.SIN_CONFIRMAR -> sin++;
                case Pedido.CONFIRMADO -> conf++;
                case Pedido.EN_PRODUCCION -> prod++;
                case Pedido.ENTREGADO -> entr++;
                default -> { }
            }
        }
        tglTodos.setText("Todos  " + controlador.listarPedidos().size());
        tglSinConfirmar.setText("Sin confirmar  " + sin);
        tglConfirmado.setText("Confirmados  " + conf);
        tglEnProduccion.setText("En producción  " + prod);
        tglEntregado.setText("Entregados  " + entr);
    }

    // ---------- Detalle ----------

    private void mostrarDetalle(Pedido p) {
        boolean hay = p != null;
        panelDetalle.setVisible(hay);
        panelDetalle.setManaged(hay);
        lblSinSeleccion.setVisible(!hay);
        lblSinSeleccion.setManaged(!hay);
        if (!hay) {
            return;
        }

        cajaDiseno.getChildren().setAll(ImagenDiseno.crear(p.getDiseno(), p.getNombreGrupo(), 340, 260));
        lblInfoDiseno.setText(textoDiseno(p.getDiseno()));

        lblDetalleNumero.setText("Pedido #" + p.getIdPedido());
        ponerEstado(lblDetalleEstado, p.getEstado());
        lblDetalleEquipo.setText(p.getNombreGrupo());

        Cliente cliente = controlador.buscarClientePorId(p.getIdCliente());
        lblDetalleCliente.setText(cliente == null ? "—" : cliente.getNombre() + "\n" + cliente.getTelefono());
        lblDetalleEncargado.setText(nombreEncargado(p, true));
        lblDetalleSolicitud.setText(Formato.fecha(p.getFechaSolicitud()));
        ponerEntrega(lblDetalleEntrega, p);
        lblDetallePrenda.setText(p.getTipoPrenda() + "  ·  " + Formato.pesos(p.getValorUnitario()) + " c/u");
        lblDetalleUniformes.setText(String.valueOf(p.getCantidadUniformes()));

        double total = p.getValorTotal();
        double abonado = p.getCredito() == null ? 0 : p.getCredito().getValorAbonado();
        double saldo = p.getCredito() == null ? total : p.getCredito().getSaldoPendiente(total);
        lblDetalleTotal.setText(Formato.pesos(total));
        lblDetalleAbonado.setText(Formato.pesos(abonado)
                + (total > 0 ? "  (" + Math.round(abonado * 100 / total) + "%)" : ""));
        lblDetalleSaldo.setText(saldo <= 0 ? "Pagado" : Formato.pesos(saldo));
    }

    /** Clic en la imagen: la muestra en grande. */
    @FXML
    private void ampliarDiseno() {
        Pedido p = lstPedidos.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }
        Dialog<Void> dialogo = new Dialog<>();
        dialogo.setTitle("Diseño · " + p.getNombreGrupo());
        dialogo.setHeaderText("Pedido #" + p.getIdPedido() + " · " + p.getNombreGrupo());
        dialogo.getDialogPane().getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        dialogo.getDialogPane().setContent(ImagenDiseno.crear(p.getDiseno(), p.getNombreGrupo(), 640, 520));
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialogo.showAndWait();
    }

    private String textoDiseno(Diseno d) {
        if (d == null) {
            return "Sin diseño cargado.";
        }
        Disenador disenador = controlador.buscarDisenadorPorId(d.getIdDisenador());
        String quien = disenador == null ? "" : "Diseñó: " + disenador.getNombre() + "  ·  ";
        String estado = Diseno.APROBADO.equals(d.getEstado())
                ? "Aprobado el " + Formato.fecha(d.getFechaAprobacion())
                : "Diseño en revisión";
        return quien + estado + "  ·  Llegó por " + d.getMedioRecepcion() + "\nClic en la imagen para verla en grande.";
    }

    // ---------- Ayudas compartidas con la celda ----------

    private String nombreEncargado(Pedido p, boolean conCargo) {
        Encargado e = controlador.buscarEncargadoPorId(p.getIdEncargado());
        if (e == null) {
            return "Sin encargado";
        }
        return conCargo ? e.getNombre() + " (" + e.getCargo() + ")" : e.getNombre();
    }

    private static void ponerEstado(Label etiqueta, String estado) {
        etiqueta.getStyleClass().removeAll("estado-sin-confirmar", "estado-confirmado",
                "estado-en-produccion", "estado-entregado");
        if (!etiqueta.getStyleClass().contains("estado")) {
            etiqueta.getStyleClass().add("estado");
        }
        String texto;
        String clase;
        switch (estado == null ? "" : estado) {
            case Pedido.CONFIRMADO -> { texto = "Confirmado"; clase = "estado-confirmado"; }
            case Pedido.EN_PRODUCCION -> { texto = "En producción"; clase = "estado-en-produccion"; }
            case Pedido.ENTREGADO -> { texto = "Entregado"; clase = "estado-entregado"; }
            default -> { texto = "Sin confirmar"; clase = "estado-sin-confirmar"; }
        }
        etiqueta.setText(texto);
        etiqueta.getStyleClass().add(clase);
    }

    /** Fecha de entrega; en rojo si ya pasó y el pedido no se ha entregado. */
    private static void ponerEntrega(Label etiqueta, Pedido p) {
        etiqueta.getStyleClass().remove("fecha-vencida");
        LocalDate entrega = p.getFechaEntregaEstimada();
        if (entrega == null) {
            etiqueta.setText("Por definir (falta el abono)");
            return;
        }
        boolean vencida = entrega.isBefore(LocalDate.now()) && !Pedido.ENTREGADO.equals(p.getEstado());
        etiqueta.setText(Formato.fecha(entrega) + (vencida ? "  · vencida" : ""));
        if (vencida) {
            etiqueta.getStyleClass().add("fecha-vencida");
        }
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").trim().toLowerCase();
    }

    // ---------- Tarjeta de cada pedido en la lista ----------

    private class PedidoCelda extends ListCell<Pedido> {

        @Override
        protected void updateItem(Pedido p, boolean vacia) {
            super.updateItem(p, vacia);
            setText(null);
            if (vacia || p == null) {
                setGraphic(null);
                return;
            }

            Node miniatura = ImagenDiseno.crear(p.getDiseno(), p.getNombreGrupo(), 84, 84);

            Label numero = new Label("#" + p.getIdPedido());
            numero.getStyleClass().add("numero-pedido");
            Label equipo = new Label(p.getNombreGrupo());
            equipo.getStyleClass().add("tarjeta-titulo");
            HBox filaTitulo = new HBox(8, numero, equipo);
            filaTitulo.setAlignment(Pos.CENTER_LEFT);

            Cliente cliente = controlador.buscarClientePorId(p.getIdCliente());
            Label detalle = new Label((cliente == null ? "Sin cliente" : cliente.getNombre())
                    + "  ·  " + p.getTipoPrenda() + "  ·  " + p.getCantidadUniformes() + " uniformes");
            detalle.getStyleClass().add("tarjeta-detalle");

            Label encargado = dato("Encargado", nombreEncargado(p, false));
            Label entrega = new Label();
            ponerEntrega(entrega, p);
            entrega.getStyleClass().add("dato-chip");
            HBox datos = new HBox(8, encargado, conTitulo("Entrega", entrega));
            datos.setAlignment(Pos.CENTER_LEFT);

            VBox centro = new VBox(4, filaTitulo, detalle, datos);
            centro.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(centro, Priority.ALWAYS);

            Label estado = new Label();
            ponerEstado(estado, p.getEstado());
            Label total = new Label(Formato.pesos(p.getValorTotal()));
            total.getStyleClass().add("tarjeta-total");
            VBox derecha = new VBox(8, estado, total);
            derecha.setAlignment(Pos.CENTER_RIGHT);
            derecha.setMinWidth(Region.USE_PREF_SIZE);

            HBox tarjeta = new HBox(14, miniatura, centro, derecha);
            tarjeta.setAlignment(Pos.CENTER_LEFT);
            tarjeta.getStyleClass().add("tarjeta-pedido");
            // La tarjeta ocupa todo el ancho de la lista (menos la barra de desplazamiento)
            tarjeta.prefWidthProperty().bind(lstPedidos.widthProperty().subtract(36));
            tarjeta.setMinWidth(0);
            setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
            setPrefWidth(0);
            setGraphic(tarjeta);
        }

        private Label dato(String titulo, String valor) {
            Label l = new Label(titulo + ": " + valor);
            l.getStyleClass().add("dato-chip");
            return l;
        }

        private Label conTitulo(String titulo, Label etiqueta) {
            etiqueta.setText(titulo + ": " + etiqueta.getText());
            return etiqueta;
        }
    }
}
