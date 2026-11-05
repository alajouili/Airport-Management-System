package com.airport.passengers.dto;

import com.airport.passengers.entity.TypeAssistance;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssistanceRequest(
        @NotNull(message = "La réservation est obligatoire")
        Long reservationId,

        @NotNull(message = "Le type d'assistance est obligatoire")
        TypeAssistance typeAssistance,

        @Size(max = 500, message = "500 caractères maximum")
        String description
) {
}
