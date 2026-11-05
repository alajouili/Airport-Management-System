package com.airport.passengers.dto;

import com.airport.passengers.entity.TypeEvenementBagage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EvenementBagageRequest(
        @NotNull(message = "Le type d'événement est obligatoire")
        TypeEvenementBagage typeEvenement,

        @NotBlank(message = "Le lieu est obligatoire")
        @Size(max = 100, message = "100 caractères maximum")
        String lieu,

        @Size(max = 255, message = "255 caractères maximum")
        String commentaire
) {
}
