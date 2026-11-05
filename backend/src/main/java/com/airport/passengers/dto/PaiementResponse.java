package com.airport.passengers.dto;

import com.airport.passengers.entity.ModePaiement;
import com.airport.passengers.entity.Paiement;
import com.airport.passengers.entity.StatutPaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponse(
        Long id,
        BigDecimal montant,
        ModePaiement mode,
        StatutPaiement statut,
        String referenceTransaction,
        LocalDateTime datePaiement
) {
    public static PaiementResponse from(Paiement p) {
        return new PaiementResponse(p.getId(), p.getMontant(), p.getMode(), p.getStatut(),
                p.getReferenceTransaction(), p.getDatePaiement());
    }
}
