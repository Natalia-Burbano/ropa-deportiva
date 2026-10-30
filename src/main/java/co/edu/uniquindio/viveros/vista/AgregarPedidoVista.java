package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.PedidoControlador;
import co.edu.uniquindio.viveros.controlador.ReglaNegocioException;
import co.edu.uniquindio.viveros.modelo.Abono;
import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Cotizacion;
import co.edu.uniquindio.viveros.modelo.Disciplina;
import co.edu.uniquindio.viveros.modelo.Disenador;
import co.edu.uniquindio.viveros.modelo.Diseno;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.modelo.Uniforme;
import co.edu.uniquindio.viveros.util.Formato;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static co.edu.uniquindio.viveros.controlador.PedidoControlador.*;

/**
 * Pantalla 1: Agregar pedido de cliente (transacción). Responsable: Integrante 2.
 * Sigue el prototipo: 1 Cliente, 2 Datos del pedido, 3 Uniformes, 4 Cotización,
 * 5 Diseño, 6 Abono, 7 Entrega y el botón Confirmar pedido.
 * Los errores de las reglas salen junto a cada campo.
 */
public class AgregarPedidoVista {

    private final PedidoControlador controlador = new PedidoControlador();
    private final ScrollPane raiz = new ScrollPane();

    // Mensajes de error y campos marcados en rojo, por nombre de campo
    private final Map<String, Label> mensajes = new HashMap<>();
    private final Map<String, Node> campos = new HashMap<>();

    // Datos del pedido que se está armando
    private Pedido pedido;
    private Cliente clienteSeleccionado;
    private Diseno diseno;
    private final ObservableList<Uniforme> uniformes = FXCollections.observableArrayList();

    // Encabezado
    private final TextField txtIdPedido = soloLectura();
    private final TextField txtFechaSolicitud = soloLectura();
    private final ComboBox<Encargado> cbEncargado = new ComboBox<>();
    private final Label banner = new Label("No se pudo guardar el pedido. Revise los campos marcados en rojo.");

    // 1. Cliente
    private final TextField txtBuscar = new TextField();
    private final ListView<Cliente> lstResultados = new ListView<>();
    private final Label lblClienteVacio = vacio("Busque un cliente por nombre o teléfono para empezar.");
    private final TextField txtClienteNombre = soloLectura();
    private final TextField txtClienteTelefono = soloLectura();
    private final TextField txtClienteCanal = soloLectura();
    private HBox filaCliente;

    // 2. Datos del pedido
    private final TextField txtNombreGrupo = new TextField();
    private final ComboBox<String> cbTipoPrenda = new ComboBox<>(FXCollections.observableArrayList(
            "Camiseta", "Camiseta y pantaloneta", "Camiseta, pantaloneta y medias"));
    private final TextField txtValorUnitario = new TextField();

    // 3. Uniformes
    private final TableView<Uniforme> tblUniformes = new TableView<>(uniformes);
    private final Label lblCantidad = new Label();

    // 4. Cotización
    private final Label lblValorTotal = new Label();
    private final CheckBox chkCotizacion = new CheckBox("El cliente aceptó la cotización");

    // 5. Diseño
    private final Label lblDisenoVacio = vacio("Aún no se ha cargado el diseño.");
    private final Label lblDisenoArchivo = new Label();
    private final Label lblDisenoEstado = new Label();

    // 6. Abono
    private final Label lblMinimo = new Label();
    private final TextField txtAbono = new TextField();
    private final ComboBox<String> cbMetodoPago = new ComboBox<>(
            FXCollections.observableArrayList("Efectivo", "Nequi", "Bancolombia"));
    private final TextField txtFechaAbono = soloLectura();
    private final Label lblSaldo = new Label();

    // 7. Entrega
    private final TextField txtEntrega = soloLectura();

