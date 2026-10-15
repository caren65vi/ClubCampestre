package com.clubcampestre.exception;

/** Se lanza cuando se intenta crear un registro que ya existe (HTTP 409). */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String message) {
        super(message);
    }
}
