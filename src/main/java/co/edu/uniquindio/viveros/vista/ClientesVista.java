package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.controlador.ClienteControlador;
import co.edu.uniquindio.viveros.modelo.Cliente;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * Pantalla 2: CRUD de Cliente. Responsable: Integrante 1.
 * Por ahora solo muestra la lista, para comprobar que los datos cargan.
 */
public class ClientesVista {

    private final ClienteControlador controlador = new ClienteControlador();
    private final VBox raiz = new VBox(10);

    public ClientesVista() {
        raiz.setPadding(new Insets(20));

        TableView<Cliente> tabla = new TableView<>();
        tabla.getColumns().add(columna("ID", "idCliente"));
        tabla.getColumns().add(columna("Nombre", "nombre"));
        tabla.getColumns().add(columna("Teléfono", "telefono"));
        tabla.getColumns().add(columna("Canal de contacto", "canalContacto"));
        tabla.setItems(FXCollections.observableArrayList(controlador.listar()));
        tabla.setPlaceholder(new Label("No hay clientes registrados"));

        // TODO Integrante 1: buscador, paginación, formulario crear/editar y botón eliminar
        raiz.getChildren().addAll(new Label("Clientes"), tabla);
    }

    private TableColumn<Cliente, Object> columna(String titulo, String atributo) {
        TableColumn<Cliente, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(atributo));
        return col;
    }

    public Parent getVista() {
        return raiz;
    }
}
