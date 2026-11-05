package com.airport.passengers.dto;

import jakarta.validation.constraints.NotNull;

public record AffectationSiegeRequest(
        @NotNull(message = "Le siège est obligatoire")
        Long siegeId
) {
}
