package com.airport.passengers.dto;

import com.airport.operations.entity.Vol;
import com.airport.passengers.entity.ClasseVoyage;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One reservation, with the passenger and flight information the screens display. */
public record ReservationResponse(
        Long id,
        String reference,
        Long passagerId,
        String passagerNom,
        Long volId,
        String numeroVol,
        String compagnieNom,
        String aeroportDepart,
        String aeroportArrivee,
        LocalDateTime dateHeureDepart,
        ClasseVoyage classe,
        BigDecimal montant,
        StatutReservation statut,
        LocalDateTime dateReservation,
        LocalDateTime dateAnnulation
) {
    /** Must be called inside a transaction: passenger and flight are lazy relations. */
    public static ReservationResponse from(Reservation r) {
        Vol vol = r.getVol();
        return new ReservationResponse(
                r.getId(), r.getReference(),
                r.getPassager().getId(), r.getPassager().getNomComplet(),
                vol.getId(), vol.getNumeroVol(), vol.getCompagnie().getNom(),
                vol.getAeroportDepart(), vol.getAeroportArrivee(), vol.getDateHeureDepart(),
                r.getClasse(), r.getMontant(), r.getStatut(),
                r.getDateReservation(), r.getDateAnnulation());
    }
}
