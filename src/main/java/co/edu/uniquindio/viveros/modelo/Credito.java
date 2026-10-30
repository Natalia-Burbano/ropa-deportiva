package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Agrupa los abonos de un pedido. Lleva lo pagado y lo que falta. */
public class Credito {

    public static final String ABIERTO = "ABIERTO";
    public static final String PAGADO = "PAGADO";

    private int idCredito;
    private LocalDate fechaCreacion;
    private String estado;
    private List<Abono> abonos = new ArrayList<>(); // Credito 1 -- 1..* Abono

    public Credito() {
    }

    public Credito(int idCredito, LocalDate fechaCreacion, String estado) {
        this.idCredito = idCredito;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
    }

    /** Atributo derivado /valorAbonado: suma de todos los abonos. */
    public double getValorAbonado() {
        double suma = 0;
        for (Abono abono : abonos) {
            suma += abono.getValor();
        }
        return suma;
    }

    /** Atributo derivado /saldoPendiente: lo que falta por pagar del pedido. */
    public double getSaldoPendiente(double valorTotalPedido) {
        return valorTotalPedido - getValorAbonado();
    }

    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Abono> getAbonos() { return abonos; }
    public void setAbonos(List<Abono> abonos) { this.abonos = abonos; }
}
