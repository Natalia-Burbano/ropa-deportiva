package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Pedido de uniformes de un equipo. Es el centro de la transacción. */
public class Pedido {

    public static final String SIN_CONFIRMAR = "SIN_CONFIRMAR";
    public static final String CONFIRMADO = "CONFIRMADO";
    public static final String EN_PRODUCCION = "EN_PRODUCCION";
    public static final String ENTREGADO = "ENTREGADO";

    private int idPedido;
    private LocalDate fechaSolicitud;
    private String nombreGrupo;
    private String tipoPrenda;
    private double valorUnitario;
    private LocalDate fechaConfirmacionDiseno;
    private LocalDate fechaEntregaEstimada;
    private String estado;

    // Relaciones del diagrama de clases
    private int idCliente;                           // Cliente 1 -- 1..* Pedido
    private int idEncargado;                         // Encargado 1 -- * Pedido
    private Cotizacion cotizacion;                   // Pedido 1 -- 1 Cotizacion
    private List<Uniforme> uniformes = new ArrayList<>(); // Pedido 1 -- * Uniforme
    private Diseno diseno;                           // Pedido 1 -- 1 Diseno
    private Credito credito;                         // Pedido 1 -- 1 Credito
    private OrdenProduccion ordenProduccion;         // Pedido 1 -- 0..1 OrdenProduccion

    public Pedido() {
    }

    /** Atributo derivado /cantidadUniformes. */
    public int getCantidadUniformes() {
        return uniformes.size();
    }

    /** Atributo derivado /valorTotal = cantidad de uniformes x valor unitario. */
    public double getValorTotal() {
        return getCantidadUniformes() * valorUnitario;
    }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getNombreGrupo() { return nombreGrupo; }
    public void setNombreGrupo(String nombreGrupo) { this.nombreGrupo = nombreGrupo; }

    public String getTipoPrenda() { return tipoPrenda; }
    public void setTipoPrenda(String tipoPrenda) { this.tipoPrenda = tipoPrenda; }

    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double valorUnitario) { this.valorUnitario = valorUnitario; }

    public LocalDate getFechaConfirmacionDiseno() { return fechaConfirmacionDiseno; }
    public void setFechaConfirmacionDiseno(LocalDate fechaConfirmacionDiseno) { this.fechaConfirmacionDiseno = fechaConfirmacionDiseno; }

    public LocalDate getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdEncargado() { return idEncargado; }
    public void setIdEncargado(int idEncargado) { this.idEncargado = idEncargado; }

    public Cotizacion getCotizacion() { return cotizacion; }
    public void setCotizacion(Cotizacion cotizacion) { this.cotizacion = cotizacion; }

    public List<Uniforme> getUniformes() { return uniformes; }
    public void setUniformes(List<Uniforme> uniformes) { this.uniformes = uniformes; }

    public Diseno getDiseno() { return diseno; }
    public void setDiseno(Diseno diseno) { this.diseno = diseno; }

    public Credito getCredito() { return credito; }
    public void setCredito(Credito credito) { this.credito = credito; }

    public OrdenProduccion getOrdenProduccion() { return ordenProduccion; }
    public void setOrdenProduccion(OrdenProduccion ordenProduccion) { this.ordenProduccion = ordenProduccion; }
}
