package com.library.exception;

/**
 * Thrown when the requested book cannot be found.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