    public AgregarPedidoVista() {
        VBox izquierda = new VBox(16, seccionCliente(), seccionDatos(), seccionUniformes());
        VBox derecha = new VBox(16, seccionCotizacion(), seccionDiseno(), seccionAbono(), seccionEntrega(), botones());
        derecha.setPrefWidth(360);
        derecha.setMinWidth(320);
        HBox columnas = new HBox(16, izquierda, derecha);
        HBox.setHgrow(izquierda, Priority.ALWAYS);

        banner.getStyleClass().add("banner-error");
        banner.setMaxWidth(Double.MAX_VALUE);
        ocultar(banner);

        VBox contenido = new VBox(16, encabezado(), banner, columnas);
        contenido.setPadding(new Insets(20));
        raiz.setContent(contenido);
        raiz.setFitToWidth(true);

        limpiar();
    }

    public Parent getVista() {
        return raiz;
    }

    // =====================================================================
    // Armado de la pantalla
    // =====================================================================

    private Node encabezado() {
        Label titulo = new Label("Agregar pedido de cliente");
        titulo.getStyleClass().add("titulo");
        Label subtitulo = new Label("El vendedor registra el pedido de uniformes del equipo");
        subtitulo.getStyleClass().add("subtitulo");
        VBox textos = new VBox(2, titulo, subtitulo);

        cbEncargado.setItems(FXCollections.observableArrayList(controlador.listarEncargados()));
        cbEncargado.setPromptText("Seleccione…");

        VBox fId = campo("N.º de pedido", txtIdPedido, null);
        VBox fFecha = campo("Fecha de solicitud", txtFechaSolicitud, null);
        VBox fEnc = campo("Encargado", cbEncargado, CAMPO_ENCARGADO);
        fId.setPrefWidth(110);
        fFecha.setPrefWidth(140);
        fEnc.setPrefWidth(240);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox fila = new HBox(12, textos, espacio, fId, fFecha, fEnc);
        fila.setAlignment(Pos.TOP_LEFT);
        return fila;
    }

    private Node seccionCliente() {
        txtBuscar.setPromptText("Nombre o teléfono");
        txtBuscar.setOnAction(e -> buscarCliente());
        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("boton");
        btnBuscar.setOnAction(e -> buscarCliente());
        HBox filaBuscar = new HBox(8, txtBuscar, btnBuscar);
        HBox.setHgrow(txtBuscar, Priority.ALWAYS);

        Label msgCliente = mensaje();
        mensajes.put(CAMPO_CLIENTE, msgCliente);
        campos.put(CAMPO_CLIENTE, txtBuscar);

        lstResultados.setPrefHeight(120);
        lstResultados.getSelectionModel().selectedItemProperty().addListener((obs, antes, cliente) -> {
            if (cliente != null) {
                seleccionarCliente(cliente);
            }
        });
        ocultar(lstResultados);

        VBox fNombre = campo("Nombre", txtClienteNombre, null);
        VBox fTel = campo("Teléfono", txtClienteTelefono, null);
        VBox fCanal = campo("Canal de contacto", txtClienteCanal, null);
        filaCliente = new HBox(12, fNombre, fTel, fCanal);
        HBox.setHgrow(fNombre, Priority.ALWAYS);
        HBox.setHgrow(fTel, Priority.ALWAYS);
        HBox.setHgrow(fCanal, Priority.ALWAYS);

        return seccion("1", "Cliente",
                new VBox(4, etiqueta("Buscar cliente"), filaBuscar, msgCliente),
                lstResultados, lblClienteVacio, filaCliente);
    }

    private Node seccionDatos() {
        txtNombreGrupo.setPromptText("Ej: Deportivo Puerto Asís");
        cbTipoPrenda.setPromptText("Seleccione…");
        txtValorUnitario.setPromptText("$0");
        txtValorUnitario.textProperty().addListener((o, a, b) -> actualizarTotales());

        VBox fGrupo = campo("Nombre del equipo", txtNombreGrupo, CAMPO_NOMBRE_GRUPO);
        VBox fTipo = campo("Tipo de prenda", cbTipoPrenda, CAMPO_TIPO_PRENDA);
        VBox fValor = campo("Valor unitario", txtValorUnitario, CAMPO_VALOR_UNITARIO);
        HBox fila = new HBox(12, fGrupo, fTipo, fValor);
        HBox.setHgrow(fGrupo, Priority.ALWAYS);
        HBox.setHgrow(fTipo, Priority.ALWAYS);
        HBox.setHgrow(fValor, Priority.ALWAYS);
        return seccion("2", "Datos del pedido", fila);
    }

