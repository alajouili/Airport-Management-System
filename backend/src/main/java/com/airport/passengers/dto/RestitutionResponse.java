package com.airport.passengers.dto;

import com.airport.passengers.entity.DemandeRestitutionObjet;
import com.airport.passengers.entity.StatutRestitution;

import java.time.LocalDateTime;

public record RestitutionResponse(
        Long id,
        Long passagerId,
        String nomDemandeur,
        String contact,
        String descriptionPreuve,
        StatutRestitution statut,
        String commentaireDecision,
        LocalDateTime dateDemande,
        LocalDateTime dateDecision
) {
    public static RestitutionResponse from(DemandeRestitutionObjet d) {
        return new RestitutionResponse(d.getId(), d.getPassager() != null ? d.getPassager().getId() : null,
                d.getNomDemandeur(), d.getContact(), d.getDescriptionPreuve(), d.getStatut(),
                d.getCommentaireDecision(), d.getDateDemande(), d.getDateDecision());
    }
}
