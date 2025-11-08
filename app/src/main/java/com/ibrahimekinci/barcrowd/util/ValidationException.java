package com.ibrahimekinci.barcrowd.util;

/**
 * Represents an error in user input validation (e.g., bad format, length, or uniqueness).
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}