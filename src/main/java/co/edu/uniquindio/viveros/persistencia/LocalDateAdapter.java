package co.edu.uniquindio.viveros.persistencia;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

/** Le enseña a Gson a guardar las fechas como texto "2026-10-29". */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate fecha) throws IOException {
        if (fecha == null) {
            out.nullValue();
        } else {
            out.value(fecha.toString());
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return LocalDate.parse(in.nextString());
    }
}
