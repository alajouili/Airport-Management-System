package com.airport.passengers.dto;

import com.airport.passengers.entity.StatutReclamation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** "reponse" is required by the service when the new status is RESOLUE or REJETEE. */
public record ReclamationStatutRequest(
        @NotNull(message = "Le statut est obligatoire")
        StatutReclamation statut,

        @Size(max = 2000, message = "2000 caractères maximum")
        String reponse
) {
}
