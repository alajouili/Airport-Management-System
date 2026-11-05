package com.airport.passengers.dto;

import com.airport.passengers.entity.DemandeAssistancePassager;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutAssistance;
import com.airport.passengers.entity.TypeAssistance;

import java.time.LocalDateTime;

public record AssistanceResponse(
        Long id,
        Long reservationId,
        String reservationReference,
        String passagerNom,
        String numeroVol,
        LocalDateTime dateHeureDepart,
        TypeAssistance typeAssistance,
        String description,
        StatutAssistance statut,
        LocalDateTime dateDemande,
        LocalDateTime dateMiseAJour
) {
    public static AssistanceResponse from(DemandeAssistancePassager d) {
        Reservation r = d.getReservation();
        return new AssistanceResponse(d.getId(), r.getId(), r.getReference(), r.getPassager().getNomComplet(),
                r.getVol().getNumeroVol(), r.getVol().getDateHeureDepart(), d.getTypeAssistance(),
                d.getDescription(), d.getStatut(), d.getDateDemande(), d.getDateMiseAJour());
    }
}
