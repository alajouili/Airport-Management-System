package com.airport.operations.dto;

import com.airport.operations.entity.Vol;

import java.time.LocalDateTime;

/**
 * OWNED BY STUDENT A - temporary minimal version.
 *
 * DTO = Data Transfer Object: the exact JSON shape sent to Angular.
 * We never send the JPA entity directly, so that:
 *  - the API does not change when the table changes,
 *  - lazy relations do not cause errors during JSON conversion.
 */
public record VolResponse(
        Long id,
        String numeroVol,
        String compagnieCode,
        String compagnieNom,
        String aeroportDepart,
        String aeroportArrivee,
        LocalDateTime dateHeureDepart,
        LocalDateTime dateHeureArrivee,
        Integer capacite,
        String statut
) {
    /** Converts an entity into its DTO. Must be called inside a transaction (lazy airline). */
    public static VolResponse from(Vol vol) {
        return new VolResponse(
                vol.getId(),
                vol.getNumeroVol(),
                vol.getCompagnie().getCodeIata(),
                vol.getCompagnie().getNom(),
                vol.getAeroportDepart(),
                vol.getAeroportArrivee(),
                vol.getDateHeureDepart(),
                vol.getDateHeureArrivee(),
                vol.getCapacite(),
                vol.getStatut().name());
    }
}
