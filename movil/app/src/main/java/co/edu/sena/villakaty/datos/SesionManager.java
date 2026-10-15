package co.edu.sena.villakaty.datos;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda la sesión del colaborador (token JWT, usuario y rol) en SharedPreferences privadas de la app.
 */
public class SesionManager {

    private static final String ARCHIVO = "sesion_villakaty";
    private static final String CLAVE_TOKEN = "token";
    private static final String CLAVE_USUARIO = "usuario";
    private static final String CLAVE_ROL = "rol";

    private final SharedPreferences preferencias;

    public SesionManager(Context contexto) {
        preferencias = contexto.getApplicationContext().getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    public void guardar(String token, String usuario, String rol) {
        preferencias.edit()
                .putString(CLAVE_TOKEN, token)
                .putString(CLAVE_USUARIO, usuario)
                .putString(CLAVE_ROL, rol)
                .apply();
    }

    public String getToken() {
        return preferencias.getString(CLAVE_TOKEN, null);
    }

    public String getUsuario() {
        return preferencias.getString(CLAVE_USUARIO, "");
    }

    public String getRol() {
        return preferencias.getString(CLAVE_ROL, "");
    }

    public boolean haySesion() {
        return getToken() != null;
    }

    public void cerrar() {
        preferencias.edit().clear().apply();
    }
}
