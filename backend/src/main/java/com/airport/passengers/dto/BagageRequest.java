package com.airport.passengers.dto;

import com.airport.passengers.entity.TypeBagage;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BagageRequest(
        @NotNull(message = "Le type de bagage est obligatoire")
        TypeBagage type,

        @NotNull(message = "Le poids est obligatoire")
        @DecimalMin(value = "0.1", message = "Le poids doit être positif")
        @DecimalMax(value = "32", message = "32 kg maximum par bagage")
        @Digits(integer = 3, fraction = 2, message = "2 décimales maximum")
        BigDecimal poidsKg
) {
}
