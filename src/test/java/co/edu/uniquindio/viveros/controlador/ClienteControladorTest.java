package co.edu.uniquindio.viveros.controlador;

import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.persistencia.JsonRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Casos de aceptación Given-When-Then del CRUD de Cliente (CU-02 Gestionar clientes).
 * Usan una carpeta temporal, así no tocan la carpeta data de la app.
 */
class ClienteControladorTest {

    @TempDir
    Path carpeta;

    private ClienteControlador controlador;
    private JsonRepositorio<Cliente> clientes;
    private JsonRepositorio<Pedido> pedidos;

    @BeforeEach
    void prepararDatos() {
        clientes = new JsonRepositorio<>(carpeta.resolve("clientes.json"), Cliente.class, Cliente::getIdCliente);
        pedidos = new JsonRepositorio<>(carpeta.resolve("pedidos.json"), Pedido.class, Pedido::getIdPedido);
        JsonRepositorio<Encargado> encargados =
                new JsonRepositorio<>(carpeta.resolve("encargados.json"), Encargado.class, Encargado::getIdEncargado);
        encargados.agregar(new Encargado(1, "Marcela Viveros", "Encargada"));
        clientes.agregar(new Cliente(1, "Jhon Jairo Cabrera", "3112345678", "WhatsApp", 1));
        clientes.agregar(new Cliente(2, "Óscar Jiménez", "3001112233", "Instagram", 1));
        controlador = new ClienteControlador(clientes, pedidos, encargados);
    }

    @Test
    @DisplayName("GWT-C1: crear un cliente con datos válidos")
    void crearClienteValido() throws ReglaNegocioException {
        // Given: un cliente nuevo con nombre, teléfono, canal y encargado válidos
        Cliente nuevo = new Cliente(0, "  Sandra   Pantoja ", "318 777-6655", "Punto de venta", 1);

        // When: el encargado da Guardar cliente
        Cliente creado = controlador.crear(nuevo);

        // Then: queda guardado con el siguiente id y los datos limpios
        assertEquals(3, creado.getIdCliente());
        assertEquals("Sandra Pantoja", creado.getNombre());
        assertEquals("3187776655", creado.getTelefono());
        assertNotNull(controlador.buscarPorId(3));
    }

    @Test
    @DisplayName("GWT-C2: no se permite un cliente duplicado (mismo teléfono)")
    void noPermiteTelefonoDuplicado() {
        // Given: ya existe Jhon Jairo Cabrera con el teléfono 3112345678
        Cliente repetido = new Cliente(0, "Jhon Cabrera", "3112345678", "WhatsApp", 1);

        // When: se intenta registrar otro cliente con el mismo teléfono
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> controlador.crear(repetido));

        // Then: se bloquea con un error en el campo teléfono y no se guarda
        assertTrue(ex.getErrores().get(ClienteControlador.CAMPO_TELEFONO).contains("Ya existe"));
        assertEquals(2, controlador.listar().size());
    }

    @Test
    @DisplayName("GWT-C3: no se permite un cliente duplicado (mismo nombre, sin importar tildes)")
    void noPermiteNombreDuplicado() {
        // Given: ya existe "Óscar Jiménez"
        Cliente repetido = new Cliente(0, "oscar jimenez", "3009998877", "WhatsApp", 1);

        // When: se intenta registrar "oscar jimenez" con otro teléfono
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> controlador.crear(repetido));

        // Then: se marca el error en el campo nombre
        assertTrue(ex.getErrores().containsKey(ClienteControlador.CAMPO_NOMBRE));
    }

    @Test
    @DisplayName("GWT-C4: teléfono inválido y campos vacíos muestran un error por campo")
    void datosInvalidos() {
        // Given: un formulario con teléfono de 7 dígitos y sin canal ni encargado
        Cliente incompleto = new Cliente(0, "", "3112233", null, 0);

        // When: se da Guardar cliente
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> controlador.crear(incompleto));

        // Then: hay un mensaje para cada campo con problema
        assertEquals(4, ex.getErrores().size());
        assertTrue(ex.getErrores().get(ClienteControlador.CAMPO_TELEFONO).contains("10 dígitos"));
    }

    @Test
    @DisplayName("GWT-C5: editar un cliente sin marcarse a sí mismo como duplicado")
    void editarCliente() throws ReglaNegocioException {
        // Given: el cliente 1 existe
        Cliente cambios = new Cliente(1, "Jhon Jairo Cabrera", "3112345678", "Instagram", 1);

        // When: se cambia solo el canal de contacto y se guarda
        controlador.actualizar(cambios);

        // Then: el cambio queda guardado
        assertEquals("Instagram", controlador.buscarPorId(1).getCanalContacto());
    }

    @Test
    @DisplayName("GWT-C6: un error al editar no deja datos a medias")
    void editarInvalidoNoCambiaNada() {
        // Given: el cliente 1 existe y el 2 tiene el teléfono 3001112233
        Cliente cambios = new Cliente(1, "Jhon Jairo Cabrera", "3001112233", "WhatsApp", 1);

        // When: se intenta poner al cliente 1 el teléfono del cliente 2
        assertThrows(ReglaNegocioException.class, () -> controlador.actualizar(cambios));

        // Then: el cliente 1 conserva su teléfono original
        assertEquals("3112345678", controlador.buscarPorId(1).getTelefono());
    }

    @Test
    @DisplayName("GWT-C7: no se elimina un cliente que tiene pedidos")
    void noEliminaClienteConPedidos() {
        // Given: el cliente 1 tiene un pedido
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setIdCliente(1);
        pedidos.agregar(pedido);

        // When: se intenta eliminar
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class, () -> controlador.eliminar(1));

        // Then: se bloquea y el cliente sigue en la lista
        assertTrue(ex.getMessage().contains("1 pedido"));
        assertNotNull(controlador.buscarPorId(1));
    }

    @Test
    @DisplayName("GWT-C8: eliminar un cliente sin pedidos")
    void eliminaClienteSinPedidos() throws ReglaNegocioException {
        // Given: el cliente 2 no tiene pedidos
        // When: se elimina
        controlador.eliminar(2);
        // Then: ya no aparece
        assertNull(controlador.buscarPorId(2));
    }

    @Test
    @DisplayName("GWT-C9: el listado se pagina y la búsqueda ignora tildes")
    void paginacionYBusqueda() throws ReglaNegocioException {
        // Given: 10 clientes en total
        for (int i = 0; i < 8; i++) {
            controlador.crear(new Cliente(0, "Cliente Prueba " + (char) ('A' + i), "31000000" + (10 + i), "WhatsApp", 1));
        }

        // When: se pide la primera y la segunda página de 8
        ClienteControlador.Pagina<Cliente> p1 = controlador.listarPaginado("", 0, 8);
        ClienteControlador.Pagina<Cliente> p2 = controlador.listarPaginado("", 1, 8);

        // Then: 8 en la primera, 2 en la segunda, y "oscar" encuentra a "Óscar"
        assertEquals(2, p1.totalPaginas());
        assertEquals(8, p1.elementos().size());
        assertEquals(2, p2.elementos().size());
        assertEquals(1, controlador.buscarCliente("oscar").size());
    }
}
