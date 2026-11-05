package com.airport.passengers.dto;

import java.util.List;

/**
 * Everything the reservation page needs in ONE request:
 * the reservation, its payment attempts, its ticket (null before payment),
 * the bags registered at check-in, the assistance requests and the review (null if none).
 */
public record ReservationDetailResponse(
        ReservationResponse reservation,
        List<PaiementResponse> paiements,
        BilletResponse billet,
        List<BagageResponse> bagages,
        List<AssistanceResponse> assistances,
        AvisResponse avis
) {
}
