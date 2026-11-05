package com.airport.passengers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvisRequest(
        @NotNull(message = "La réservation est obligatoire")
        Long reservationId,

        @NotNull(message = "La note est obligatoire")
        @Min(value = 1, message = "Note entre 1 et 5")
        @Max(value = 5, message = "Note entre 1 et 5")
        Integer note,

        @Size(max = 1000, message = "1000 caractères maximum")
        String commentaire
) {
}
