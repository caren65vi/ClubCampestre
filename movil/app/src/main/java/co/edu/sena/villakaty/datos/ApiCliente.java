package co.edu.sena.villakaty.datos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import co.edu.sena.villakaty.BuildConfig;
import co.edu.sena.villakaty.modelo.Catalogo;
import co.edu.sena.villakaty.modelo.Empleado;

/**
 * Cliente HTTP de la API REST de Villa Katy (mismos endpoints que usa el front-end web).
 * Todos los métodos son bloqueantes: se deben llamar desde un hilo en segundo plano.
 */
public class ApiCliente {

    private static final int TIEMPO_ESPERA_MS = 10000;
    private final String urlBase;
    private final SesionManager sesion;

    public ApiCliente(SesionManager sesion) {
        this(BuildConfig.API_URL, sesion);
    }

    public ApiCliente(String urlBase, SesionManager sesion) {
        this.urlBase = urlBase;
        this.sesion = sesion;
    }

    /** POST /auth/login: guarda el token JWT y el usuario en la sesión. */
    public void iniciarSesion(String usuario, String contrasena) throws ApiException {
        try {
            JSONObject cuerpo = new JSONObject();
            cuerpo.put("nombreUsuario", usuario.trim());
            cuerpo.put("contrasena", contrasena);
            JSONObject respuesta = new JSONObject(enviar("POST", "/auth/login", cuerpo.toString(), false));
            sesion.guardar(respuesta.getString("token"), respuesta.optString("nombreUsuario"), respuesta.optString("rol"));
        } catch (JSONException e) {
            throw new ApiException(0, "Respuesta inesperada del servidor.");
        }
    }

    /** GET /empleados */
    public List<Empleado> listarEmpleados() throws ApiException {
        try {
            return EmpleadoParser.listaDesdeJson(enviar("GET", "/empleados", null, true));
        } catch (JSONException e) {
            throw new ApiException(0, "No se pudo leer la lista de empleados.");
        }
    }

    /** GET /empleados/{documento} */
    public Empleado consultarEmpleado(String documento) throws ApiException {
        try {
            String respuesta = enviar("GET", "/empleados/" + codificar(documento), null, true);
            return EmpleadoParser.empleadoDesdeJson(new JSONObject(respuesta));
        } catch (JSONException e) {
            throw new ApiException(0, "No se pudo leer el empleado.");
        }
    }

    /** POST /empleados */
    public Empleado registrarEmpleado(Empleado empleado) throws ApiException {
        try {
            String respuesta = enviar("POST", "/empleados", EmpleadoParser.empleadoAJson(empleado).toString(), true);
            return EmpleadoParser.empleadoDesdeJson(new JSONObject(respuesta));
        } catch (JSONException e) {
            throw new ApiException(0, "No se pudo preparar el registro.");
        }
    }

    /** PATCH /empleados/{documento}/estado */
    public Empleado cambiarEstado(String documento, String estado, String fechaEfectiva, String motivo) throws ApiException {
        try {
            JSONObject cuerpo = new JSONObject();
            cuerpo.put("estado", estado);
            cuerpo.put("fechaEfectiva", fechaEfectiva);
            cuerpo.put("motivo", motivo);
            String respuesta = enviar("PATCH", "/empleados/" + codificar(documento) + "/estado", cuerpo.toString(), true);
            return EmpleadoParser.empleadoDesdeJson(new JSONObject(respuesta));
        } catch (JSONException e) {
            throw new ApiException(0, "No se pudo cambiar el estado.");
        }
    }

    /** GET /cargos */
    public List<Catalogo> listarCargos() throws ApiException {
        try {
            return EmpleadoParser.cargosDesdeJson(enviar("GET", "/cargos", null, true));
        } catch (JSONException e) {
            throw new ApiException(0, "No se pudieron cargar los cargos.");
        }
    }

    /** GET /tipos-documento */
    public List<Catalogo> listarTiposDocumento() throws ApiException {
        try {
            return EmpleadoParser.catalogoDesdeJson(enviar("GET", "/tipos-documento", null, true),
                    "idTipoDocumento", "nombreTipoDocumento");
        } catch (JSONException e) {
            throw new ApiException(0, "No se pudieron cargar los tipos de documento.");
        }
    }

    /**
     * Envía la petición HTTP y devuelve el cuerpo de la respuesta.
     * Si el código no es 2xx lanza ApiException con el mensaje {"error"} del back-end.
     */
    private String enviar(String metodo, String ruta, String cuerpoJson, boolean conToken) throws ApiException {
        HttpURLConnection conexion = null;
        try {
            conexion = (HttpURLConnection) new URL(urlBase + ruta).openConnection();
            conexion.setConnectTimeout(TIEMPO_ESPERA_MS);
            conexion.setReadTimeout(TIEMPO_ESPERA_MS);
            conexion.setRequestProperty("Accept", "application/json");
            if (conToken && sesion.getToken() != null) {
                conexion.setRequestProperty("Authorization", "Bearer " + sesion.getToken());
            }
            // En Android, HttpURLConnection (basado en OkHttp) acepta GET, POST, PUT, PATCH y DELETE
            conexion.setRequestMethod(metodo);
            if (cuerpoJson != null) {
                conexion.setDoOutput(true);
                conexion.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(cuerpoJson.getBytes(StandardCharsets.UTF_8));
                }
            }
            int codigo = conexion.getResponseCode();
            boolean exito = codigo >= 200 && codigo < 300;
            String respuesta = leer(exito ? conexion.getInputStream() : conexion.getErrorStream());
            if (!exito) {
                throw new ApiException(codigo, EmpleadoParser.mensajeDeError(respuesta, mensajePorCodigo(codigo)));
            }
            return respuesta;
        } catch (IOException e) {
            throw new ApiException(0, "No se pudo conectar con el servidor. Verifica que el back-end esté encendido.");
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    private static String mensajePorCodigo(int codigo) {
        if (codigo == 401 || codigo == 403) {
            return "Tu sesión venció o no es válida. Inicia sesión de nuevo.";
        }
        return "No se pudo completar la solicitud (error " + codigo + ").";
    }

    private static String leer(InputStream entrada) throws IOException {
        if (entrada == null) {
            return "";
        }
        try (InputStream in = entrada; ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] bloque = new byte[4096];
            int leidos;
            while ((leidos = in.read(bloque)) != -1) {
                buffer.write(bloque, 0, leidos);
            }
            return buffer.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static String codificar(String valor) {
        try {
            return URLEncoder.encode(valor, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            return valor;
        }
    }
}
