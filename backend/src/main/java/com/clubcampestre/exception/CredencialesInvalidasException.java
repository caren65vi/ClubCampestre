package com.clubcampestre.exception;

/** Se lanza cuando el usuario o la contraseña no son correctos (HTTP 401). */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
