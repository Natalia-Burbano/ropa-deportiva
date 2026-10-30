package co.edu.uniquindio.viveros.persistencia;

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
import co.edu.uniquindio.viveros.util.Fechas;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos de prueba que se cargan la primera vez que se abre la app:
 * 12 clientes y 20 pedidos con estados mezclados (5 de cada estado).
 * Para volver a empezar desde cero, borren la carpeta "data".
 */
final class DatosPrueba {

    private static final String[] JUGADORES = {
            "Santiago", "Mateo", "Samuel", "Sebastián", "Nicolás", "Alejandro", "Daniel", "Tomás",
            "Valentina", "Sofía", "Mariana", "Isabella", "Gabriela", "Camila", "Juliana", "Laura"
    };
    private static final String[] TALLAS = {"S", "M", "L", "XL"};
    private static final String[] PRENDAS = {"Camiseta", "Camiseta y pantaloneta", "Camiseta, pantaloneta y medias"};
    private static final double[] PRECIOS = {45000, 55000, 65000};
    private static final String[] CANALES = {"Punto de venta", "WhatsApp", "Instagram"};
    private static final String[] PAGOS = {"Efectivo", "Nequi", "Bancolombia"};

    private DatosPrueba() {
    }

    static void cargar(BaseDatos bd) {
        // Encargados, diseñadores y disciplinas
        bd.encargados().listar().addAll(List.of(
                new Encargado(1, "Marcela Viveros", "Encargada"),
                new Encargado(2, "Carlos Muñoz", "Vendedor"),
                new Encargado(3, "Paola Rosero", "Vendedora")));
        bd.disenadores().listar().addAll(List.of(
                new Disenador(1, "Andrés Benavides", "3104567890"),
                new Disenador(2, "Luisa Ortega", "3156781234")));
        List<Disciplina> disciplinas = List.of(
                new Disciplina(1, "Fútbol"),
                new Disciplina(2, "Microfútbol"),
                new Disciplina(3, "Baloncesto"),
                new Disciplina(4, "Voleibol"));
        bd.disciplinas().listar().addAll(disciplinas);

        // Clientes (capitanes o delegados de equipo)
        String[][] clientes = {
                {"Jhon Jairo Cabrera", "3112345678"}, {"María Fernanda López", "3209876543"},
                {"Óscar Jiménez", "3001112233"}, {"Diana Marcela Ruiz", "3145556677"},
                {"Felipe Guerrero", "3018889900"}, {"Luz Ángela Mora", "3176543210"},
                {"Héctor Fabio Rojas", "3123334455"}, {"Sandra Pantoja", "3187776655"},
                {"Wilson Chamorro", "3009990011"}, {"Yuli Paola Bastidas", "3194442211"},
                {"Edwin Ordóñez", "3136667788"}, {"Carolina Erazo", "3051231234"}};
        for (int i = 0; i < clientes.length; i++) {
            bd.clientes().listar().add(new Cliente(i + 1, clientes[i][0], clientes[i][1],
                    CANALES[i % CANALES.length], (i % 3) + 1));
        }

        // Pedidos
        String[] equipos = {
                "Deportivo Puerto Asís", "Club Los Halcones", "Colegio Santa María", "Atlético Mocoa",
                "Real Putumayo", "Leones FC", "Las Panteras", "Juventud Unida", "Club Amazonas",
                "Tigres del Sur", "Selección Docentes", "Inter Barrio Centro", "Águilas Doradas",
                "Club Voley Orito", "Estrellas del Caquetá", "Los Galácticos", "Fénix FC",
                "Colegio Ciudad Sur", "Club Baloncesto Sibundoy", "Veteranos Unidos"};
        String[] estados = {Pedido.SIN_CONFIRMAR, Pedido.CONFIRMADO, Pedido.EN_PRODUCCION, Pedido.ENTREGADO};

        int idUniforme = 1;
        int idAbono = 1;
        LocalDate base = LocalDate.of(2026, 9, 1);

        for (int i = 0; i < equipos.length; i++) {
            int id = i + 1;
            String estado = estados[i % estados.length];
            LocalDate fechaSolicitud = base.plusDays(i * 2L);
            Disciplina disciplina = disciplinas.get(i % disciplinas.size());

            Pedido p = new Pedido();
            p.setIdPedido(id);
            p.setFechaSolicitud(fechaSolicitud);
            p.setNombreGrupo(equipos[i]);
            p.setTipoPrenda(PRENDAS[i % PRENDAS.length]);
            p.setValorUnitario(PRECIOS[i % PRECIOS.length]);
            p.setIdCliente((i % clientes.length) + 1);
            p.setIdEncargado((i % 3) + 1);
            p.setEstado(estado);

            // Uniformes: entre 6 y 14 por pedido, todos completos
            int cantidad = 6 + (i % 9);
            for (int u = 0; u < cantidad; u++) {
                p.getUniformes().add(new Uniforme(idUniforme++,
                        JUGADORES[(i + u) % JUGADORES.length], u + 1,
                        TALLAS[(i + u) % TALLAS.length], disciplina));
            }

            double total = p.getValorTotal();
            boolean sinConfirmar = estado.equals(Pedido.SIN_CONFIRMAR);

            // Cotización: en los pedidos sin confirmar, algunas siguen pendientes
            String estadoCotizacion = (sinConfirmar && i % 8 == 0) ? Cotizacion.PENDIENTE : Cotizacion.ACEPTADA;
            p.setCotizacion(new Cotizacion(id, fechaSolicitud, total, estadoCotizacion));

            // Diseño: aprobado en todos menos en los sin confirmar
            String canal = CANALES[i % CANALES.length];
            if (sinConfirmar) {
                p.setDiseno(new Diseno(id, "disenos/pedido-" + id + "-v1.png", null, canal,
                        Diseno.EN_REVISION, (i % 2) + 1));
            } else {
                LocalDate aprobacion = fechaSolicitud.plusDays(2);
                p.setDiseno(new Diseno(id, "disenos/pedido-" + id + "-final.png", aprobacion, canal,
                        Diseno.APROBADO, (i % 2) + 1));
                p.setFechaConfirmacionDiseno(aprobacion);
            }

            // Crédito y abonos
            Credito credito = new Credito(id, fechaSolicitud, Credito.ABIERTO);
            LocalDate fechaAbono = fechaSolicitud.plusDays(3);
            String pago = PAGOS[i % PAGOS.length];
            if (sinConfirmar) {
                // Abono menor al 50%: por eso el pedido no se puede confirmar
                if (estadoCotizacion.equals(Cotizacion.ACEPTADA)) {
                    idAbono = agregarAbono(credito, idAbono, fechaAbono, total * 0.3, pago, total);
                }
            } else {
                idAbono = agregarAbono(credito, idAbono, fechaAbono, total * 0.5, pago, total);
                p.setFechaEntregaEstimada(Fechas.sumarDiasHabiles(fechaAbono, Fechas.DIAS_HABILES_ENTREGA));
            }
            if (estado.equals(Pedido.ENTREGADO)) {
                // Al entregar, el cliente paga el saldo
                idAbono = agregarAbono(credito, idAbono, p.getFechaEntregaEstimada(),
                        credito.getSaldoPendiente(total), "Efectivo", total);
                credito.setEstado(Credito.PAGADO);
            }
            p.setCredito(credito);

            // Orden de producción
            if (estado.equals(Pedido.EN_PRODUCCION)) {
                p.setOrdenProduccion(new OrdenProduccion(id, fechaAbono.plusDays(1), OrdenProduccion.ENVIADA));
            } else if (estado.equals(Pedido.ENTREGADO)) {
                p.setOrdenProduccion(new OrdenProduccion(id, fechaAbono.plusDays(1), OrdenProduccion.TERMINADA));
            }

            bd.pedidos().listar().add(p);
        }

        bd.encargados().guardar();
        bd.disenadores().guardar();
        bd.disciplinas().guardar();
        bd.clientes().guardar();
        bd.pedidos().guardar();
    }

    private static int agregarAbono(Credito credito, int idAbono, LocalDate fecha, double valor,
                                    String metodoPago, double totalPedido) {
        Abono abono = new Abono(idAbono, fecha, valor, metodoPago);
        credito.getAbonos().add(abono);
        abono.setComprobante(new ComprobanteAbono(idAbono, fecha, valor, credito.getSaldoPendiente(totalPedido)));
        return idAbono + 1;
    }
}
