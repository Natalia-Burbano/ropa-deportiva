package co.edu.uniquindio.viveros.controlador;

import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.persistencia.BaseDatos;
import co.edu.uniquindio.viveros.persistencia.JsonRepositorio;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD de Cliente (pantalla 2). Responsable: Integrante 1.
 * En la matriz de trazabilidad estos métodos reemplazan a GET/POST/PATCH/DELETE /clientes.
 */
public class ClienteControlador {

    private final JsonRepositorio<Cliente> clientes = BaseDatos.get().clientes();

    /** Listar clientes (GET /clientes). */
    public List<Cliente> listar() {
        return new ArrayList<>(clientes.listar());
    }

    /**
     * Buscar cliente por nombre o teléfono (GET /clientes?query=).
     * La usan las dos pantallas: Agregar pedido y CRUD de Cliente.
     */
    public List<Cliente> buscarCliente(String query) {
        if (query == null || query.isBlank()) {
            return listar();
        }
        String texto = query.trim().toLowerCase();
        List<Cliente> encontrados = new ArrayList<>();
        for (Cliente c : clientes.listar()) {
            if (c.getNombre().toLowerCase().contains(texto) || c.getTelefono().contains(texto)) {
                encontrados.add(c);
            }
        }
        return encontrados;
    }

    public Cliente buscarPorId(int idCliente) {
        return clientes.buscarPorId(idCliente);
    }

    /** Crear cliente (POST /clientes). */
    public Cliente crear(Cliente cliente) {
        // TODO Integrante 1: validar datos y que el cliente no esté duplicado
        cliente.setIdCliente(clientes.siguienteId());
        clientes.agregar(cliente);
        return cliente;
    }

    /** Editar cliente (PATCH /clientes/{id}). */
    public void actualizar(Cliente cliente) {
        // TODO Integrante 1: validar datos antes de guardar
        clientes.guardar();
    }

    /** Eliminar cliente (DELETE /clientes/{id}). */
    public void eliminar(int idCliente) {
        // TODO Integrante 1: decidir qué pasa si el cliente tiene pedidos
        clientes.eliminar(idCliente);
    }
}
