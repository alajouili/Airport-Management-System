package com.airport.passengers.dto;

import com.airport.passengers.entity.CanalEnregistrement;
import jakarta.validation.constraints.NotNull;

public record EnregistrementRequest(
        @NotNull(message = "Le canal d'enregistrement est obligatoire")
        CanalEnregistrement canal
) {
}
