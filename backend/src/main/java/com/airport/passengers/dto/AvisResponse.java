package com.airport.passengers.dto;

import com.airport.passengers.entity.AvisPassager;
import com.airport.passengers.entity.Reservation;

import java.time.LocalDateTime;

public record AvisResponse(
        Long id,
        Long reservationId,
        String reservationReference,
        String passagerNom,
        String numeroVol,
        String compagnieNom,
        String aeroportArrivee,
        Integer note,
        String commentaire,
        LocalDateTime dateAvis
) {
    public static AvisResponse from(AvisPassager a) {
        Reservation r = a.getReservation();
        return new AvisResponse(a.getId(), r.getId(), r.getReference(), r.getPassager().getNomComplet(),
                r.getVol().getNumeroVol(), r.getVol().getCompagnie().getNom(), r.getVol().getAeroportArrivee(),
                a.getNote(), a.getCommentaire(), a.getDateAvis());
    }
}
