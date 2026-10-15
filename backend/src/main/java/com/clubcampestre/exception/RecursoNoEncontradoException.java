package com.clubcampestre.exception;

/** Se lanza cuando el registro buscado no existe (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
