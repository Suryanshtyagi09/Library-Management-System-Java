package com.library.exception;

/**
 * Thrown when validation fails for user input or business data.
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
