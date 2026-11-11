package co.edu.uniquindio.viveros.vista;

import co.edu.uniquindio.viveros.modelo.Diseno;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Muestra la imagen del diseño de un pedido.
 * Si la imagen no existe (por ejemplo en los datos de prueba), dibuja una camiseta verde con las iniciales del equipo.
 */
public final class ImagenDiseno {

    /** Carpeta donde se guardan las copias de las imágenes de diseño. */
    public static final Path CARPETA_DISENOS = Path.of("data", "disenos");

    // Silueta de una camiseta, en un cuadro de 100 x 100
    private static final String CAMISETA =
            "M30 10 L42 5 Q50 15 58 5 L70 10 L94 28 L83 45 L72 38 L72 95 L28 95 L28 38 L17 45 L6 28 Z";

    private static final Map<String, Image> CACHE = new HashMap<>();

    private ImagenDiseno() {
    }

    /** Busca el archivo de la imagen: la ruta tal cual o dentro de la carpeta data. Null si no existe. */
    public static File archivo(Diseno diseno) {
        if (diseno == null || diseno.getOrigen() == null || diseno.getOrigen().isBlank()) {
            return null;
        }
        try {
            Path ruta = Path.of(diseno.getOrigen());
            for (Path candidato : new Path[]{ruta, Path.of("data").resolve(ruta)}) {
                if (Files.isRegularFile(candidato)) {
                    return candidato.toFile();
                }
            }
        } catch (InvalidPathException e) {
            // Ruta con caracteres raros: se trata como si no existiera
        }
        return null;
    }

    /**
     * Copia la imagen escogida a data/disenos para que el pedido no dependa de dónde estaba el archivo original.
     * Devuelve la ruta de la copia.
     */
    public static File guardarCopia(File original) {
        try {
            Path carpeta = CARPETA_DISENOS.toAbsolutePath();
            if (original.toPath().toAbsolutePath().startsWith(carpeta)) {
                return original; // ya es una copia guardada
            }
            Files.createDirectories(carpeta);
            String nombre = System.currentTimeMillis() + "-" + original.getName().replaceAll("[^A-Za-z0-9._-]", "_");
            Path destino = carpeta.resolve(nombre);
            Files.copy(original.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toFile();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen del diseño", e);
        }
    }

    /**
     * Crea el cuadro con la imagen del diseño, de ancho x alto y con esquinas redondeadas.
     * @param texto texto para la camiseta de reemplazo (normalmente el nombre del equipo).
     */
    public static Node crear(Diseno diseno, String texto, double ancho, double alto) {
        StackPane caja = new StackPane();
        caja.setMinSize(ancho, alto);
        caja.setPrefSize(ancho, alto);
        caja.setMaxSize(ancho, alto);

        Image imagen = cargar(archivo(diseno), ancho, alto);
        if (imagen != null) {
            caja.getStyleClass().add("diseno-foto");
            ImageView vista = new ImageView(imagen);
            vista.setPreserveRatio(true);
            vista.setSmooth(true);
            vista.setFitWidth(ancho);
            vista.setFitHeight(alto);
            caja.getChildren().add(vista);
        } else {
            caja.getStyleClass().add("diseno-placeholder");
            SVGPath camiseta = new SVGPath();
            camiseta.setContent(CAMISETA);
            camiseta.getStyleClass().add("diseno-camiseta");
            double escala = Math.min(ancho, alto) * 0.78 / 100.0;
            camiseta.setScaleX(escala);
            camiseta.setScaleY(escala);

            Label iniciales = new Label(iniciales(texto));
            iniciales.getStyleClass().add("diseno-iniciales");
            iniciales.setStyle("-fx-font-size: " + Math.max(10, Math.round(Math.min(ancho, alto) * 0.17)) + "px;");
            StackPane.setAlignment(iniciales, Pos.CENTER);
            caja.getChildren().addAll(camiseta, iniciales);

            if (ancho >= 160) {
                Label aviso = new Label("Vista de ejemplo · sin foto cargada");
                aviso.getStyleClass().add("diseno-aviso");
                StackPane.setAlignment(aviso, Pos.BOTTOM_CENTER);
                caja.getChildren().add(aviso);
            }
        }

        // Etiqueta si el diseño no está aprobado
        if (diseno != null && !Diseno.APROBADO.equals(diseno.getEstado())) {
            Label revision = new Label("En revisión");
            revision.getStyleClass().add("diseno-cinta");
            StackPane.setAlignment(revision, Pos.TOP_LEFT);
            caja.getChildren().add(revision);
        }

        // Esquinas redondeadas
        Rectangle recorte = new Rectangle(ancho, alto);
        recorte.setArcWidth(16);
        recorte.setArcHeight(16);
        caja.setClip(recorte);
        return caja;
    }

    private static Image cargar(File archivo, double ancho, double alto) {
        if (archivo == null) {
            return null;
        }
        // Se carga al doble del tamaño para que se vea nítida en pantallas con escala
        String clave = archivo.getAbsolutePath() + "|" + archivo.lastModified() + "|" + ancho + "x" + alto;
        Image imagen = CACHE.computeIfAbsent(clave,
                k -> new Image(archivo.toURI().toString(), ancho * 2, alto * 2, true, true));
        return imagen.isError() ? null : imagen;
    }

    /** "Club Los Halcones" → "CL" */
    private static String iniciales(String texto) {
        if (texto == null || texto.isBlank()) {
            return "VR";
        }
        StringBuilder sb = new StringBuilder();
        for (String palabra : texto.trim().split("\\s+")) {
            if (!palabra.isEmpty() && Character.isLetterOrDigit(palabra.charAt(0))) {
                sb.append(Character.toUpperCase(palabra.charAt(0)));
            }
            if (sb.length() == 2) {
                break;
            }
        }
        return sb.length() == 0 ? "VR" : sb.toString();
    }
}
