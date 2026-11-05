package com.airport.passengers.entity;

/**
 * A physical handling event (a scan). Most events move the bag to a new
 * status; CONTROLE (security check) is recorded without changing the status.
 */
public enum TypeEvenementBagage {
    SCAN_TRI(StatutBagage.TRIE),
    CHARGEMENT(StatutBagage.CHARGE),
    DECHARGEMENT(StatutBagage.DECHARGE),
    LIVRAISON_TAPIS(StatutBagage.LIVRE),
    DECLARATION_PERTE(StatutBagage.PERDU),
    BAGAGE_RETROUVE(StatutBagage.RETROUVE),
    CONTROLE(null);

    private final StatutBagage statutCible;

    TypeEvenementBagage(StatutBagage statutCible) {
        this.statutCible = statutCible;
    }

    /** The status the bag moves to, or null if this event does not change it. */
    public StatutBagage getStatutCible() {
        return statutCible;
    }
}
