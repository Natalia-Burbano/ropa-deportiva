package co.edu.uniquindio.viveros.controlador;

import co.edu.uniquindio.viveros.modelo.Abono;
import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.ComprobanteAbono;
import co.edu.uniquindio.viveros.modelo.Cotizacion;
import co.edu.uniquindio.viveros.modelo.Credito;
import co.edu.uniquindio.viveros.modelo.Disciplina;
import co.edu.uniquindio.viveros.modelo.Disenador;
import co.edu.uniquindio.viveros.modelo.Diseno;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.OrdenProduccion;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.modelo.Uniforme;
import co.edu.uniquindio.viveros.persistencia.BaseDatos;
import co.edu.uniquindio.viveros.util.Fechas;
import co.edu.uniquindio.viveros.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Transacción "Agregar pedido de cliente" (pantalla 1). Responsable: Integrante 2.
 * Los nombres de los métodos son los mismos del diagrama de secuencia.
 * En la matriz de trazabilidad reemplazan a POST /pedidos y PATCH /pedidos/{id}.
 */
public class PedidoControlador {

    // Nombres de los campos, para mostrar cada error junto a su campo en la pantalla
    public static final String CAMPO_ENCARGADO = "encargado";
    public static final String CAMPO_CLIENTE = "cliente";
    public static final String CAMPO_NOMBRE_GRUPO = "nombreGrupo";
    public static final String CAMPO_TIPO_PRENDA = "tipoPrenda";
    public static final String CAMPO_VALOR_UNITARIO = "valorUnitario";
    public static final String CAMPO_UNIFORMES = "uniformes";
    public static final String CAMPO_COTIZACION = "cotizacion";
    public static final String CAMPO_DISENO = "diseno";
    public static final String CAMPO_ABONO = "abono";
    public static final String CAMPO_METODO_PAGO = "metodoPago";
    public static final String CAMPO_ESTADO = "estado";

    /** Regla 001: el abono debe ser mínimo el 50% del valor total. */
    public static final double PORCENTAJE_MINIMO_ABONO = 0.5;

    private final BaseDatos bd = BaseDatos.get();
    private final ClienteControlador clienteControlador = new ClienteControlador();

    /** Crea un pedido vacío con su número, la fecha de hoy, la cotización y el crédito. */
    public Pedido nuevoPedido() {
        int id = bd.pedidos().siguienteId();
        LocalDate hoy = LocalDate.now();
        Pedido pedido = new Pedido();
        pedido.setIdPedido(id);
        pedido.setFechaSolicitud(hoy);
        pedido.setEstado(Pedido.SIN_CONFIRMAR);
        pedido.setCotizacion(new Cotizacion(id, hoy, 0, Cotizacion.PENDIENTE));
        pedido.setCredito(new Credito(id, hoy, Credito.ABIERTO));
        return pedido;
    }

    /** Paso 1 del CU: buscar al cliente por nombre o teléfono. */
    public List<Cliente> buscarCliente(String query) {
        return clienteControlador.buscarCliente(query);
    }

    /** Lo mínimo que debe pagar el cliente como abono (50% del valor total). */
    public double calcularAbonoMinimo(Pedido pedido) {
        return pedido.getValorTotal() * PORCENTAJE_MINIMO_ABONO;
    }

