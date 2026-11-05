package com.airport.passengers.entity;

/**
 * Statuses of a bag, with the ONLY transitions allowed:
 *
 *   ENREGISTRE -> TRIE -> CHARGE -> DECHARGE -> LIVRE
 *        \          \        \          \
 *         +----------+--------+----------+--> PERDU -> RETROUVE -> LIVRE
 *
 * A bag that is LIVRE (delivered) cannot change any more.
 */
public enum StatutBagage {
    ENREGISTRE,
    TRIE,
    CHARGE,
    DECHARGE,
    LIVRE,
    PERDU,
    RETROUVE;

    public boolean canTransitionTo(StatutBagage target) {
        return switch (this) {
            case ENREGISTRE -> target == TRIE || target == PERDU;
            case TRIE -> target == CHARGE || target == PERDU;
            case CHARGE -> target == DECHARGE || target == PERDU;
            case DECHARGE -> target == LIVRE || target == PERDU;
            case PERDU -> target == RETROUVE;
            case RETROUVE -> target == LIVRE;
            case LIVRE -> false;
        };
    }
}
