package com.airport.common.exception;

/**
 * Thrown when a request is valid in format but breaks a business rule,
 * for example "this parking space is already booked for that period".
 * GlobalExceptionHandler turns it into an HTTP 409 (Conflict) response.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
