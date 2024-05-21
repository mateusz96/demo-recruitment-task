package com.example.demo.exceptions;

public class TechnicalException extends RuntimeException {

    public TechnicalException() {
        super();
    }

    public TechnicalException(final String message) {
        super(message);
    }
}
