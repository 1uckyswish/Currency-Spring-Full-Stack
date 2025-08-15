package com.noel.currency_converter.exception;

// Wrong base currency input
public class InvalidBaseCurrencyException extends RuntimeException {
    public InvalidBaseCurrencyException(String message) {
        super(message);
    }
}
