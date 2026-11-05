package com.airport.passengers.dto;

import com.airport.passengers.entity.EvenementTraitementBagage;
import com.airport.passengers.entity.TypeEvenementBagage;

import java.time.LocalDateTime;

public record EvenementBagageResponse(
        Long id,
        TypeEvenementBagage typeEvenement,
        String lieu,
        String commentaire,
        LocalDateTime dateEvenement
) {
    public static EvenementBagageResponse from(EvenementTraitementBagage e) {
        return new EvenementBagageResponse(e.getId(), e.getTypeEvenement(), e.getLieu(),
                e.getCommentaire(), e.getDateEvenement());
    }
}
