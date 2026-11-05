package com.airport.common.exception;

/**
 * Thrown when a record does not exist, for example GET /api/vols/999.
 * GlobalExceptionHandler turns it into an HTTP 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " introuvable (id = " + id + ")");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
