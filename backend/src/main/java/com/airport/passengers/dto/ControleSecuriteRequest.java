package com.airport.passengers.dto;

import com.airport.passengers.entity.ResultatControle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ControleSecuriteRequest(
        @NotNull(message = "Le point de contrôle est obligatoire")
        Long pointControleId,

        @NotNull(message = "Le résultat est obligatoire")
        ResultatControle resultat,

        @Size(max = 255, message = "255 caractères maximum")
        String commentaire
) {
}
