package com.airport.passengers.dto;

import com.airport.passengers.entity.EvenementEmbarquement;
import com.airport.passengers.entity.TypeEvenementEmbarquement;

import java.time.LocalDateTime;

public record EvenementEmbarquementResponse(
        Long id,
        TypeEvenementEmbarquement typeEvenement,
        String porte,
        String commentaire,
        LocalDateTime dateEvenement
) {
    public static EvenementEmbarquementResponse from(EvenementEmbarquement e) {
        return new EvenementEmbarquementResponse(e.getId(), e.getTypeEvenement(), e.getPorte(),
                e.getCommentaire(), e.getDateEvenement());
    }
}
