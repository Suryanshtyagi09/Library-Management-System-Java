package com.library.exception;

/**
 * Thrown when the requested book is not available for issue.
 */
public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message);
    }
}
