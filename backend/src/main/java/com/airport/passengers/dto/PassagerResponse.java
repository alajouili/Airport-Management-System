package com.airport.passengers.dto;

import com.airport.passengers.entity.Passager;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PassagerResponse(
        Long id,
        String nom,
        String prenom,
        String nomComplet,
        String email,
        String telephone,
        LocalDate dateNaissance,
        String nationalite,
        String numeroPasseport,
        LocalDateTime dateCreation
) {
    public static PassagerResponse from(Passager p) {
        return new PassagerResponse(
                p.getId(), p.getNom(), p.getPrenom(), p.getNomComplet(), p.getEmail(),
                p.getTelephone(), p.getDateNaissance(), p.getNationalite(),
                p.getNumeroPasseport(), p.getDateCreation());
    }
}
