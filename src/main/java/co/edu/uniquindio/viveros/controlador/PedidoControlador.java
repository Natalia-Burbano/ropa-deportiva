package co.edu.uniquindio.viveros.controlador;

import co.edu.uniquindio.viveros.modelo.Abono;
import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Disciplina;
import co.edu.uniquindio.viveros.modelo.Disenador;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.persistencia.BaseDatos;
import co.edu.uniquindio.viveros.util.Fechas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Transacción "Agregar pedido de cliente" (pantalla 1). Responsable: Integrante 2.
 * Los nombres de los métodos son los mismos del diagrama de secuencia.
 * En la matriz de trazabilidad reemplazan a POST /pedidos y PATCH /pedidos/{id}.
 */
public class PedidoControlador {

    private final BaseDatos bd = BaseDatos.get();
    private final ClienteControlador clienteControlador = new ClienteControlador();

    /** Paso 1 del CU: buscar al cliente. */
    public List<Cliente> buscarCliente(String query) {
        return clienteControlador.buscarCliente(query);
    }

    /**
     * Revisa las reglas de negocio y devuelve la lista de errores.
     * Si la lista está vacía, el pedido cumple todas las reglas.
     */
    public List<String> validarReglas(Pedido pedido) {
        List<String> errores = new ArrayList<>();
        // TODO Integrante 2: validar las reglas acordadas
        //  - Regla 001: abono mínimo del 50% del valor total
        //  - Regla 003: cada uniforme con disciplina, nombre, número y talla
        //  - Regla 004: diseño aprobado para pasar a producción
        //  - Cotización aceptada antes del abono
        //  - Todo pedido con un encargado responsable
        return errores;
    }

    /** Guarda el pedido si cumple las reglas (POST /pedidos). */
    public Pedido crearPedido(Pedido pedido) {
        // TODO Integrante 2: llamar a validarReglas() y guardar solo si no hay errores
        throw new UnsupportedOperationException("Pendiente: crearPedido");
    }

    /** Agrega un abono al crédito del pedido y genera su comprobante. */
    public void registrarAbono(Pedido pedido, Abono abono) {
        // TODO Integrante 2
        throw new UnsupportedOperationException("Pendiente: registrarAbono");
    }

    /** Regla 002: fecha del abono + días hábiles de entrega. */
    public LocalDate calcularFechaEntrega(LocalDate fechaAbono) {
        return Fechas.sumarDiasHabiles(fechaAbono, Fechas.DIAS_HABILES_ENTREGA);
    }

    /** Cambia el estado del pedido a EN_PRODUCCION (PATCH /pedidos/{id}). */
    public void enviarAProduccion(Pedido pedido) {
        // TODO Integrante 2: solo si tiene diseño aprobado y abono >= 50%
        throw new UnsupportedOperationException("Pendiente: enviarAProduccion");
    }

    // Listas para llenar los combos de la pantalla
    public List<Encargado> listarEncargados() { return bd.encargados().listar(); }
    public List<Disenador> listarDisenadores() { return bd.disenadores().listar(); }
    public List<Disciplina> listarDisciplinas() { return bd.disciplinas().listar(); }
    public List<Pedido> listarPedidos() { return bd.pedidos().listar(); }
}
