package com.airport.passengers.entity;

import java.math.BigDecimal;

/** Hold baggage or cabin baggage, each with its maximum weight. */
public enum TypeBagage {
    SOUTE(new BigDecimal("32")),
    CABINE(new BigDecimal("10"));

    private final BigDecimal poidsMaxKg;

    TypeBagage(BigDecimal poidsMaxKg) {
        this.poidsMaxKg = poidsMaxKg;
    }

    public BigDecimal getPoidsMaxKg() {
        return poidsMaxKg;
    }
}
