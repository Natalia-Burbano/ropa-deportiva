package co.edu.uniquindio.viveros.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Ayudas para manejar fechas. */
public final class Fechas {

    /**
     * Regla 002: la entrega es la fecha del abono + 8 a 10 días hábiles.
     * Para poder programarlo se usa un número fijo. Cambiar aquí si acuerdan otro.
     */
    public static final int DIAS_HABILES_ENTREGA = 10;

    private Fechas() {
    }

    /** Suma días hábiles (de lunes a viernes) a una fecha. No tiene en cuenta festivos. */
    public static LocalDate sumarDiasHabiles(LocalDate fecha, int dias) {
        LocalDate resultado = fecha;
        int sumados = 0;
        while (sumados < dias) {
            resultado = resultado.plusDays(1);
            DayOfWeek dia = resultado.getDayOfWeek();
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) {
                sumados++;
            }
        }
        return resultado;
    }
}
