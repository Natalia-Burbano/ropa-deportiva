package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.ClienteControlador;
import co.edu.uniquindio.viveros.controlador.ClienteControlador.Pagina;
import co.edu.uniquindio.viveros.controlador.ReglaNegocioException;
import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Encargado;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador de la vista ClientesVista.fxml (pantalla 2: CRUD de Cliente). Responsable: Integrante 1.
 * Solo maneja la pantalla; las reglas están en ClienteControlador.
 */
public class ClientesVistaController {

    private final ClienteControlador controlador = new ClienteControlador();

    // Listado
    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colIdCliente;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCanalContacto;
    @FXML private TableColumn<Cliente, String> colEncargado;
    @FXML private TableColumn<Cliente, Integer> colPedidos;
    @FXML private Label lblVacio;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label lblPagina;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensajeLista;

    // Formulario (los nombres siguen los atributos de Cliente)
    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbCanalContacto;
    @FXML private ComboBox<Encargado> cmbEncargado;
    @FXML private Label msgNombre;
    @FXML private Label msgTelefono;
    @FXML private Label msgCanalContacto;
    @FXML private Label msgEncargado;
    @FXML private Label lblBanner;
    @FXML private Label lblExito;
    @FXML private Button btnGuardar;

    private final Map<String, Label> mensajes = new LinkedHashMap<>();
    private final Map<String, Control> campos = new LinkedHashMap<>();

    private int paginaActual = 0;
    /** Cliente que se está editando; null cuando el formulario es para uno nuevo. */
    private Cliente clienteEditando;

