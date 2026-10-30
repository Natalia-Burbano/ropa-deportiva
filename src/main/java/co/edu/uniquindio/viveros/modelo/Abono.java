package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;

/** Pago parcial que hace el cliente. Cada abono genera un comprobante. */
public class Abono {

    private int idAbono;
    private LocalDate fecha;
    private double valor;
    private String metodoPago; // Efectivo, Nequi, Bancolombia...
    private ComprobanteAbono comprobante; // Abono 1 -- 1 ComprobanteAbono

    public Abono() {
    }

    public Abono(int idAbono, LocalDate fecha, double valor, String metodoPago) {
        this.idAbono = idAbono;
        this.fecha = fecha;
        this.valor = valor;
        this.metodoPago = metodoPago;
    }

    public int getIdAbono() { return idAbono; }
    public void setIdAbono(int idAbono) { this.idAbono = idAbono; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public ComprobanteAbono getComprobante() { return comprobante; }
    public void setComprobante(ComprobanteAbono comprobante) { this.comprobante = comprobante; }
}
