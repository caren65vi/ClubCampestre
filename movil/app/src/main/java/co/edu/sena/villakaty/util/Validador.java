package co.edu.sena.villakaty.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Reglas de validación del formulario de registro (las mismas del front-end web y del back-end).
 * No depende de Android, por eso se prueba con pruebas unitarias en la JVM.
 */
public final class Validador {

    private static final Pattern SOLO_LETRAS = Pattern.compile("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' ]+$");
    private static final Pattern DOCUMENTO = Pattern.compile("^\\d{5,20}$");
    private static final Pattern CELULAR = Pattern.compile("^\\d{7,10}$");
    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private Validador() {
    }

    /**
     * Valida los campos del registro y devuelve un mapa campo → mensaje de error (vacío si todo es válido).
     */
    public static Map<String, String> validarRegistro(String documento, String primerNombre, String segundoNombre,
            String primerApellido, String segundoApellido, String celular, String correo, String fecha, String salario) {
        Map<String, String> errores = new LinkedHashMap<>();

        if (vacio(documento)) {
            errores.put("documento", "Escribe el número de documento.");
        } else if (!DOCUMENTO.matcher(documento.trim()).matches()) {
            errores.put("documento", "Usa solo números, entre 5 y 20 dígitos.");
        }
        validarNombre(errores, "primerNombre", primerNombre, true, "Escribe el primer nombre.");
        validarNombre(errores, "segundoNombre", segundoNombre, false, null);
        validarNombre(errores, "primerApellido", primerApellido, true, "Escribe el primer apellido.");
        validarNombre(errores, "segundoApellido", segundoApellido, false, null);

        String celularLimpio = celular == null ? "" : celular.replace(" ", "");
        if (celularLimpio.isEmpty()) {
            errores.put("celular", "Escribe el número de celular.");
        } else if (!CELULAR.matcher(celularLimpio).matches()) {
            errores.put("celular", "Usa solo números, entre 7 y 10 dígitos.");
        }

        if (vacio(correo)) {
            errores.put("correo", "Escribe el correo electrónico.");
        } else if (!CORREO.matcher(correo.trim()).matches()) {
            errores.put("correo", "Escribe un correo válido, por ejemplo nombre@correo.com.");
        }

        if (vacio(fecha)) {
            errores.put("fecha", "Escribe la fecha de ingreso (AAAA-MM-DD).");
        } else {
            try {
                if (LocalDate.parse(fecha.trim()).isAfter(LocalDate.now())) {
                    errores.put("fecha", "La fecha de ingreso no puede ser futura.");
                }
            } catch (DateTimeParseException e) {
                errores.put("fecha", "Usa el formato AAAA-MM-DD, por ejemplo 2026-10-01.");
            }
        }

        if (vacio(salario)) {
            errores.put("salario", "Escribe el salario.");
        } else {
            try {
                if (Double.parseDouble(salario.trim()) <= 0) {
                    errores.put("salario", "El salario debe ser mayor a 0.");
                }
            } catch (NumberFormatException e) {
                errores.put("salario", "El salario debe ser un número.");
            }
        }
        return errores;
    }

    /** El motivo es obligatorio al cambiar el estado y no puede superar 300 caracteres. */
    public static String validarMotivo(String motivo) {
        if (vacio(motivo)) {
            return "Escribe el motivo o número de acta.";
        }
        return motivo.trim().length() > 300 ? "El motivo no puede superar 300 caracteres." : null;
    }

    private static void validarNombre(Map<String, String> errores, String campo, String valor,
            boolean obligatorio, String mensajeVacio) {
        if (vacio(valor)) {
            if (obligatorio) {
                errores.put(campo, mensajeVacio);
            }
        } else if (!SOLO_LETRAS.matcher(valor.trim()).matches()) {
            errores.put(campo, "Usa solo letras.");
        }
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