    private Node seccionUniformes() {
        List<Disciplina> disciplinas = controlador.listarDisciplinas();
        List<String> tallas = List.of("S", "M", "L", "XL");

        TableColumn<Uniforme, Uniforme> colNum = new TableColumn<>("#");
        colNum.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        colNum.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Uniforme u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : String.valueOf(getIndex() + 1));
            }
        });
        colNum.setStyle("-fx-alignment: CENTER;");
        colNum.setPrefWidth(40);
        colNum.setSortable(false);

        TableColumn<Uniforme, Uniforme> colDisc = columnaLista("Disciplina", disciplinas,
                Uniforme::getDisciplina, Uniforme::setDisciplina);
        TableColumn<Uniforme, Uniforme> colNombre = columnaTexto("Nombre del jugador",
                Uniforme::getNombreJugador, Uniforme::setNombreJugador);
        TableColumn<Uniforme, Uniforme> colNumero = columnaTexto("Número",
                u -> u.getNumero() > 0 ? String.valueOf(u.getNumero()) : "",
                (u, texto) -> u.setNumero((int) Formato.leerPesos(texto)));
        TableColumn<Uniforme, Uniforme> colTalla = columnaLista("Talla", tallas,
                u -> u.getTalla() == null || u.getTalla().isEmpty() ? null : u.getTalla(), Uniforme::setTalla);

        TableColumn<Uniforme, Uniforme> colQuitar = new TableColumn<>("");
        colQuitar.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        colQuitar.setCellFactory(c -> new TableCell<>() {
            private final Button btn = new Button("✕");

            {
                btn.getStyleClass().add("boton-quitar");
                btn.setOnAction(e -> {
                    uniformes.remove(getItem());
                    tblUniformes.refresh();
                    actualizarTotales();
                });
            }

            @Override
            protected void updateItem(Uniforme u, boolean empty) {
                super.updateItem(u, empty);
                setGraphic(empty || u == null ? null : btn);
            }
        });
        colQuitar.setPrefWidth(50);
        colQuitar.setSortable(false);

        colDisc.setPrefWidth(140);
        colNombre.setPrefWidth(200);
        colNumero.setPrefWidth(80);
        colTalla.setPrefWidth(90);

        tblUniformes.getColumns().addAll(List.of(colNum, colDisc, colNombre, colNumero, colTalla, colQuitar));
        tblUniformes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tblUniformes.setPlaceholder(new Label("Todavía no hay uniformes. Use «+ Agregar uniforme»."));
        tblUniformes.setPrefHeight(280);
        tblUniformes.setFixedCellSize(40);

        Label msgUniformes = mensaje();
        mensajes.put(CAMPO_UNIFORMES, msgUniformes);
        campos.put(CAMPO_UNIFORMES, tblUniformes);

        Button btnAgregar = new Button("+ Agregar uniforme");
        btnAgregar.getStyleClass().add("boton");
        btnAgregar.setOnAction(e -> agregarUniforme());

        Label ayuda = new Label("Cada uniforme con disciplina, nombre, número y talla (Regla 003)");
        ayuda.getStyleClass().add("ayuda");

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        lblCantidad.getStyleClass().add("etiqueta");
        HBox pie = new HBox(12, btnAgregar, espacio, lblCantidad);
        pie.setAlignment(Pos.CENTER_LEFT);

        return seccion("3", "Uniformes", ayuda, tblUniformes, msgUniformes, pie);
    }

    private Node seccionCotizacion() {
        lblValorTotal.getStyleClass().add("total");
        Label ayuda = new Label("Cantidad de uniformes × valor unitario");
        ayuda.getStyleClass().add("ayuda");

        Label msgCot = mensaje();
        mensajes.put(CAMPO_COTIZACION, msgCot);
        campos.put(CAMPO_COTIZACION, chkCotizacion);

        return seccion("4", "Cotización", etiqueta("Valor total"), lblValorTotal, ayuda, chkCotizacion, msgCot);
    }

    private Node seccionDiseno() {
        lblDisenoArchivo.setWrapText(true);
        Button btnDiseno = new Button("Cargar diseño…");
        btnDiseno.getStyleClass().add("boton");
        btnDiseno.setOnAction(e -> abrirDiseno());

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox filaEstado = new HBox(8, etiqueta("Estado:"), lblDisenoEstado, espacio, btnDiseno);
        filaEstado.setAlignment(Pos.CENTER_LEFT);

        Label msgDiseno = mensaje();
        mensajes.put(CAMPO_DISENO, msgDiseno);
        campos.put(CAMPO_DISENO, lblDisenoEstado);

        return seccion("5", "Diseño", etiqueta("Foto del diseño"), lblDisenoVacio, lblDisenoArchivo, filaEstado, msgDiseno);
    }

    private Node seccionAbono() {
        txtAbono.setPromptText("$0");
        txtAbono.textProperty().addListener((o, a, b) -> actualizarTotales());
        cbMetodoPago.setPromptText("Seleccione…");

        Label bloqueado = vacio("Se habilita cuando el cliente acepte la cotización.");
        bloqueado.visibleProperty().bind(chkCotizacion.selectedProperty().not());
        bloqueado.managedProperty().bind(chkCotizacion.selectedProperty().not());

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        lblMinimo.getStyleClass().add("etiqueta");
        HBox filaMinimo = new HBox(8, new Label("Mínimo a pagar (50%)"), espacio, lblMinimo);

        VBox fAbono = campo("Valor del abono", txtAbono, CAMPO_ABONO);
        VBox fMetodo = campo("Método de pago", cbMetodoPago, CAMPO_METODO_PAGO);
        VBox fFecha = campo("Fecha del abono", txtFechaAbono, null);
        HBox fila = new HBox(12, fMetodo, fFecha);
        HBox.setHgrow(fMetodo, Priority.ALWAYS);
        HBox.setHgrow(fFecha, Priority.ALWAYS);

        Region espacio2 = new Region();
        HBox.setHgrow(espacio2, Priority.ALWAYS);
        lblSaldo.getStyleClass().add("etiqueta");
        HBox filaSaldo = new HBox(8, new Label("Saldo pendiente"), espacio2, lblSaldo);

        VBox datosAbono = new VBox(10, filaMinimo, fAbono, fila, filaSaldo);
        datosAbono.disableProperty().bind(chkCotizacion.selectedProperty().not());

        return seccion("6", "Abono", bloqueado, datosAbono);
    }

    private Node seccionEntrega() {
        txtEntrega.setPromptText("Se calcula al registrar el abono");
        Label ayuda = new Label("Fecha del abono + 10 días hábiles (Regla 002)");
        ayuda.getStyleClass().add("ayuda");
        return seccion("7", "Entrega", campo("Fecha de entrega estimada", txtEntrega, null), ayuda);
    }

    private Node botones() {
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("boton");
        btnCancelar.setOnAction(e -> cancelar());
        Button btnConfirmar = new Button("Confirmar pedido");
        btnConfirmar.getStyleClass().add("boton-principal");
        btnConfirmar.setOnAction(e -> confirmar());
        HBox fila = new HBox(10, btnCancelar, btnConfirmar);
        fila.setAlignment(Pos.CENTER_RIGHT);
        return fila;
    }

    // =====================================================================
    // Acciones
    // =====================================================================

    private void buscarCliente() {
        limpiarError(CAMPO_CLIENTE);
        List<Cliente> encontrados = controlador.buscarCliente(txtBuscar.getText());
        if (encontrados.isEmpty()) {
            ocultar(lstResultados);
            mostrarError(CAMPO_CLIENTE, "No se encontró ningún cliente con ese dato. Regístrelo en la pestaña Clientes.");
        } else if (encontrados.size() == 1) {
            seleccionarCliente(encontrados.get(0));
        } else {
            lstResultados.setItems(FXCollections.observableArrayList(encontrados));
            mostrar(lstResultados);
        }
    }

    private void seleccionarCliente(Cliente cliente) {
        clienteSeleccionado = cliente;
        txtClienteNombre.setText(cliente.getNombre());
        txtClienteTelefono.setText(cliente.getTelefono());
        txtClienteCanal.setText(cliente.getCanalContacto());
        ocultar(lstResultados);
        ocultar(lblClienteVacio);
        mostrar(filaCliente);
        limpiarError(CAMPO_CLIENTE);
    }

    private void agregarUniforme() {
        Uniforme nuevo = new Uniforme();
        nuevo.setNombreJugador("");
        nuevo.setTalla("");
        // Toma la misma disciplina del uniforme anterior para escribir menos
        if (!uniformes.isEmpty()) {
            nuevo.setDisciplina(uniformes.get(uniformes.size() - 1).getDisciplina());
        }
        uniformes.add(nuevo);
        tblUniformes.scrollTo(nuevo);
        actualizarTotales();
    }

    private void abrirDiseno() {
        new CargarDisenoDialogo(controlador, txtNombreGrupo.getText(), diseno)
                .showAndWait()
                .ifPresent(nuevo -> {
                    diseno = nuevo;
                    mostrarDiseno();
                    limpiarError(CAMPO_DISENO);
                });
    }

    private void mostrarDiseno() {
        lblDisenoEstado.getStyleClass().removeAll("estado-aprobado", "estado-revision", "estado-sin");
        if (diseno == null) {
            mostrar(lblDisenoVacio);
            ocultar(lblDisenoArchivo);
            lblDisenoEstado.setText("Sin diseño");
            lblDisenoEstado.getStyleClass().add("estado-sin");
            return;
        }
        ocultar(lblDisenoVacio);
        Disenador disenador = controlador.buscarDisenadorPorId(diseno.getIdDisenador());
        lblDisenoArchivo.setText(new File(diseno.getOrigen()).getName() + "\nDiseñador: "
                + (disenador == null ? "" : disenador.getNombre()) + " · " + diseno.getMedioRecepcion());
        mostrar(lblDisenoArchivo);
        boolean aprobado = Diseno.APROBADO.equals(diseno.getEstado());
        lblDisenoEstado.setText(aprobado ? "Aprobado" : "En revisión");
        lblDisenoEstado.getStyleClass().add(aprobado ? "estado-aprobado" : "estado-revision");
    }

    /** Recalcula cantidad, total, mínimo del 50%, saldo y fecha de entrega cada vez que algo cambia. */
    private void actualizarTotales() {
        int cantidad = uniformes.size();
        double total = cantidad * Formato.leerPesos(txtValorUnitario.getText());
        double abono = Formato.leerPesos(txtAbono.getText());

        lblCantidad.setText("Cantidad de uniformes: " + cantidad);
        lblValorTotal.setText(Formato.pesos(total));
        lblMinimo.setText(Formato.pesos(total * PORCENTAJE_MINIMO_ABONO));
        lblSaldo.setText(Formato.pesos(Math.max(total - abono, 0)));
        txtEntrega.setText(abono > 0 ? Formato.fecha(controlador.calcularFechaEntrega(LocalDate.now())) : "");
    }

    private void confirmar() {
        limpiarErrores();
        Pedido armado = construirPedido();
        Abono abono = new Abono(0, LocalDate.now(), Formato.leerPesos(txtAbono.getText()), cbMetodoPago.getValue());
        try {
            Pedido guardado = controlador.crearPedido(armado, abono);
            new ComprobanteDialogo(controlador, guardado).showAndWait();
            limpiar();
        } catch (ReglaNegocioException ex) {
            mostrarErrores(ex.getErrores());
        }
    }

    private Pedido construirPedido() {
        Encargado encargado = cbEncargado.getValue();
        pedido.setIdEncargado(encargado == null ? 0 : encargado.getIdEncargado());
        pedido.setIdCliente(clienteSeleccionado == null ? 0 : clienteSeleccionado.getIdCliente());
        pedido.setNombreGrupo(txtNombreGrupo.getText() == null ? "" : txtNombreGrupo.getText().trim());
        pedido.setTipoPrenda(cbTipoPrenda.getValue());
        pedido.setValorUnitario(Formato.leerPesos(txtValorUnitario.getText()));
        pedido.setUniformes(new ArrayList<>(uniformes));
        pedido.getCotizacion().setEstado(chkCotizacion.isSelected() ? Cotizacion.ACEPTADA : Cotizacion.PENDIENTE);
        pedido.setDiseno(diseno);
        return pedido;
    }

    private void cancelar() {
        Alert pregunta = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea descartar este pedido?",
                ButtonType.NO, ButtonType.YES);
        pregunta.setHeaderText(null);
        pregunta.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> limpiar());
    }

    /** Deja la pantalla lista para un pedido nuevo (estado vacío). */
    private void limpiar() {
        pedido = controlador.nuevoPedido();
        clienteSeleccionado = null;
        diseno = null;

        txtIdPedido.setText(String.valueOf(pedido.getIdPedido()));
        txtFechaSolicitud.setText(Formato.fecha(pedido.getFechaSolicitud()));
        cbEncargado.setValue(null);

        txtBuscar.clear();
        txtClienteNombre.clear();
        txtClienteTelefono.clear();
        txtClienteCanal.clear();
        ocultar(lstResultados);
        mostrar(lblClienteVacio);
        ocultar(filaCliente);

        txtNombreGrupo.clear();
        cbTipoPrenda.setValue(null);
        txtValorUnitario.clear();
        uniformes.clear();

        chkCotizacion.setSelected(false);
        mostrarDiseno();

        txtAbono.clear();
        cbMetodoPago.setValue(null);
        txtFechaAbono.setText(Formato.fecha(LocalDate.now()));

        limpiarErrores();
        actualizarTotales();
    }

    // =====================================================================
    // Errores junto a cada campo
    // =====================================================================

    private void mostrarErrores(Map<String, String> errores) {
        mostrar(banner);
        errores.forEach(this::mostrarError);
        raiz.setVvalue(0);
    }

    private void mostrarError(String clave, String texto) {
        Label m = mensajes.get(clave);
        if (m != null) {
            m.setText(texto);
            mostrar(m);
        }
        Node campo = campos.get(clave);
        if (campo != null && !campo.getStyleClass().contains("campo-error")) {
            campo.getStyleClass().add("campo-error");
        }
    }

    private void limpiarError(String clave) {
        Label m = mensajes.get(clave);
        if (m != null) {
            ocultar(m);
        }
        Node campo = campos.get(clave);
        if (campo != null) {
            campo.getStyleClass().remove("campo-error");
        }
    }

    private void limpiarErrores() {
        ocultar(banner);
        for (String clave : mensajes.keySet()) {
            limpiarError(clave);
        }
    }

    // =====================================================================
    // Ayudas para armar la pantalla
    // =====================================================================

    private VBox seccion(String numero, String titulo, Node... hijos) {
        Label num = new Label(numero);
        num.getStyleClass().add("numero");
        Label tit = new Label(titulo);
        tit.getStyleClass().add("titulo-seccion");
        HBox cabecera = new HBox(8, num, tit);
        cabecera.setAlignment(Pos.CENTER_LEFT);

        VBox caja = new VBox(10);
        caja.getChildren().add(cabecera);
        caja.getChildren().addAll(hijos);
        caja.getStyleClass().add("tarjeta");
        return caja;
    }

    /** Etiqueta + control + mensaje de error (si tiene clave). */
    private VBox campo(String etiqueta, Control control, String clave) {
        control.setMaxWidth(Double.MAX_VALUE);
        VBox caja = new VBox(4, etiqueta(etiqueta), control);
        if (clave != null) {
            Label m = mensaje();
            mensajes.put(clave, m);
            campos.put(clave, control);
            caja.getChildren().add(m);
            // Al corregir el campo, se quita el error
            if (control instanceof TextField tf) {
                tf.textProperty().addListener((o, a, b) -> limpiarError(clave));
            } else if (control instanceof ComboBox<?> cb) {
                cb.valueProperty().addListener((o, a, b) -> limpiarError(clave));
            }
        }
        return caja;
    }

    /** Columna de la tabla con un campo de texto que guarda en el uniforme mientras se escribe. */
    private TableColumn<Uniforme, Uniforme> columnaTexto(String titulo, Function<Uniforme, String> leer,
                                                         BiConsumer<Uniforme, String> guardar) {
        TableColumn<Uniforme, Uniforme> col = new TableColumn<>(titulo);
        col.setSortable(false);
        col.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        col.setCellFactory(c -> new TableCell<>() {
            private final TextField tf = new TextField();
            private boolean cargando;

            {
                tf.textProperty().addListener((o, a, b) -> {
                    Uniforme u = getItem();
                    if (u != null && !cargando) {
                        guardar.accept(u, b);
                        limpiarError(CAMPO_UNIFORMES);
                    }
                });
            }

            @Override
            protected void updateItem(Uniforme u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null) {
                    setGraphic(null);
                } else {
                    cargando = true;
                    tf.setText(leer.apply(u));
                    cargando = false;
                    setGraphic(tf);
                }
            }
        });
        return col;
    }

    /** Columna de la tabla con una lista desplegable que guarda en el uniforme al escoger. */
    private <T> TableColumn<Uniforme, Uniforme> columnaLista(String titulo, List<T> opciones,
                                                             Function<Uniforme, T> leer,
                                                             BiConsumer<Uniforme, T> guardar) {
        TableColumn<Uniforme, Uniforme> col = new TableColumn<>(titulo);
        col.setSortable(false);
        col.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        col.setCellFactory(c -> new TableCell<>() {
            private final ComboBox<T> cb = new ComboBox<>(FXCollections.observableArrayList(opciones));
            private boolean cargando;

            {
                cb.setMaxWidth(Double.MAX_VALUE);
                cb.setPromptText("—");
                cb.valueProperty().addListener((o, a, b) -> {
                    Uniforme u = getItem();
                    if (u != null && !cargando) {
                        guardar.accept(u, b);
                        limpiarError(CAMPO_UNIFORMES);
                    }
                });
            }

            @Override
            protected void updateItem(Uniforme u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null) {
                    setGraphic(null);
                } else {
                    cargando = true;
                    cb.setValue(leer.apply(u));
                    cargando = false;
                    setGraphic(cb);
                }
            }
        });
        return col;
    }

    private static Label etiqueta(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("etiqueta");
        return l;
    }

    private static Label mensaje() {
        Label m = new Label();
        m.getStyleClass().add("msg-error");
        m.setWrapText(true);
        ocultar(m);
        return m;
    }

    private static Label vacio(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("vacio");
        l.setMaxWidth(Double.MAX_VALUE);
        l.setWrapText(true);
        return l;
    }

    private static TextField soloLectura() {
        TextField tf = new TextField();
        tf.setEditable(false);
        tf.setFocusTraversable(false);
        tf.getStyleClass().add("solo-lectura");
        return tf;
    }

    private static void ocultar(Node n) {
        n.setVisible(false);
        n.setManaged(false);
    }

    private static void mostrar(Node n) {
        n.setVisible(true);
        n.setManaged(true);
    }
}
