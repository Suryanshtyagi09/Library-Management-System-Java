package com.library.util;

import com.library.exception.InvalidDataException;

import java.util.regex.Pattern;

/**
 * Provides simple input validation utilities.
 */
public final class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private ValidationUtil() {
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName + " cannot be empty.");
        }
    }

    public static void requirePositive(int number, String fieldName) {
        if (number <= 0) {
            throw new InvalidDataException(fieldName + " must be greater than zero.");
        }
    }

    public static void validateEmail(String email) {
        requireNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidDataException("Email address is invalid.");
        }
    }
}
