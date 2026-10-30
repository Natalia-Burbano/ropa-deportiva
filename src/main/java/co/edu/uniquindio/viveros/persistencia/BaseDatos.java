package co.edu.uniquindio.viveros.persistencia;

import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Disciplina;
import co.edu.uniquindio.viveros.modelo.Disenador;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.Pedido;

import java.nio.file.Path;

/**
 * Punto único para llegar a todos los archivos de datos.
 * Los archivos quedan en la carpeta "data" del proyecto.
 * Si la carpeta está vacía, se llenan con los datos de prueba.
 */
public final class BaseDatos {

    private static final Path CARPETA = Path.of("data");
    private static BaseDatos instancia;

    private final JsonRepositorio<Cliente> clientes;
    private final JsonRepositorio<Pedido> pedidos;
    private final JsonRepositorio<Encargado> encargados;
    private final JsonRepositorio<Disenador> disenadores;
    private final JsonRepositorio<Disciplina> disciplinas;

    private BaseDatos(Path carpeta) {
        clientes = new JsonRepositorio<>(carpeta.resolve("clientes.json"), Cliente.class, Cliente::getIdCliente);
        pedidos = new JsonRepositorio<>(carpeta.resolve("pedidos.json"), Pedido.class, Pedido::getIdPedido);
        encargados = new JsonRepositorio<>(carpeta.resolve("encargados.json"), Encargado.class, Encargado::getIdEncargado);
        disenadores = new JsonRepositorio<>(carpeta.resolve("disenadores.json"), Disenador.class, Disenador::getIdDisenador);
        disciplinas = new JsonRepositorio<>(carpeta.resolve("disciplinas.json"), Disciplina.class, Disciplina::getIdDisciplina);

        if (clientes.estaVacio() && pedidos.estaVacio()) {
            DatosPrueba.cargar(this);
        }
    }

    public static BaseDatos get() {
        if (instancia == null) {
            instancia = new BaseDatos(CARPETA);
        }
        return instancia;
    }

    public JsonRepositorio<Cliente> clientes() { return clientes; }
    public JsonRepositorio<Pedido> pedidos() { return pedidos; }
    public JsonRepositorio<Encargado> encargados() { return encargados; }
    public JsonRepositorio<Disenador> disenadores() { return disenadores; }
    public JsonRepositorio<Disciplina> disciplinas() { return disciplinas; }
}
