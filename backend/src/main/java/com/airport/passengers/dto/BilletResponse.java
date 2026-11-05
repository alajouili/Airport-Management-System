package com.airport.passengers.dto;

import com.airport.passengers.entity.AffectationSiegeBillet;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.CanalEnregistrement;
import com.airport.passengers.entity.Enregistrement;
import com.airport.passengers.entity.StatutBillet;

import java.time.LocalDateTime;

/** A ticket with its seat (if chosen) and its check-in (if done). */
public record BilletResponse(
        Long id,
        String numeroBillet,
        Long reservationId,
        StatutBillet statut,
        LocalDateTime dateEmission,
        Long siegeId,
        String siegeNumero,
        Long enregistrementId,
        CanalEnregistrement canalEnregistrement,
        LocalDateTime dateEnregistrement
) {
    /** affectation and enregistrement may be null (no seat yet, not checked in yet). */
    public static BilletResponse from(Billet billet, AffectationSiegeBillet affectation, Enregistrement enregistrement) {
        return new BilletResponse(
                billet.getId(),
                billet.getNumeroBillet(),
                billet.getReservation().getId(),
                billet.getStatut(),
                billet.getDateEmission(),
                affectation != null ? affectation.getSiege().getId() : null,
                affectation != null ? affectation.getSiege().getNumero() : null,
                enregistrement != null ? enregistrement.getId() : null,
                enregistrement != null ? enregistrement.getCanal() : null,
                enregistrement != null ? enregistrement.getDateEnregistrement() : null);
    }
}
