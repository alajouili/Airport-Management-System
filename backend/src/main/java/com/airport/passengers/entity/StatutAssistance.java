package com.airport.passengers.entity;

/**
 * Life cycle of an assistance request:
 *   DEMANDEE -> CONFIRMEE -> REALISEE
 *   DEMANDEE or CONFIRMEE -> ANNULEE
 */
public enum StatutAssistance {
    DEMANDEE,
    CONFIRMEE,
    REALISEE,
    ANNULEE;

    public boolean canTransitionTo(StatutAssistance target) {
        return switch (this) {
            case DEMANDEE -> target == CONFIRMEE || target == ANNULEE;
            case CONFIRMEE -> target == REALISEE || target == ANNULEE;
            case REALISEE, ANNULEE -> false;
        };
    }
}
