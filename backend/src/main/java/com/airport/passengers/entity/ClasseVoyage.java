package com.airport.passengers.entity;

import java.math.BigDecimal;

/**
 * Travel class, with a simplified fixed price in Tunisian dinars.
 * (A real system would use a fare per flight and per date; the specification
 * does not ask for it, so a fixed price keeps the booking rules clear.)
 */
public enum ClasseVoyage {
    ECONOMIQUE(new BigDecimal("350.000")),
    AFFAIRES(new BigDecimal("950.000"));

    private final BigDecimal prixBase;

    ClasseVoyage(BigDecimal prixBase) {
        this.prixBase = prixBase;
    }

    public BigDecimal getPrixBase() {
        return prixBase;
    }
}
