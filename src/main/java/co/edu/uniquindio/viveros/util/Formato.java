package co.edu.uniquindio.viveros.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Ayudas para mostrar y leer pesos y fechas en las pantallas. */
public final class Formato {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat MILES;

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        MILES = new DecimalFormat("#,##0", simbolos);
    }

    private Formato() {
    }

    /** 130000 → "$130.000" */
    public static String pesos(double valor) {
        return "$" + MILES.format(Math.round(valor));
    }

    /** Lee lo que escribe el usuario: "130000", "130.000" o "$130.000" → 130000. Vacío → 0. */
    public static double leerPesos(String texto) {
        if (texto == null) {
            return 0;
        }
        String soloNumeros = texto.replaceAll("[^0-9]", "");
        return soloNumeros.isEmpty() ? 0 : Double.parseDouble(soloNumeros);
    }

    /** 2026-10-30 → "30/10/2026" */
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }
}
