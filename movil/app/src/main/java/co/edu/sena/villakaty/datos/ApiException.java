package co.edu.sena.villakaty.datos;

/**
 * Error devuelto por la API (o de conexión), con un mensaje listo para mostrar al usuario.
 */
public class ApiException extends Exception {

    private final int codigoHttp;

    public ApiException(int codigoHttp, String mensaje) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
    }

    /** Código HTTP de la respuesta (0 si no hubo conexión con el servidor). */
    public int getCodigoHttp() {
        return codigoHttp;
    }

    /** true si la sesión venció o el token no es válido (401 o 403). */
    public boolean esSesionInvalida() {
        return codigoHttp == 401 || codigoHttp == 403;
    }
}
