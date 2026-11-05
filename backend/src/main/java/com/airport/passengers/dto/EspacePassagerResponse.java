package com.airport.passengers.dto;

import java.util.List;

/** The passenger's view of one trip: the full detail plus their complaints about it. */
public record EspacePassagerResponse(
        ReservationDetailResponse voyage,
        List<ReclamationResponse> reclamations
) {
}
