package com.airport.passengers.dto;

import com.airport.passengers.entity.CategorieReclamation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** reservationId and bagageId are optional; a BAGAGE complaint needs bagageId. */
public record ReclamationRequest(
        @NotNull(message = "Le passager est obligatoire")
        Long passagerId,

        Long reservationId,

        Long bagageId,

        @NotNull(message = "La catégorie est obligatoire")
        CategorieReclamation categorie,

        @NotBlank(message = "Le sujet est obligatoire")
        @Size(max = 150, message = "150 caractères maximum")
        String sujet,

        @NotBlank(message = "La description est obligatoire")
        @Size(max = 2000, message = "2000 caractères maximum")
        String description
) {
}
