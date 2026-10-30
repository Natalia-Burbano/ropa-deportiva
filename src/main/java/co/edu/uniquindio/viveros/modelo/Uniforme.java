package co.edu.uniquindio.viveros.modelo;

/** Una prenda del pedido, personalizada para un jugador. */
public class Uniforme {

    private int idUniforme;
    private String nombreJugador;
    private int numero;
    private String talla;
    private Disciplina disciplina; // Uniforme 1..* -- 1 Disciplina

    public Uniforme() {
    }

    public Uniforme(int idUniforme, String nombreJugador, int numero, String talla, Disciplina disciplina) {
        this.idUniforme = idUniforme;
        this.nombreJugador = nombreJugador;
        this.numero = numero;
        this.talla = talla;
        this.disciplina = disciplina;
    }

    public int getIdUniforme() { return idUniforme; }
    public void setIdUniforme(int idUniforme) { this.idUniforme = idUniforme; }

    public String getNombreJugador() { return nombreJugador; }
    public void setNombreJugador(String nombreJugador) { this.nombreJugador = nombreJugador; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public Disciplina getDisciplina() { return disciplina; }
    public void setDisciplina(Disciplina disciplina) { this.disciplina = disciplina; }
}
