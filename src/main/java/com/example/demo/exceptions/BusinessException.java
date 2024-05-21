package com.example.demo.exceptions;

public final class BusinessException extends RuntimeException {

    public BusinessException(final String message) {
        super(message);
    }
}
