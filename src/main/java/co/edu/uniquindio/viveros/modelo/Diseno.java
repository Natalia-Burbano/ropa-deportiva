package co.edu.uniquindio.viveros.modelo;

import java.time.LocalDate;

/** Diseño del uniforme. Solo una versión puede quedar aprobada por pedido. */
public class Diseno {

    public static final String EN_REVISION = "EN_REVISION";
    public static final String APROBADO = "APROBADO";

    private int idDiseno;
    private String origen;          // Ruta de la foto o imagen del diseño
    private LocalDate fechaAprobacion;
    private String medioRecepcion;  // Punto de venta, WhatsApp o Instagram
    private String estado;
    private int idDisenador;        // Disenador 1 -- 0..* Diseno

    public Diseno() {
    }

    public Diseno(int idDiseno, String origen, LocalDate fechaAprobacion, String medioRecepcion,
                  String estado, int idDisenador) {
        this.idDiseno = idDiseno;
        this.origen = origen;
        this.fechaAprobacion = fechaAprobacion;
        this.medioRecepcion = medioRecepcion;
        this.estado = estado;
        this.idDisenador = idDisenador;
    }

    public int getIdDiseno() { return idDiseno; }
    public void setIdDiseno(int idDiseno) { this.idDiseno = idDiseno; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDate fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getMedioRecepcion() { return medioRecepcion; }
    public void setMedioRecepcion(String medioRecepcion) { this.medioRecepcion = medioRecepcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdDisenador() { return idDisenador; }
    public void setIdDisenador(int idDisenador) { this.idDisenador = idDisenador; }
}