    /**
     * Revisa las reglas de negocio del pedido y su abono.
     * Devuelve los errores por campo; si está vacío, el pedido cumple todas las reglas.
     */
    public Map<String, String> validarReglas(Pedido pedido, Abono abono) {
        Map<String, String> errores = new LinkedHashMap<>();

        // Todo pedido con un encargado responsable
        if (bd.encargados().buscarPorId(pedido.getIdEncargado()) == null) {
            errores.put(CAMPO_ENCARGADO, "Escoja el encargado que recibe el pedido.");
        }
        if (bd.clientes().buscarPorId(pedido.getIdCliente()) == null) {
            errores.put(CAMPO_CLIENTE, "Debe escoger un cliente.");
        }
        if (vacio(pedido.getNombreGrupo())) {
            errores.put(CAMPO_NOMBRE_GRUPO, "Escriba el nombre del equipo.");
        }
        if (vacio(pedido.getTipoPrenda())) {
            errores.put(CAMPO_TIPO_PRENDA, "Escoja el tipo de prenda.");
        }
        if (pedido.getValorUnitario() <= 0) {
            errores.put(CAMPO_VALOR_UNITARIO, "El valor por prenda debe ser mayor a 0.");
        }

        // Regla 003: cada uniforme con disciplina, nombre, número y talla
        String errorUniformes = validarUniformes(pedido.getUniformes());
        if (errorUniformes != null) {
            errores.put(CAMPO_UNIFORMES, errorUniformes);
        }

        // Regla 005: cotización aceptada antes del abono
        if (pedido.getCotizacion() == null || !Cotizacion.ACEPTADA.equals(pedido.getCotizacion().getEstado())) {
            errores.put(CAMPO_COTIZACION, "El cliente debe aceptar la cotización antes de pagar el abono.");
        }

        // Regla 004: diseño aprobado obligatorio
        Diseno diseno = pedido.getDiseno();
        if (diseno == null || vacio(diseno.getOrigen())) {
            errores.put(CAMPO_DISENO, "Cargue la foto del diseño aprobado.");
        } else if (!Diseno.APROBADO.equals(diseno.getEstado())) {
            errores.put(CAMPO_DISENO, "El diseño todavía no está aprobado por el cliente.");
        }

        // Regla 001: abono mínimo del 50% del valor total
        double total = pedido.getValorTotal();
        double minimo = total * PORCENTAJE_MINIMO_ABONO;
        if (abono == null || abono.getValor() <= 0) {
            errores.put(CAMPO_ABONO, "Escriba el valor del abono.");
        } else if (total > 0 && abono.getValor() < minimo) {
            errores.put(CAMPO_ABONO, "El abono debe ser mínimo de " + Formato.pesos(minimo) + " (50% del total).");
        } else if (total > 0 && abono.getValor() > total) {
            errores.put(CAMPO_ABONO, "El abono no puede ser mayor al valor total (" + Formato.pesos(total) + ").");
        }
        if (abono == null || vacio(abono.getMetodoPago())) {
            errores.put(CAMPO_METODO_PAGO, "Escoja el método de pago.");
        }
        return errores;
    }

    /**
     * Guarda el pedido si cumple las reglas (POST /pedidos).
     * Registra el abono, calcula la fecha de entrega y deja el pedido CONFIRMADO.
     */
    public Pedido crearPedido(Pedido pedido, Abono abono) throws ReglaNegocioException {
        Map<String, String> errores = validarReglas(pedido, abono);
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(errores);
        }

        int idUniforme = siguienteIdUniforme();
        for (Uniforme uniforme : pedido.getUniformes()) {
            uniforme.setIdUniforme(idUniforme++);
        }
        pedido.getCotizacion().setValorTotal(pedido.getValorTotal());
        pedido.getDiseno().setIdDiseno(pedido.getIdPedido());
        pedido.setFechaConfirmacionDiseno(pedido.getDiseno().getFechaAprobacion());

        registrarAbono(pedido, abono);
        pedido.setFechaEntregaEstimada(calcularFechaEntrega(abono.getFecha()));
        pedido.setEstado(Pedido.CONFIRMADO);

