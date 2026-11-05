package com.airport.passengers.entity;

/**
 * Life cycle of a reservation:
 *   EN_ATTENTE_PAIEMENT --(accepted payment)--> CONFIRMEE
 *   EN_ATTENTE_PAIEMENT or CONFIRMEE --(cancel)--> ANNULEE
 */
public enum StatutReservation {
    EN_ATTENTE_PAIEMENT,
    CONFIRMEE,
    ANNULEE
}
