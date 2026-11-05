package com.airport.passengers.dto;

import com.airport.passengers.entity.TypePointControle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PointControleRequest(
        @NotBlank(message = "Le code est obligatoire")
        @Pattern(regexp = "^[A-Za-z0-9-]{3,10}$", message = "3 à 10 lettres, chiffres ou tirets")
        String code,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 100, message = "100 caractères maximum")
        String nom,

        @NotBlank(message = "Le terminal est obligatoire")
        @Size(max = 20, message = "20 caractères maximum")
        String terminal,

        @NotNull(message = "Le type est obligatoire")
        TypePointControle type,

        boolean actif
) {
}