        bd.pedidos().agregar(pedido);
        return pedido;
    }

    /** Agrega un abono al crédito del pedido y genera su comprobante. */
    public ComprobanteAbono registrarAbono(Pedido pedido, Abono abono) {
        abono.setIdAbono(siguienteIdAbono());
        Credito credito = pedido.getCredito();
        credito.getAbonos().add(abono);

        double saldo = credito.getSaldoPendiente(pedido.getValorTotal());
        ComprobanteAbono comprobante = new ComprobanteAbono(abono.getIdAbono(), abono.getFecha(), abono.getValor(), saldo);
        abono.setComprobante(comprobante);
        if (saldo <= 0) {
            credito.setEstado(Credito.PAGADO);
        }
        // Si el pedido ya estaba guardado (abonos posteriores), se guarda el cambio
        if (bd.pedidos().buscarPorId(pedido.getIdPedido()) != null) {
            bd.pedidos().guardar();
        }
        return comprobante;
    }

    /** Regla 002: fecha del abono + días hábiles de entrega. */
    public LocalDate calcularFechaEntrega(LocalDate fechaAbono) {
        return Fechas.sumarDiasHabiles(fechaAbono, Fechas.DIAS_HABILES_ENTREGA);
    }

    /** Cambia el estado del pedido a EN_PRODUCCION y crea la orden (PATCH /pedidos/{id}). */
    public OrdenProduccion enviarAProduccion(Pedido pedido) throws ReglaNegocioException {
        Map<String, String> errores = new LinkedHashMap<>();
        if (!Pedido.CONFIRMADO.equals(pedido.getEstado())) {
            errores.put(CAMPO_ESTADO, "Solo se pueden enviar a producción los pedidos confirmados.");
        }
        if (pedido.getDiseno() == null || !Diseno.APROBADO.equals(pedido.getDiseno().getEstado())) {
            errores.put(CAMPO_DISENO, "No se puede enviar a producción: el diseño no está aprobado.");
        }
        if (pedido.getCredito().getValorAbonado() < calcularAbonoMinimo(pedido)) {
            errores.put(CAMPO_ABONO, "No se puede enviar a producción: el abono es menor al 50%.");
        }
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(errores);
        }

        OrdenProduccion orden = new OrdenProduccion(pedido.getIdPedido(), LocalDate.now(), OrdenProduccion.ENVIADA);
        pedido.setOrdenProduccion(orden);
        pedido.setEstado(Pedido.EN_PRODUCCION);
        bd.pedidos().guardar();
        return orden;
    }

    // ---------- Ayudas ----------

    private String validarUniformes(List<Uniforme> uniformes) {
        if (uniformes == null || uniformes.isEmpty()) {
            return "Agregue al menos un uniforme.";
        }
        List<String> lineas = new ArrayList<>();
        for (int i = 0; i < uniformes.size(); i++) {
            Uniforme u = uniformes.get(i);
            List<String> faltan = new ArrayList<>();
            if (u.getDisciplina() == null) faltan.add("la disciplina");
            if (vacio(u.getNombreJugador())) faltan.add("el nombre");
            if (u.getNumero() <= 0) faltan.add("el número");
            if (vacio(u.getTalla())) faltan.add("la talla");
            if (!faltan.isEmpty()) {
                lineas.add("Uniforme " + (i + 1) + ": falta " + unir(faltan) + ".");
            }
        }
        return lineas.isEmpty() ? null : String.join("\n", lineas);
    }

    /** ["el nombre", "la talla"] → "el nombre y la talla" */
    private static String unir(List<String> partes) {
        if (partes.size() == 1) {
            return partes.get(0);
        }
        return String.join(", ", partes.subList(0, partes.size() - 1)) + " y " + partes.get(partes.size() - 1);
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private int siguienteIdUniforme() {
        int mayor = 0;
        for (Pedido p : bd.pedidos().listar()) {
            for (Uniforme u : p.getUniformes()) {
                mayor = Math.max(mayor, u.getIdUniforme());
            }
        }
        return mayor + 1;
    }

    private int siguienteIdAbono() {
        int mayor = 0;
        for (Pedido p : bd.pedidos().listar()) {
            if (p.getCredito() != null) {
                for (Abono a : p.getCredito().getAbonos()) {
                    mayor = Math.max(mayor, a.getIdAbono());
                }
            }
        }
        return mayor + 1;
    }

    // Listas para llenar los combos de la pantalla y nombres para mostrar
    public List<Encargado> listarEncargados() { return bd.encargados().listar(); }
    public List<Disenador> listarDisenadores() { return bd.disenadores().listar(); }
    public List<Disciplina> listarDisciplinas() { return bd.disciplinas().listar(); }
    public List<Pedido> listarPedidos() { return bd.pedidos().listar(); }

    public Cliente buscarClientePorId(int idCliente) { return bd.clientes().buscarPorId(idCliente); }
    public Encargado buscarEncargadoPorId(int idEncargado) { return bd.encargados().buscarPorId(idEncargado); }
    public Disenador buscarDisenadorPorId(int idDisenador) { return bd.disenadores().buscarPorId(idDisenador); }
}
