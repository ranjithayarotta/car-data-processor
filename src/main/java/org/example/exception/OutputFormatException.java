package org.example.exception;

public class OutputFormatException extends RuntimeException {
    public OutputFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}