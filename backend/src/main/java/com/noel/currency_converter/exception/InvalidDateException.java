package com.noel.currency_converter.exception;

// Wrong date input
public class InvalidDateException extends RuntimeException {
    public InvalidDateException(String message) {
        super(message);
    }
}