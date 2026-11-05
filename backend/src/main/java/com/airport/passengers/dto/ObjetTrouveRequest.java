package com.airport.passengers.dto;

import com.airport.passengers.entity.CategorieObjet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ObjetTrouveRequest(
        @NotBlank(message = "La description est obligatoire")
        @Size(max = 255, message = "255 caractères maximum")
        String description,

        @NotNull(message = "La catégorie est obligatoire")
        CategorieObjet categorie,

        @NotBlank(message = "Le lieu est obligatoire")
        @Size(max = 100, message = "100 caractères maximum")
        String lieuTrouve,

        @NotNull(message = "La date est obligatoire")
        @PastOrPresent(message = "La date ne peut pas être dans le futur")
        LocalDateTime dateTrouve
) {
}
