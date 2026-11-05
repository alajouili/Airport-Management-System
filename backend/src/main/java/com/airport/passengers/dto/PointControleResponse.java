package com.airport.passengers.dto;

import com.airport.passengers.entity.PointControleSecurite;
import com.airport.passengers.entity.TypePointControle;

public record PointControleResponse(
        Long id,
        String code,
        String nom,
        String terminal,
        TypePointControle type,
        boolean actif
) {
    public static PointControleResponse from(PointControleSecurite p) {
        return new PointControleResponse(p.getId(), p.getCode(), p.getNom(), p.getTerminal(), p.getType(),
                Boolean.TRUE.equals(p.getActif()));
    }
}
