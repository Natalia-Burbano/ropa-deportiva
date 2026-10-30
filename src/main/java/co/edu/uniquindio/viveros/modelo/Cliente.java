package co.edu.uniquindio.viveros.modelo;

/** Persona que pide los uniformes (capitán o delegado del equipo). */
public class Cliente {

    private int idCliente;
    private String nombre;
    private String telefono;
    private String canalContacto; // Punto de venta, WhatsApp o Instagram
    private int idEncargado;      // Encargado que lo registró (Encargado 1 -- 0..* Cliente)

    public Cliente() {
    }

    public Cliente(int idCliente, String nombre, String telefono, String canalContacto, int idEncargado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.canalContacto = canalContacto;
        this.idEncargado = idEncargado;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCanalContacto() { return canalContacto; }
    public void setCanalContacto(String canalContacto) { this.canalContacto = canalContacto; }

    public int getIdEncargado() { return idEncargado; }
    public void setIdEncargado(int idEncargado) { this.idEncargado = idEncargado; }

    @Override
    public String toString() {
        return nombre + " (" + telefono + ")";
    }
}
