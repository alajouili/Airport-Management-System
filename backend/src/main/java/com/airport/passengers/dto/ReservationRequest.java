package com.airport.passengers.dto;

import com.airport.passengers.entity.ClasseVoyage;
import jakarta.validation.constraints.NotNull;

/** JSON received to book a flight: who, which flight, which class. */
public record ReservationRequest(
        @NotNull(message = "Le passager est obligatoire")
        Long passagerId,

        @NotNull(message = "Le vol est obligatoire")
        Long volId,

        @NotNull(message = "La classe est obligatoire")
        ClasseVoyage classe
) {
}
