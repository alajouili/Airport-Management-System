package com.airport.passengers.controller;

import com.airport.passengers.dto.PaiementRequest;
import com.airport.passengers.dto.PaiementResponse;
import com.airport.passengers.dto.ReservationDetailResponse;
import com.airport.passengers.dto.ReservationRequest;
import com.airport.passengers.dto.ReservationResponse;
import com.airport.passengers.entity.StatutReservation;
import com.airport.passengers.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET  /api/reservations?statut=&volId=&passagerId=   list with filters
 *   GET  /api/reservations/{id}                         detail (payments, ticket, bags)
 *   POST /api/reservations                              book -> 201
 *   POST /api/reservations/{id}/paiements               simulated payment -> 201
 *   POST /api/reservations/{id}/annulation              cancel
 *
 * Actions like "cancel" are POST on a sub-resource rather than DELETE,
 * because the reservation is kept (status ANNULEE), not deleted.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> findAll(@RequestParam(required = false) StatutReservation statut,
                                             @RequestParam(required = false) Long volId,
                                             @RequestParam(required = false) Long passagerId) {
        return reservationService.findAll(statut, volId, passagerId);
    }

    @GetMapping("/{id}")
    public ReservationDetailResponse findDetail(@PathVariable Long id) {
        return reservationService.findDetail(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationRequest request) {
        return reservationService.create(request);
    }

    @PostMapping("/{id}/paiements")
    @ResponseStatus(HttpStatus.CREATED)
    public PaiementResponse payer(@PathVariable Long id, @Valid @RequestBody PaiementRequest request) {
        return reservationService.payer(id, request);
    }

    @PostMapping("/{id}/annulation")
    public ReservationResponse annuler(@PathVariable Long id) {
        return reservationService.annuler(id);
    }
}
