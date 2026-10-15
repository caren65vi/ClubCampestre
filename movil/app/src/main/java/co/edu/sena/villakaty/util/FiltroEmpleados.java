package co.edu.sena.villakaty.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.edu.sena.villakaty.modelo.Empleado;

/**
 * RF-0112: filtra la lista por texto (nombre, documento o cargo, sin distinguir tildes ni mayúsculas) y por estado.
 */
public final class FiltroEmpleados {

    private FiltroEmpleados() {
    }

    /**
     * @param texto  texto buscado (vacío = todos)
     * @param estado activo, vacaciones, inactivo o null para todos
     */
    public static List<Empleado> filtrar(List<Empleado> empleados, String texto, String estado) {
        String buscado = normalizar(texto);
        List<Empleado> resultado = new ArrayList<>();
        for (Empleado e : empleados) {
            boolean coincideEstado = estado == null || estado.equals(e.getEstado());
            String contenido = normalizar(e.getNombreCompleto() + " " + e.getNumeroDocumento() + " " + e.getNombreCargo());
            if (coincideEstado && (buscado.isEmpty() || contenido.contains(buscado))) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /** Cuenta los empleados en un estado. */
    public static int contar(List<Empleado> empleados, String estado) {
        int total = 0;
        for (Empleado e : empleados) {
            if (estado.equals(e.getEstado())) {
                total++;
            }
        }
        return total;
    }

    static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase(Locale.ROOT).trim();
    }
}
