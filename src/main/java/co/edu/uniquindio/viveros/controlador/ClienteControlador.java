package co.edu.uniquindio.viveros.controlador;

import co.edu.uniquindio.viveros.modelo.Cliente;
import co.edu.uniquindio.viveros.modelo.Encargado;
import co.edu.uniquindio.viveros.modelo.Pedido;
import co.edu.uniquindio.viveros.persistencia.BaseDatos;
import co.edu.uniquindio.viveros.persistencia.JsonRepositorio;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CRUD de Cliente (pantalla 2). Responsable: Integrante 1.
 * En la matriz de trazabilidad estos métodos reemplazan a GET/POST/PATCH/DELETE /clientes.
 *
 * Reglas que valida:
 *  - RN-007 Cliente sin registros duplicados (mismo teléfono o mismo nombre).
 *  - RN-008 Teléfono válido: 10 dígitos, celular (3xx) o fijo (60x).
 *  - RN-009 Todo cliente queda registrado por un encargado.
 *  - RN-010 No se elimina un cliente que tiene pedidos.
 */
public class ClienteControlador {

    // Nombres de los campos (iguales a los atributos de Cliente), para mostrar cada error junto a su campo
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_TELEFONO = "telefono";
    public static final String CAMPO_CANAL_CONTACTO = "canalContacto";
    public static final String CAMPO_ENCARGADO = "idEncargado";
    public static final String CAMPO_GENERAL = "general";

    /** Canales por los que llega un cliente (los mismos de los datos de prueba). */
    public static final List<String> CANALES_CONTACTO = List.of("Punto de venta", "WhatsApp", "Instagram");

    public static final int TAMANO_PAGINA = 8;

    private final JsonRepositorio<Cliente> clientes;
    private final JsonRepositorio<Pedido> pedidos;
    private final JsonRepositorio<Encargado> encargados;

    public ClienteControlador() {
        this(BaseDatos.get().clientes(), BaseDatos.get().pedidos(), BaseDatos.get().encargados());
    }

    /** Constructor para las pruebas: permite usar archivos temporales en vez de la carpeta data. */
    public ClienteControlador(JsonRepositorio<Cliente> clientes, JsonRepositorio<Pedido> pedidos,
                              JsonRepositorio<Encargado> encargados) {
        this.clientes = clientes;
        this.pedidos = pedidos;
        this.encargados = encargados;
    }

    // ---------- GET /clientes ----------

    /** Listar clientes (GET /clientes). */
    public List<Cliente> listar() {
        return new ArrayList<>(clientes.listar());
    }

    /**
     * Buscar cliente por nombre o teléfono (GET /clientes?query=).
     * La usan las dos pantallas: Agregar pedido y CRUD de Cliente.
     * No distingue mayúsculas ni tildes: "oscar" encuentra a "Óscar Jiménez".
     */
    public List<Cliente> buscarCliente(String query) {
        if (query == null || query.isBlank()) {
            return listar();
        }
        String texto = normalizar(query);
        String digitos = soloDigitos(query);
        List<Cliente> encontrados = new ArrayList<>();
        for (Cliente c : clientes.listar()) {
            boolean porNombre = normalizar(c.getNombre()).contains(texto);
            boolean porTelefono = !digitos.isEmpty() && c.getTelefono() != null && c.getTelefono().contains(digitos);
            if (porNombre || porTelefono) {
                encontrados.add(c);
            }
        }
        return encontrados;
    }

    /** Una página del listado (GET /clientes?query=&pagina=). La primera página es la 0. */
    public Pagina<Cliente> listarPaginado(String query, int pagina, int tamano) {
        List<Cliente> todos = buscarCliente(query);
        int totalPaginas = Math.max(1, (int) Math.ceil(todos.size() / (double) tamano));
        int numero = Math.max(0, Math.min(pagina, totalPaginas - 1));
        int desde = numero * tamano;
        int hasta = Math.min(desde + tamano, todos.size());
        List<Cliente> elementos = desde < hasta ? todos.subList(desde, hasta) : List.of();
        return new Pagina<>(new ArrayList<>(elementos), numero, totalPaginas, todos.size());
    }

    public Cliente buscarPorId(int idCliente) {
        return clientes.buscarPorId(idCliente);
    }

    // ---------- POST /clientes ----------

