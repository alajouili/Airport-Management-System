package com.airport.passengers.dto;

import com.airport.passengers.entity.TypeEvenementEmbarquement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmbarquementRequest(
        @NotNull(message = "Le type d'événement est obligatoire")
        TypeEvenementEmbarquement typeEvenement,

        @NotBlank(message = "La porte est obligatoire")
        @Size(max = 10, message = "10 caractères maximum")
        String porte,

        @Size(max = 255, message = "255 caractères maximum")
        String commentaire
) {
}
