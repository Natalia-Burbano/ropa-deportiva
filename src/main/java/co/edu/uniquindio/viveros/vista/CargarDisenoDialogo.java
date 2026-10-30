package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.PedidoControlador;
import co.edu.uniquindio.viveros.modelo.Disenador;
import co.edu.uniquindio.viveros.modelo.Diseno;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

/**
 * Ventana "Cargar diseño aprobado" (formulario Soporte del prototipo).
 * Devuelve el Diseno con la foto, el diseñador, el medio de recepción y si está aprobado.
 * Regla 004: el pedido no se confirma sin diseño aprobado.
 * Regla 006: solo hay una versión por pedido; si se carga otra, reemplaza la anterior.
 */
public class CargarDisenoDialogo extends Dialog<Diseno> {

    private final ComboBox<Disenador> cbDisenador = new ComboBox<>();
    private final ComboBox<String> cbMedio = new ComboBox<>(
            FXCollections.observableArrayList("Punto de venta", "WhatsApp", "Instagram"));
    private final CheckBox chkAprobado = new CheckBox("El cliente aprobó este diseño");
    private final Label lblArchivo = new Label("Aún no se ha cargado ninguna imagen");
    private final ImageView vistaPrevia = new ImageView();
    private final Label msgError = new Label();
    private File archivo;

    public CargarDisenoDialogo(PedidoControlador controlador, String equipo, Diseno actual) {
        setTitle("Cargar diseño aprobado");
        setHeaderText("Diseño del pedido");
        getDialogPane().getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());

        ButtonType guardar = new ButtonType("Guardar diseño", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, guardar);

        TextField txtEquipo = new TextField(equipo == null || equipo.isBlank() ? "(sin nombre de equipo)" : equipo);
        txtEquipo.setEditable(false);
        txtEquipo.getStyleClass().add("solo-lectura");

        // Imagen
        Button btnBuscar = new Button("Buscar archivo…");
        btnBuscar.getStyleClass().add("boton");
        btnBuscar.setOnAction(e -> buscarArchivo());
        vistaPrevia.setFitWidth(280);
        vistaPrevia.setFitHeight(160);
        vistaPrevia.setPreserveRatio(true);
        StackPane cajaImagen = new StackPane(lblArchivo, vistaPrevia);
        cajaImagen.getStyleClass().add("vacio");
        cajaImagen.setPrefSize(300, 170);

        cbDisenador.setItems(FXCollections.observableArrayList(controlador.listarDisenadores()));
        cbDisenador.setPromptText("Seleccione…");
        cbDisenador.setMaxWidth(Double.MAX_VALUE);
        cbMedio.setPromptText("Seleccione…");
        cbMedio.setMaxWidth(Double.MAX_VALUE);

        msgError.getStyleClass().add("msg-error");
        msgError.setWrapText(true);
        msgError.setVisible(false);
        msgError.setManaged(false);

        Label ayuda = new Label("Solo imágenes JPG o PNG. Si carga una versión nueva, reemplaza la anterior.");
        ayuda.getStyleClass().add("ayuda");
        ayuda.setWrapText(true);

        VBox contenido = new VBox(10,
                etiqueta("Equipo"), txtEquipo,
                etiqueta("Imagen del diseño"), cajaImagen, btnBuscar, ayuda,
                etiqueta("Diseñador"), cbDisenador,
                etiqueta("Medio de recepción"), cbMedio,
                chkAprobado, msgError);
        contenido.setPadding(new Insets(10));
        contenido.setPrefWidth(340);
        getDialogPane().setContent(contenido);

        // Si ya había un diseño, se muestran sus datos para poder cambiarlos
        if (actual != null) {
            if (actual.getOrigen() != null) {
                File f = new File(actual.getOrigen());
                if (f.exists()) {
                    mostrarArchivo(f);
                }
            }
            cbDisenador.setValue(controlador.buscarDisenadorPorId(actual.getIdDisenador()));
            cbMedio.setValue(actual.getMedioRecepcion());
            chkAprobado.setSelected(Diseno.APROBADO.equals(actual.getEstado()));
        }

        // No deja guardar si falta algo
        Button btnGuardar = (Button) getDialogPane().lookupButton(guardar);
        btnGuardar.addEventFilter(ActionEvent.ACTION, e -> {
            String error = validar();
            if (error != null) {
                msgError.setText(error);
                msgError.setVisible(true);
                msgError.setManaged(true);
                e.consume();
            }
        });

        setResultConverter(boton -> boton == guardar ? construirDiseno() : null);
    }

    private void buscarArchivo() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Escoger imagen del diseño");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes (JPG, PNG)", "*.jpg", "*.jpeg", "*.png"));
        File escogido = selector.showOpenDialog(getDialogPane().getScene().getWindow());
        if (escogido != null) {
            mostrarArchivo(escogido);
        }
    }

    private void mostrarArchivo(File f) {
        archivo = f;
        vistaPrevia.setImage(new Image(f.toURI().toString(), 280, 160, true, true));
        lblArchivo.setText("");
    }

    private String validar() {
        if (archivo == null) {
            return "Cargue la imagen del diseño.";
        }
        String nombre = archivo.getName().toLowerCase();
        if (!(nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".png"))) {
            return "Solo se permiten imágenes JPG o PNG.";
        }
        if (cbDisenador.getValue() == null) {
            return "Escoja el diseñador que hizo el diseño.";
        }
        if (cbMedio.getValue() == null) {
            return "Escoja por dónde llegó el diseño.";
        }
        return null;
    }

    private Diseno construirDiseno() {
        boolean aprobado = chkAprobado.isSelected();
        Diseno d = new Diseno();
        d.setOrigen(archivo.getAbsolutePath());
        d.setMedioRecepcion(cbMedio.getValue());
        d.setIdDisenador(cbDisenador.getValue().getIdDisenador());
        d.setEstado(aprobado ? Diseno.APROBADO : Diseno.EN_REVISION);
        d.setFechaAprobacion(aprobado ? LocalDate.now() : null);
        return d;
    }

    private static Label etiqueta(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("etiqueta");
        return l;
    }
}