    @FXML
    private void initialize() {
        // Columnas de la tabla
        colIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCanalContacto.setCellValueFactory(new PropertyValueFactory<>("canalContacto"));
        colEncargado.setCellValueFactory(c -> new SimpleStringProperty(nombreEncargado(c.getValue().getIdEncargado())));
        colPedidos.setCellValueFactory(c ->
                new SimpleObjectProperty<>(controlador.contarPedidos(c.getValue().getIdCliente())));

        // Doble clic en una fila = editar
        tblClientes.setRowFactory(tabla -> {
            TableRow<Cliente> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) {
                    cargarEnFormulario(fila.getItem());
                }
            });
            return fila;
        });

        // Editar y Eliminar solo se activan con una fila seleccionada
        btnEditar.disableProperty().bind(tblClientes.getSelectionModel().selectedItemProperty().isNull());
        btnEliminar.disableProperty().bind(tblClientes.getSelectionModel().selectedItemProperty().isNull());

        // Búsqueda mientras se escribe
        txtBuscar.textProperty().addListener((obs, antes, ahora) -> {
            paginaActual = 0;
            refrescarTabla();
        });

        // Combos del formulario
        cmbCanalContacto.setItems(FXCollections.observableArrayList(ClienteControlador.CANALES_CONTACTO));
        cmbEncargado.setItems(FXCollections.observableArrayList(controlador.listarEncargados()));

        // Mensajes de error junto a cada campo
        mensajes.put(ClienteControlador.CAMPO_NOMBRE, msgNombre);
        mensajes.put(ClienteControlador.CAMPO_TELEFONO, msgTelefono);
        mensajes.put(ClienteControlador.CAMPO_CANAL_CONTACTO, msgCanalContacto);
        mensajes.put(ClienteControlador.CAMPO_ENCARGADO, msgEncargado);
        campos.put(ClienteControlador.CAMPO_NOMBRE, txtNombre);
        campos.put(ClienteControlador.CAMPO_TELEFONO, txtTelefono);
        campos.put(ClienteControlador.CAMPO_CANAL_CONTACTO, cmbCanalContacto);
        campos.put(ClienteControlador.CAMPO_ENCARGADO, cmbEncargado);

        // Al corregir un campo se quita su error
        txtNombre.textProperty().addListener((o, a, n) -> limpiarError(ClienteControlador.CAMPO_NOMBRE));
        txtTelefono.textProperty().addListener((o, a, n) -> limpiarError(ClienteControlador.CAMPO_TELEFONO));
        cmbCanalContacto.valueProperty().addListener((o, a, n) -> limpiarError(ClienteControlador.CAMPO_CANAL_CONTACTO));
        cmbEncargado.valueProperty().addListener((o, a, n) -> limpiarError(ClienteControlador.CAMPO_ENCARGADO));

        nuevo();
        refrescarTabla();
    }

    // ---------- Listado ----------

    private void refrescarTabla() {
        Pagina<Cliente> pagina = controlador.listarPaginado(txtBuscar.getText(), paginaActual,
                ClienteControlador.TAMANO_PAGINA);
        paginaActual = pagina.numero();
        tblClientes.setItems(FXCollections.observableArrayList(pagina.elementos()));
        tblClientes.refresh();

        // Estado vacío: distinto si no hay clientes o si la búsqueda no encontró nada
        boolean hayBusqueda = txtBuscar.getText() != null && !txtBuscar.getText().isBlank();
        lblVacio.setText(hayBusqueda
                ? "Ningún cliente coincide con «" + txtBuscar.getText().trim() + "». Puede registrarlo en el formulario."
                : "No hay clientes registrados. Use el formulario de la derecha para crear el primero.");

        lblPagina.setText("Página " + (pagina.numero() + 1) + " de " + pagina.totalPaginas()
                + "  ·  " + pagina.totalElementos() + (pagina.totalElementos() == 1 ? " cliente" : " clientes"));
        btnAnterior.setDisable(!pagina.hayAnterior());
        btnSiguiente.setDisable(!pagina.haySiguiente());
    }

    @FXML
    private void paginaAnterior() {
        paginaActual--;
        refrescarTabla();
    }

    @FXML
    private void paginaSiguiente() {
        paginaActual++;
        refrescarTabla();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear(); // el listener refresca la tabla
    }

    @FXML
    private void editarSeleccionado() {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            cargarEnFormulario(seleccionado);
        }
    }

    @FXML
    private void eliminarSeleccionado() {
        ocultar(lblMensajeLista);
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar a " + seleccionado.getNombre() + " (" + seleccionado.getTelefono() + ")?\n"
                        + "Esta acción no se puede deshacer.",
                ButtonType.YES, ButtonType.NO);
        confirmar.setTitle("Eliminar cliente");
        confirmar.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.YES) {
            return;
        }
        try {
            controlador.eliminar(seleccionado.getIdCliente());
            if (clienteEditando != null && clienteEditando.getIdCliente() == seleccionado.getIdCliente()) {
                nuevo();
            }
            refrescarTabla();
            mostrarExito("Se eliminó a " + seleccionado.getNombre() + ".");
        } catch (ReglaNegocioException ex) {
            lblMensajeLista.setText(ex.getMessage());
            mostrar(lblMensajeLista);
        }
    }

    // ---------- Formulario ----------

    /** Deja el formulario listo para registrar un cliente nuevo. */
    @FXML
    private void nuevo() {
        clienteEditando = null;
        lblTituloFormulario.setText("Nuevo cliente");
        btnGuardar.setText("Guardar cliente");
        txtNombre.clear();
        txtTelefono.clear();
        cmbCanalContacto.getSelectionModel().clearSelection();
        cmbCanalContacto.setValue(null);
        cmbEncargado.getSelectionModel().clearSelection();
        cmbEncargado.setValue(null);
        limpiarErrores();
        ocultar(lblExito);
        tblClientes.getSelectionModel().clearSelection();
    }

    private void cargarEnFormulario(Cliente cliente) {
        limpiarErrores();
        ocultar(lblExito);
        clienteEditando = cliente;
        lblTituloFormulario.setText("Editar cliente #" + cliente.getIdCliente());
        btnGuardar.setText("Guardar cambios");
        txtNombre.setText(cliente.getNombre());
        txtTelefono.setText(cliente.getTelefono());
        cmbCanalContacto.setValue(cliente.getCanalContacto());
        cmbEncargado.setValue(controlador.buscarEncargadoPorId(cliente.getIdEncargado()));
        txtNombre.requestFocus();
    }

    @FXML
    private void guardar() {
        limpiarErrores();
        ocultar(lblExito);

        Cliente datos = new Cliente(
                clienteEditando == null ? 0 : clienteEditando.getIdCliente(),
                txtNombre.getText(),
                txtTelefono.getText(),
                cmbCanalContacto.getValue(),
                cmbEncargado.getValue() == null ? 0 : cmbEncargado.getValue().getIdEncargado());

        try {
            if (clienteEditando == null) {
                Cliente creado = controlador.crear(datos);
                txtBuscar.clear();
                nuevo();
                irAlCliente(creado);
                mostrarExito("Cliente #" + creado.getIdCliente() + " registrado: " + creado.getNombre() + ".");
            } else {
                Cliente actualizado = controlador.actualizar(datos);
                nuevo();
                refrescarTabla();
                mostrarExito("Se guardaron los cambios de " + actualizado.getNombre() + ".");
            }
        } catch (ReglaNegocioException ex) {
            mostrarErrores(ex.getErrores());
        }
    }

    /** Después de crear, muestra la página donde quedó el cliente y lo deja seleccionado. */
    private void irAlCliente(Cliente cliente) {
        int posicion = controlador.listar().indexOf(cliente);
        paginaActual = Math.max(0, posicion) / ClienteControlador.TAMANO_PAGINA;
        refrescarTabla();
        tblClientes.getSelectionModel().select(cliente);
        tblClientes.scrollTo(cliente);
    }

    // ---------- Errores y mensajes ----------

    private void mostrarErrores(Map<String, String> errores) {
        Control primero = null;
        for (Map.Entry<String, String> error : errores.entrySet()) {
            Label mensaje = mensajes.get(error.getKey());
            if (mensaje != null) {
                mensaje.setText(error.getValue());
                mostrar(mensaje);
                Control campo = campos.get(error.getKey());
                campo.getStyleClass().add("campo-error");
                if (primero == null) {
                    primero = campo;
                }
            } else {
                lblBanner.setText(error.getValue());
                mostrar(lblBanner);
            }
        }
        if (errores.size() > 1 || lblBanner.isVisible()) {
            if (!lblBanner.isVisible()) {
                lblBanner.setText("Revise los " + errores.size() + " campos marcados en rojo.");
                mostrar(lblBanner);
            }
        }
        if (primero != null) {
            primero.requestFocus();
        }
    }

    private void limpiarError(String campo) {
        Label mensaje = mensajes.get(campo);
        if (mensaje != null) {
            ocultar(mensaje);
        }
        Control control = campos.get(campo);
        if (control != null) {
            control.getStyleClass().removeAll("campo-error");
        }
    }

    private void limpiarErrores() {
        for (String campo : mensajes.keySet()) {
            limpiarError(campo);
        }
        ocultar(lblBanner);
        ocultar(lblMensajeLista);
    }

    private void mostrarExito(String texto) {
        lblExito.setText("✔ " + texto);
        mostrar(lblExito);
    }

    private String nombreEncargado(int idEncargado) {
        Encargado e = controlador.buscarEncargadoPorId(idEncargado);
        return e == null ? "—" : e.getNombre();
    }

    private static void mostrar(Label label) {
        label.setVisible(true);
        label.setManaged(true);
    }

    private static void ocultar(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
}
