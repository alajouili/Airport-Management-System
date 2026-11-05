package com.airport.passengers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** passagerId is optional: the claimant may not be a registered passenger. */
public record RestitutionRequest(
        Long passagerId,

        @NotBlank(message = "Le nom du demandeur est obligatoire")
        @Size(max = 160, message = "160 caractères maximum")
        String nomDemandeur,

        @NotBlank(message = "Un contact (email ou téléphone) est obligatoire")
        @Size(max = 150, message = "150 caractères maximum")
        String contact,

        @NotBlank(message = "Décrivez l'objet pour prouver qu'il vous appartient")
        @Size(max = 500, message = "500 caractères maximum")
        String descriptionPreuve
) {
}
