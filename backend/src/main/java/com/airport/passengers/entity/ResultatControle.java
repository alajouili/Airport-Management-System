package com.airport.passengers.entity;

/** Result of a security check. Only CONFORME allows boarding. */
public enum ResultatControle {
    CONFORME,
    FOUILLE_COMPLEMENTAIRE,
    REFUSE
}
