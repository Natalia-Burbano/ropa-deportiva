package co.edu.uniquindio.viveros.modelo;

/** Deporte al que pertenece el uniforme (fútbol, baloncesto, etc.). */
public class Disciplina {

    private int idDisciplina;
    private String nombre;

    public Disciplina() {
    }

    public Disciplina(int idDisciplina, String nombre) {
        this.idDisciplina = idDisciplina;
        this.nombre = nombre;
    }

    public int getIdDisciplina() { return idDisciplina; }
    public void setIdDisciplina(int idDisciplina) { this.idDisciplina = idDisciplina; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
