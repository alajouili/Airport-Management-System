package com.airport.passengers.dto;

import com.airport.passengers.entity.ControleSecuritePassager;
import com.airport.passengers.entity.ResultatControle;

import java.time.LocalDateTime;

public record ControleSecuriteResponse(
        Long id,
        String pointControleCode,
        String pointControleNom,
        ResultatControle resultat,
        String commentaire,
        LocalDateTime dateControle
) {
    public static ControleSecuriteResponse from(ControleSecuritePassager c) {
        return new ControleSecuriteResponse(c.getId(), c.getPointControle().getCode(), c.getPointControle().getNom(),
                c.getResultat(), c.getCommentaire(), c.getDateControle());
    }
}
