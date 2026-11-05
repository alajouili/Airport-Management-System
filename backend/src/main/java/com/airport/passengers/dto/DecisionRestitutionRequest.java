package com.airport.passengers.dto;

import jakarta.validation.constraints.Size;

/** acceptee = true gives the object back; a refusal requires a comment (checked by the service). */
public record DecisionRestitutionRequest(
        boolean acceptee,

        @Size(max = 255, message = "255 caractères maximum")
        String commentaire
) {
}
