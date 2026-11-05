package com.airport.passengers.entity;

public enum StatutBillet {
    /** Issued and valid. */
    EMIS,
    /** The passenger boarded (set by the boarding module, Part 2b). */
    UTILISE,
    /** The reservation was cancelled. */
    ANNULE
}
