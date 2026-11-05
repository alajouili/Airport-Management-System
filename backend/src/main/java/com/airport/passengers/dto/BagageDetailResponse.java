package com.airport.passengers.dto;

import java.util.List;

/** A bag with its full tracking: status history and handling events. */
public record BagageDetailResponse(
        BagageResponse bagage,
        List<HistoriqueBagageResponse> historique,
        List<EvenementBagageResponse> evenements
) {
}
