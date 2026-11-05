package com.airport.passengers.dto;

import java.util.List;

public record ObjetTrouveDetailResponse(
        ObjetTrouveResponse objet,
        List<RestitutionResponse> demandes
) {
}
