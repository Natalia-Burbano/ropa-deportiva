package co.edu.uniquindio.viveros.modelo;

/** Persona que elabora el diseño del uniforme. */
public class Disenador {

    private int idDisenador;
    private String nombre;
    private String telefono;

    public Disenador() {
    }

    public Disenador(int idDisenador, String nombre, String telefono) {
        this.idDisenador = idDisenador;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getIdDisenador() { return idDisenador; }
    public void setIdDisenador(int idDisenador) { this.idDisenador = idDisenador; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return nombre;
    }
}
