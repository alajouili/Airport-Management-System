package com.airport.passengers.dto;

import com.airport.passengers.entity.HistoriqueBagage;
import com.airport.passengers.entity.StatutBagage;

import java.time.LocalDateTime;

public record HistoriqueBagageResponse(
        Long id,
        StatutBagage ancienStatut,
        StatutBagage nouveauStatut,
        String commentaire,
        LocalDateTime dateChangement
) {
    public static HistoriqueBagageResponse from(HistoriqueBagage h) {
        return new HistoriqueBagageResponse(h.getId(), h.getAncienStatut(), h.getNouveauStatut(),
                h.getCommentaire(), h.getDateChangement());
    }
}
