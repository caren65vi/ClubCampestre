package co.edu.sena.villakaty.datos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.edu.sena.villakaty.modelo.Catalogo;
import co.edu.sena.villakaty.modelo.Empleado;

/**
 * Convierte entre JSON de la API y objetos del modelo.
 * Se separa de ApiCliente para poder probarlo con pruebas unitarias sin red.
 */
public final class EmpleadoParser {

    private EmpleadoParser() {
    }

    public static Empleado empleadoDesdeJson(JSONObject json) {
        Empleado empleado = new Empleado();
        empleado.setNumeroDocumento(json.optString("numeroDocumento"));
        empleado.setIdTipoDocumento(json.has("idTipoDocumento") ? json.optInt("idTipoDocumento") : null);
        empleado.setNombreTipoDocumento(texto(json, "nombreTipoDocumento"));
        empleado.setPrimerNombre(texto(json, "primerNombre"));
        empleado.setSegundoNombre(texto(json, "segundoNombre"));
        empleado.setPrimerApellido(texto(json, "primerApellido"));
        empleado.setSegundoApellido(texto(json, "segundoApellido"));
        empleado.setTelefono(texto(json, "telefono"));
        empleado.setCorreo(texto(json, "correo"));
        empleado.setIdCargo(json.has("idCargo") ? json.optInt("idCargo") : null);
        empleado.setNombreCargo(texto(json, "nombreCargo"));
        empleado.setNombreArea(texto(json, "nombreArea"));
        empleado.setFechaContratacion(texto(json, "fechaContratacion"));
        empleado.setSalario(json.optDouble("salario", 0));
        empleado.setEstado(json.optString("estado", Empleado.ESTADO_ACTIVO));
        return empleado;
    }

    public static List<Empleado> listaDesdeJson(String texto) throws JSONException {
        JSONArray arreglo = new JSONArray(texto);
        List<Empleado> empleados = new ArrayList<>();
        for (int i = 0; i < arreglo.length(); i++) {
            empleados.add(empleadoDesdeJson(arreglo.getJSONObject(i)));
        }
        return empleados;
    }

    /** Lee un catálogo usando los nombres de campo indicados (por ejemplo idCargo / nombreCargo). */
    public static List<Catalogo> catalogoDesdeJson(String texto, String campoId, String campoNombre) throws JSONException {
        JSONArray arreglo = new JSONArray(texto);
        List<Catalogo> elementos = new ArrayList<>();
        for (int i = 0; i < arreglo.length(); i++) {
            JSONObject item = arreglo.getJSONObject(i);
            elementos.add(new Catalogo(item.getInt(campoId), item.optString(campoNombre)));
        }
        return elementos;
    }

    /** Cargos con su área entre paréntesis, por ejemplo "Recepcionista (Recepción)". */
    public static List<Catalogo> cargosDesdeJson(String texto) throws JSONException {
        JSONArray arreglo = new JSONArray(texto);
        List<Catalogo> cargos = new ArrayList<>();
        for (int i = 0; i < arreglo.length(); i++) {
            JSONObject item = arreglo.getJSONObject(i);
            cargos.add(new Catalogo(item.getInt("idCargo"),
                    item.optString("nombreCargo") + " (" + item.optString("nombreArea") + ")"));
        }
        return cargos;
    }

    /** Cuerpo JSON para registrar un empleado (POST /api/empleados). */
    public static JSONObject empleadoAJson(Empleado e) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("numeroDocumento", e.getNumeroDocumento());
        json.put("idTipoDocumento", e.getIdTipoDocumento());
        json.put("primerNombre", e.getPrimerNombre());
        json.put("segundoNombre", vacioANulo(e.getSegundoNombre()));
        json.put("primerApellido", e.getPrimerApellido());
        json.put("segundoApellido", vacioANulo(e.getSegundoApellido()));
        json.put("telefono", e.getTelefono());
        json.put("indicativo", "+57");
        json.put("correo", e.getCorreo());
        json.put("idCargo", e.getIdCargo());
        json.put("fechaContratacion", e.getFechaContratacion());
        json.put("salario", e.getSalario());
        json.put("crearUsuario", false);
        return json;
    }

    /** Extrae el mensaje de error de una respuesta {"error": "..."}; si no se puede, devuelve el texto por defecto. */
    public static String mensajeDeError(String cuerpo, String porDefecto) {
        try {
            String mensaje = new JSONObject(cuerpo).optString("error", "");
            return mensaje.isEmpty() ? porDefecto : mensaje;
        } catch (JSONException | NullPointerException e) {
            return porDefecto;
        }
    }

    private static String texto(JSONObject json, String campo) {
        return json.isNull(campo) ? null : json.optString(campo, null);
    }

    private static Object vacioANulo(String valor) {
        return valor == null || valor.trim().isEmpty() ? JSONObject.NULL : valor.trim();
    }
}
