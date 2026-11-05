package com.airport.passengers.dto;

import com.airport.passengers.entity.CategorieReclamation;
import com.airport.passengers.entity.Reclamation;
import com.airport.passengers.entity.StatutReclamation;

import java.time.LocalDateTime;

public record ReclamationResponse(
        Long id,
        String reference,
        Long passagerId,
        String passagerNom,
        Long reservationId,
        String reservationReference,
        Long bagageId,
        String bagageEtiquette,
        CategorieReclamation categorie,
        String sujet,
        String description,
        StatutReclamation statut,
        String reponse,
        LocalDateTime dateCreation,
        LocalDateTime dateCloture
) {
    public static ReclamationResponse from(Reclamation r) {
        return new ReclamationResponse(
                r.getId(), r.getReference(),
                r.getPassager().getId(), r.getPassager().getNomComplet(),
                r.getReservation() != null ? r.getReservation().getId() : null,
                r.getReservation() != null ? r.getReservation().getReference() : null,
                r.getBagage() != null ? r.getBagage().getId() : null,
                r.getBagage() != null ? r.getBagage().getNumeroEtiquette() : null,
                r.getCategorie(), r.getSujet(), r.getDescription(), r.getStatut(), r.getReponse(),
                r.getDateCreation(), r.getDateCloture());
    }
}
