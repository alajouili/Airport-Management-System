package com.airport.passengers.service;

import com.airport.common.exception.ResourceNotFoundException;
import com.airport.passengers.dto.EspacePassagerRequest;
import com.airport.passengers.dto.EspacePassagerResponse;
import com.airport.passengers.dto.ReclamationResponse;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.repository.ReclamationRepository;
import com.airport.passengers.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Passenger personal space, "manage my booking" style: the passenger proves
 * who they are with the reservation reference AND their email.
 *
 * The same error message is returned whether the reference or the email is wrong,
 * so nobody can use this screen to discover which references exist.
 * (With Student A's login, the space can later use the logged-in user instead.)
 */
@Service
@Transactional(readOnly = true)
public class EspacePassagerService {

    private final ReservationRepository reservationRepository;
    private final ReclamationRepository reclamationRepository;
    private final ReservationService reservationService;

    public EspacePassagerService(ReservationRepository reservationRepository,
                                 ReclamationRepository reclamationRepository,
                                 ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reclamationRepository = reclamationRepository;
        this.reservationService = reservationService;
    }

    public EspacePassagerResponse rechercher(EspacePassagerRequest request) {
        Reservation reservation = reservationRepository
                .findByReferenceAndEmail(request.reference().trim(), request.email().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucune réservation ne correspond à cette référence et à cet email"));

        List<ReclamationResponse> reclamations = reclamationRepository
                .findByReservationIdOrderByDateCreationDesc(reservation.getId())
                .stream().map(ReclamationResponse::from).toList();
        return new EspacePassagerResponse(reservationService.findDetail(reservation.getId()), reclamations);
    }
}
