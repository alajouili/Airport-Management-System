package com.airport.passengers.dto;

import com.airport.passengers.entity.Bagage;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutBagage;
import com.airport.passengers.entity.TypeBagage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BagageResponse(
        Long id,
        String numeroEtiquette,
        TypeBagage type,
        BigDecimal poidsKg,
        StatutBagage statut,
        LocalDateTime dateCreation,
        Long reservationId,
        String reservationReference,
        Long passagerId,
        String passagerNom,
        String numeroVol,
        String aeroportArrivee
) {
    /** Must be called inside a transaction (lazy relations). */
    public static BagageResponse from(Bagage b) {
        Reservation r = b.getEnregistrement().getBillet().getReservation();
        return new BagageResponse(
                b.getId(), b.getNumeroEtiquette(), b.getType(), b.getPoidsKg(), b.getStatut(),
                b.getDateCreation(), r.getId(), r.getReference(), r.getPassager().getId(), r.getPassager().getNomComplet(),
                r.getVol().getNumeroVol(), r.getVol().getAeroportArrivee());
    }
}
