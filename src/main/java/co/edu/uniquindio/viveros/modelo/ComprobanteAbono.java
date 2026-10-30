package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;

/** Soporte que se le entrega al cliente por cada abono. */
public class ComprobanteAbono {

    private int idComprobante;
    private LocalDate fechaEmision;
    private double valorAbonado;
    private double saldoPendiente;

    public ComprobanteAbono() {
    }

    public ComprobanteAbono(int idComprobante, LocalDate fechaEmision, double valorAbonado, double saldoPendiente) {
        this.idComprobante = idComprobante;
        this.fechaEmision = fechaEmision;
        this.valorAbonado = valorAbonado;
        this.saldoPendiente = saldoPendiente;
    }

    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public double getValorAbonado() { return valorAbonado; }
    public void setValorAbonado(double valorAbonado) { this.valorAbonado = valorAbonado; }

    public double getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(double saldoPendiente) { this.saldoPendiente = saldoPendiente; }
}
