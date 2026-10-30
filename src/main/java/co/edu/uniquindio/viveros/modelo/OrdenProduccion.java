package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;

/** Orden que se le manda al taller para fabricar los uniformes. */
public class OrdenProduccion {

    public static final String ENVIADA = "ENVIADA";
    public static final String TERMINADA = "TERMINADA";

    private int idOrden;
    private LocalDate fechaEnvio;
    private String estado;

    public OrdenProduccion() {
    }

    public OrdenProduccion(int idOrden, LocalDate fechaEnvio, String estado) {
        this.idOrden = idOrden;
        this.fechaEnvio = fechaEnvio;
        this.estado = estado;
    }

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public LocalDate getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDate fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
