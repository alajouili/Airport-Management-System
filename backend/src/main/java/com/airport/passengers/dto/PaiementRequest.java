package com.airport.passengers.dto;

import com.airport.passengers.entity.ModePaiement;
import jakarta.validation.constraints.NotNull;

/**
 * Simulated payment request.
 * There is NO card number: the amount is taken from the reservation by the server
 * (never trusted from the browser), and "simulerRefus" lets the demo show
 * what happens when a bank refuses a payment.
 */
public record PaiementRequest(
        @NotNull(message = "Le mode de paiement est obligatoire")
        ModePaiement mode,

        boolean simulerRefus
) {
}
