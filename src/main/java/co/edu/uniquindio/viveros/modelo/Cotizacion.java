package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;

/** Valor que se le presenta al cliente antes de pagar. */
public class Cotizacion {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ACEPTADA = "ACEPTADA";
    public static final String RECHAZADA = "RECHAZADA";

    private int idCotizacion;
    private LocalDate fecha;
    private double valorTotal;
    private String estado;

    public Cotizacion() {
    }

    public Cotizacion(int idCotizacion, LocalDate fecha, double valorTotal, String estado) {
        this.idCotizacion = idCotizacion;
        this.fecha = fecha;
        this.valorTotal = valorTotal;
        this.estado = estado;
    }

    public int getIdCotizacion() { return idCotizacion; }
    public void setIdCotizacion(int idCotizacion) { this.idCotizacion = idCotizacion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
