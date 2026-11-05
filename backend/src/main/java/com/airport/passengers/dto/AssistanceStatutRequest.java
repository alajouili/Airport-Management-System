package com.airport.passengers.dto;

import com.airport.passengers.entity.StatutAssistance;
import jakarta.validation.constraints.NotNull;

public record AssistanceStatutRequest(
        @NotNull(message = "Le statut est obligatoire")
        StatutAssistance statut
) {
}
