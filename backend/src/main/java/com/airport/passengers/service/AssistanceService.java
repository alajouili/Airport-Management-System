package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.passengers.dto.AssistanceRequest;
import com.airport.passengers.dto.AssistanceResponse;
import com.airport.passengers.entity.DemandeAssistancePassager;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutAssistance;
import com.airport.passengers.entity.StatutReservation;
import com.airport.passengers.repository.DemandeAssistancePassagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Assistance requests (wheelchair, reduced mobility, unaccompanied minor...).
 *
 * Rules:
 *  - the reservation must not be cancelled and the flight must not have left;
 *  - no duplicate active request of the same type for the same trip;
 *  - status changes follow StatutAssistance.canTransitionTo();
 *  - cancelling a reservation cancels its active requests (ReservationService.annuler).
 */
@Service
@Transactional
public class AssistanceService {

    private final DemandeAssistancePassagerRepository assistanceRepository;
    private final ReservationService reservationService;
    private final Clock clock;

    public AssistanceService(DemandeAssistancePassagerRepository assistanceRepository,
                             ReservationService reservationService,
                             Clock clock) {
        this.assistanceRepository = assistanceRepository;
        this.reservationService = reservationService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<AssistanceResponse> findAll(StatutAssistance statut, Long volId) {
        return assistanceRepository.findWithFilters(statut, volId).stream().map(AssistanceResponse::from).toList();
    }

    public AssistanceResponse create(AssistanceRequest request) {
        Reservation reservation = reservationService.findEntity(request.reservationId());

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessRuleException("La réservation est annulée");
        }
        if (!reservation.getVol().getDateHeureDepart().isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("Le vol est déjà parti");
        }
        if (assistanceRepository.existsByReservationIdAndTypeAssistanceAndStatutNot(
                reservation.getId(), request.typeAssistance(), StatutAssistance.ANNULEE)) {
            throw new BusinessRuleException("Une demande de ce type existe déjà pour ce voyage");
        }

        DemandeAssistancePassager demande = new DemandeAssistancePassager();
        demande.setReservation(reservation);
        demande.setTypeAssistance(request.typeAssistance());
        demande.setDescription(request.description() == null || request.description().isBlank()
                ? null : request.description().trim());
        demande.setStatut(StatutAssistance.DEMANDEE);
        return AssistanceResponse.from(assistanceRepository.save(demande));
    }

    public AssistanceResponse changerStatut(Long id, StatutAssistance nouveauStatut) {
        DemandeAssistancePassager demande = assistanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'assistance", id));
        if (!demande.getStatut().canTransitionTo(nouveauStatut)) {
            throw new BusinessRuleException("Transition impossible : " + demande.getStatut() + " → " + nouveauStatut);
        }
        demande.setStatut(nouveauStatut);
        demande.setDateMiseAJour(LocalDateTime.now(clock));
        return AssistanceResponse.from(demande);
    }
}
