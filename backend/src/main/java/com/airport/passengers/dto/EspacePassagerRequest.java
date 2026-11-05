package com.airport.passengers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Sent in the BODY of a POST (not in the URL), so the email does not appear
 * in browser history or server access logs.
 */
public record EspacePassagerRequest(
        @NotBlank(message = "La référence est obligatoire")
        String reference,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email
) {
}
