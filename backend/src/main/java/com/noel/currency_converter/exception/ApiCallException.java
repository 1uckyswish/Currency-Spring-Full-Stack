package com.noel.currency_converter.exception;

public class ApiCallException extends RuntimeException {
    public ApiCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiCallException(String message) {
        super(message);
    }
}