    /** Crear cliente (POST /clientes). Lanza la excepción con los errores por campo si algo no cumple. */
    public Cliente crear(Cliente datos) throws ReglaNegocioException {
        Map<String, String> errores = validar(datos, 0);
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(errores);
        }
        Cliente nuevo = new Cliente(clientes.siguienteId(), limpiarNombre(datos.getNombre()),
                soloDigitos(datos.getTelefono()), datos.getCanalContacto(), datos.getIdEncargado());
        clientes.agregar(nuevo);
        return nuevo;
    }

    // ---------- PATCH /clientes/{id} ----------

    /**
     * Editar cliente (PATCH /clientes/{id}).
     * Recibe una copia con los datos nuevos; solo si son válidos se copian al cliente guardado,
     * así un error no deja datos a medias en memoria.
     */
    public Cliente actualizar(Cliente datos) throws ReglaNegocioException {
        Cliente guardado = clientes.buscarPorId(datos.getIdCliente());
        if (guardado == null) {
            throw new ReglaNegocioException(Map.of(CAMPO_GENERAL, "El cliente ya no existe. Actualice la lista."));
        }
        Map<String, String> errores = validar(datos, datos.getIdCliente());
        if (!errores.isEmpty()) {
            throw new ReglaNegocioException(errores);
        }
        guardado.setNombre(limpiarNombre(datos.getNombre()));
        guardado.setTelefono(soloDigitos(datos.getTelefono()));
        guardado.setCanalContacto(datos.getCanalContacto());
        guardado.setIdEncargado(datos.getIdEncargado());
        clientes.guardar();
        return guardado;
    }

    // ---------- DELETE /clientes/{id} ----------

    /** Eliminar cliente (DELETE /clientes/{id}). RN-010: si tiene pedidos no se elimina, para no dejarlos huérfanos. */
    public void eliminar(int idCliente) throws ReglaNegocioException {
        Cliente cliente = clientes.buscarPorId(idCliente);
        if (cliente == null) {
            throw new ReglaNegocioException(Map.of(CAMPO_GENERAL, "El cliente ya no existe. Actualice la lista."));
        }
        int cantidad = contarPedidos(idCliente);
        if (cantidad > 0) {
            throw new ReglaNegocioException(Map.of(CAMPO_GENERAL,
                    "No se puede eliminar a " + cliente.getNombre() + ": tiene " + cantidad
                            + (cantidad == 1 ? " pedido registrado." : " pedidos registrados.")));
        }
        clientes.eliminar(idCliente);
    }

    // ---------- Validaciones ----------

    /**
     * Revisa los datos del cliente. Devuelve los errores por campo; si está vacío, cumple todas las reglas.
     * @param idExcluir id del cliente que se está editando (0 si es nuevo), para no compararlo consigo mismo.
     */
    public Map<String, String> validar(Cliente datos, int idExcluir) {
        Map<String, String> errores = new LinkedHashMap<>();

        // Nombre
        String nombre = limpiarNombre(datos.getNombre());
        if (nombre.isEmpty()) {
            errores.put(CAMPO_NOMBRE, "Escriba el nombre del cliente.");
        } else if (nombre.length() < 3) {
            errores.put(CAMPO_NOMBRE, "El nombre debe tener al menos 3 letras.");
        } else if (nombre.length() > 80) {
            errores.put(CAMPO_NOMBRE, "El nombre no puede pasar de 80 caracteres.");
        } else if (!nombre.matches("[\\p{L} .'-]+")) {
            errores.put(CAMPO_NOMBRE, "El nombre solo puede tener letras y espacios.");
        }

        // RN-008: teléfono
        String telefonoEscrito = datos.getTelefono() == null ? "" : datos.getTelefono().trim();
        String telefono = soloDigitos(telefonoEscrito);
        if (telefonoEscrito.isEmpty()) {
            errores.put(CAMPO_TELEFONO, "Escriba el teléfono del cliente.");
        } else if (!telefonoEscrito.matches("[0-9\\s-]+")) {
            errores.put(CAMPO_TELEFONO, "El teléfono solo puede tener números.");
        } else if (telefono.length() != 10) {
            errores.put(CAMPO_TELEFONO, "El teléfono debe tener 10 dígitos (tiene " + telefono.length() + ").");
        } else if (!telefono.startsWith("3") && !telefono.startsWith("60")) {
            errores.put(CAMPO_TELEFONO, "Debe ser un celular (empieza por 3) o un fijo (empieza por 60).");
        }

        // Canal de contacto
        if (datos.getCanalContacto() == null || !CANALES_CONTACTO.contains(datos.getCanalContacto())) {
            errores.put(CAMPO_CANAL_CONTACTO, "Escoja por dónde llegó el cliente.");
        }

        // RN-009: encargado que lo registra
        if (encargados.buscarPorId(datos.getIdEncargado()) == null) {
            errores.put(CAMPO_ENCARGADO, "Escoja el encargado que registra al cliente.");
        }

        // RN-007: sin duplicados (solo si el dato en sí está bien escrito)
        for (Cliente otro : clientes.listar()) {
            if (otro.getIdCliente() == idExcluir) {
                continue;
            }
            if (!errores.containsKey(CAMPO_TELEFONO) && telefono.equals(otro.getTelefono())) {
                errores.put(CAMPO_TELEFONO, "Ya existe un cliente con este teléfono: " + otro.getNombre() + ".");
            }
            if (!errores.containsKey(CAMPO_NOMBRE) && normalizar(nombre).equals(normalizar(otro.getNombre()))) {
                errores.put(CAMPO_NOMBRE, "Ya existe un cliente con este nombre (teléfono " + otro.getTelefono() + ").");
            }
        }
        return errores;
    }

    // ---------- Ayudas para la pantalla ----------

    public int contarPedidos(int idCliente) {
        int cantidad = 0;
        for (Pedido p : pedidos.listar()) {
            if (p.getIdCliente() == idCliente) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public List<Encargado> listarEncargados() {
        return encargados.listar();
    }

    public Encargado buscarEncargadoPorId(int idEncargado) {
        return encargados.buscarPorId(idEncargado);
    }

    /** "  óscar   JIMÉNEZ " → "oscar jimenez": para comparar sin tildes, mayúsculas ni espacios de más. */
    static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    /** Quita espacios al inicio y al final y deja uno solo entre palabras. */
    private static String limpiarNombre(String nombre) {
        return nombre == null ? "" : nombre.trim().replaceAll("\\s+", " ");
    }

    /** "311 234-5678" → "3112345678" */
    private static String soloDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("[^0-9]", "");
    }

    /** Resultado de listarPaginado: los clientes de la página y los totales para los botones. */
    public record Pagina<T>(List<T> elementos, int numero, int totalPaginas, int totalElementos) {
        public boolean hayAnterior() { return numero > 0; }
        public boolean haySiguiente() { return numero < totalPaginas - 1; }
    }
}
