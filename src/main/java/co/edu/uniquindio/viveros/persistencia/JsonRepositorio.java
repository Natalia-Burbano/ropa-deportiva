package co.edu.uniquindio.viveros.persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Guarda una lista de objetos en un archivo JSON.
 * Mientras la app está abierta los datos viven en memoria; cada vez que se llama
 * guardar() se escribe el archivo completo.
 */
public class JsonRepositorio<T> {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    private final Path archivo;
    private final Class<T> tipo;
    private final ToIntFunction<T> obtenerId;
    private List<T> datos = new ArrayList<>();

    public JsonRepositorio(Path archivo, Class<T> tipo, ToIntFunction<T> obtenerId) {
        this.archivo = archivo;
        this.tipo = tipo;
        this.obtenerId = obtenerId;
        cargar();
    }

    public boolean estaVacio() {
        return datos.isEmpty();
    }

    /** Devuelve la lista que está en memoria (los cambios se guardan con guardar()). */
    public List<T> listar() {
        return datos;
    }

    public T buscarPorId(int id) {
        for (T item : datos) {
            if (obtenerId.applyAsInt(item) == id) {
                return item;
            }
        }
        return null;
    }

    /** Siguiente id libre: el mayor id + 1. */
    public int siguienteId() {
        int mayor = 0;
        for (T item : datos) {
            mayor = Math.max(mayor, obtenerId.applyAsInt(item));
        }
        return mayor + 1;
    }

    public void agregar(T item) {
        datos.add(item);
        guardar();
    }

    public boolean eliminar(int id) {
        boolean quitado = datos.removeIf(item -> obtenerId.applyAsInt(item) == id);
        if (quitado) {
            guardar();
        }
        return quitado;
    }

    public void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            try (Writer writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
                GSON.toJson(datos, TypeToken.getParameterized(List.class, tipo).getType(), writer);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + archivo, e);
        }
    }

    private void cargar() {
        if (!Files.exists(archivo)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            List<T> leidos = GSON.fromJson(reader, TypeToken.getParameterized(List.class, tipo).getType());
            if (leidos != null) {
                datos = new ArrayList<>(leidos);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        }
    }
}
