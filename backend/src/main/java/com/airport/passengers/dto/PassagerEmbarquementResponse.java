package com.airport.passengers.dto;

import com.airport.passengers.entity.ClasseVoyage;
import com.airport.passengers.entity.StatutBillet;

/**
 * One line of the boarding gate screen: a checked-in passenger with their
 * latest security check and latest gate event (both may be null).
 * "peutEmbarquer" is computed by the server, so the screen and the rule always agree.
 */
public record PassagerEmbarquementResponse(
        Long billetId,
        String numeroBillet,
        StatutBillet statutBillet,
        Long reservationId,
        String reservationReference,
        String passagerNom,
        ClasseVoyage classe,
        String siegeNumero,
        ControleSecuriteResponse dernierControle,
        EvenementEmbarquementResponse dernierEvenement,
        boolean peutEmbarquer
) {
}
