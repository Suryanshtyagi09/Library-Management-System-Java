package com.library.exception;

/**
 * Thrown when the requested student cannot be found.
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
