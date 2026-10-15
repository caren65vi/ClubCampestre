package com.clubcampestre.exception;

/** Se lanza cuando los datos enviados no cumplen las reglas del negocio (HTTP 400). */
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String message) {
        super(message);
    }
}
