package com.airport.passengers.dto;

import com.airport.passengers.entity.CategorieObjet;
import com.airport.passengers.entity.ObjetTrouve;
import com.airport.passengers.entity.StatutObjet;

import java.time.LocalDateTime;

/** "eliminable" = the 90-day rule allows elimination now (computed by the service). */
public record ObjetTrouveResponse(
        Long id,
        String reference,
        String description,
        CategorieObjet categorie,
        String lieuTrouve,
        LocalDateTime dateTrouve,
        StatutObjet statut,
        LocalDateTime dateEnregistrement,
        LocalDateTime dateSortie,
        boolean eliminable
) {
    public static ObjetTrouveResponse from(ObjetTrouve o, boolean eliminable) {
        return new ObjetTrouveResponse(o.getId(), o.getReference(), o.getDescription(), o.getCategorie(),
                o.getLieuTrouve(), o.getDateTrouve(), o.getStatut(), o.getDateEnregistrement(),
                o.getDateSortie(), eliminable);
    }
}
