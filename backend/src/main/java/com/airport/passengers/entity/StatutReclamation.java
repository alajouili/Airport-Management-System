package com.airport.passengers.entity;

/**
 * Life cycle of a complaint:
 *   OUVERTE -> EN_COURS -> RESOLUE or REJETEE
 *   OUVERTE -> REJETEE (obviously unfounded complaint)
 * RESOLUE and REJETEE are final and require an answer to the passenger.
 */
public enum StatutReclamation {
    OUVERTE,
    EN_COURS,
    RESOLUE,
    REJETEE;

    public boolean canTransitionTo(StatutReclamation target) {
        return switch (this) {
            case OUVERTE -> target == EN_COURS || target == REJETEE;
            case EN_COURS -> target == RESOLUE || target == REJETEE;
            case RESOLUE, REJETEE -> false;
        };
    }

    public boolean isFinal() {
        return this == RESOLUE || this == REJETEE;
    }
}
