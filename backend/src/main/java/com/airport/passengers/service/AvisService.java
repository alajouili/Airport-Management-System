package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.passengers.dto.AvisRequest;
import com.airport.passengers.dto.AvisResponse;
import com.airport.passengers.entity.AvisPassager;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutBillet;
import com.airport.passengers.repository.AvisPassagerRepository;
import com.airport.passengers.repository.BilletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Passenger reviews. Rules: only after boarding (ticket UTILISE), one review per trip.
 */
@Service
@Transactional
public class AvisService {

    private final AvisPassagerRepository avisRepository;
    private final BilletRepository billetRepository;
    private final ReservationService reservationService;

    public AvisService(AvisPassagerRepository avisRepository,
                       BilletRepository billetRepository,
                       ReservationService reservationService) {
        this.avisRepository = avisRepository;
        this.billetRepository = billetRepository;
        this.reservationService = reservationService;
    }

    @Transactional(readOnly = true)
    public List<AvisResponse> findAll(Integer note) {
        return avisRepository.findWithFilters(note).stream().map(AvisResponse::from).toList();
    }

    public AvisResponse create(AvisRequest request) {
        Reservation reservation = reservationService.findEntity(request.reservationId());

        boolean aEmbarque = billetRepository.findByReservationId(reservation.getId())
                .map(b -> b.getStatut() == StatutBillet.UTILISE)
                .orElse(false);
        if (!aEmbarque) {
            throw new BusinessRuleException("Un avis peut être donné uniquement après l'embarquement");
        }
        if (avisRepository.existsByReservationId(reservation.getId())) {
            throw new BusinessRuleException("Un avis a déjà été donné pour ce voyage");
        }

        AvisPassager avis = new AvisPassager();
        avis.setReservation(reservation);
        avis.setNote(request.note());
        avis.setCommentaire(request.commentaire() == null || request.commentaire().isBlank()
                ? null : request.commentaire().trim());
        return AvisResponse.from(avisRepository.save(avis));
    }
}
