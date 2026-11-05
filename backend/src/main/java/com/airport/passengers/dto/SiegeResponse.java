package com.airport.passengers.dto;

import com.airport.passengers.entity.ClasseVoyage;

/** One seat of the seat map; "occupe" = already assigned to a ticket. */
public record SiegeResponse(
        Long id,
        String numero,
        ClasseVoyage classe,
        boolean occupe
) {
}
