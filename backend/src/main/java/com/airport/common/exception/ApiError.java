package com.airport.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The JSON body returned for every error, so Angular always receives the same shape:
 *
 * {
 *   "status": 409,
 *   "error": "Conflict",
 *   "message": "La place est déjà réservée sur cette période",
 *   "path": "/api/reservations-parking",
 *   "timestamp": "2026-10-29T10:15:00",
 *   "fieldErrors": { "email": "must be a well-formed email address" }
 * }
 *
 * A Java "record" is a short way to write an immutable class with
 * a constructor, getters, equals() and toString().
 */
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
}
