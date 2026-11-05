package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.common.util.CodeGenerator;
import com.airport.passengers.dto.ReclamationRequest;
import com.airport.passengers.dto.ReclamationResponse;
import com.airport.passengers.dto.ReclamationStatutRequest;
import com.airport.passengers.entity.Bagage;
import com.airport.passengers.entity.CategorieReclamation;
import com.airport.passengers.entity.Passager;
import com.airport.passengers.entity.Reclamation;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutReclamation;
import com.airport.passengers.repository.BagageRepository;
import com.airport.passengers.repository.ReclamationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Complaints.
 *
 * Rules:
 *  - the reservation and the bag, when given, must belong to the passenger;
 *  - a bag given without a reservation automatically links its reservation;
 *  - a BAGAGE complaint must name the bag;
 *  - status changes follow StatutReclamation.canTransitionTo();
 *  - closing (RESOLUE / REJETEE) requires an answer to the passenger.
 */
@Service
@Transactional
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final BagageRepository bagageRepository;
    private final PassagerService passagerService;
    private final ReservationService reservationService;
    private final Clock clock;

    public ReclamationService(ReclamationRepository reclamationRepository,
                              BagageRepository bagageRepository,
                              PassagerService passagerService,
                              ReservationService reservationService,
                              Clock clock) {
        this.reclamationRepository = reclamationRepository;
        this.bagageRepository = bagageRepository;
        this.passagerService = passagerService;
        this.reservationService = reservationService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> findAll(StatutReclamation statut, CategorieReclamation categorie) {
        return reclamationRepository.findWithFilters(statut, categorie).stream()
                .map(ReclamationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ReclamationResponse findById(Long id) {
        return ReclamationResponse.from(findEntity(id));
    }

    public ReclamationResponse create(ReclamationRequest request) {
        Passager passager = passagerService.findEntity(request.passagerId());

        Reservation reservation = null;
        if (request.reservationId() != null) {
            reservation = reservationService.findEntity(request.reservationId());
            if (!reservation.getPassager().getId().equals(passager.getId())) {
                throw new BusinessRuleException("Cette réservation n'appartient pas à ce passager");
            }
        }

        Bagage bagage = null;
        if (request.bagageId() != null) {
            bagage = bagageRepository.findDetailById(request.bagageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bagage", request.bagageId()));
            Reservation reservationDuBagage = bagage.getEnregistrement().getBillet().getReservation();
            if (!reservationDuBagage.getPassager().getId().equals(passager.getId())) {
                throw new BusinessRuleException("Ce bagage n'appartient pas à ce passager");
            }
            if (reservation != null && !reservation.getId().equals(reservationDuBagage.getId())) {
                throw new BusinessRuleException("Ce bagage ne correspond pas à la réservation choisie");
            }
            reservation = reservationDuBagage;
        }

        if (request.categorie() == CategorieReclamation.BAGAGE && bagage == null) {
            throw new BusinessRuleException("Une réclamation bagage doit indiquer le bagage concerné");
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setReference(generateReference());
        reclamation.setPassager(passager);
        reclamation.setReservation(reservation);
        reclamation.setBagage(bagage);
        reclamation.setCategorie(request.categorie());
        reclamation.setSujet(request.sujet().trim());
        reclamation.setDescription(request.description().trim());
        reclamation.setStatut(StatutReclamation.OUVERTE);
        return ReclamationResponse.from(reclamationRepository.save(reclamation));
    }

    public ReclamationResponse changerStatut(Long id, ReclamationStatutRequest request) {
        Reclamation reclamation = findEntity(id);
        StatutReclamation cible = request.statut();

        if (!reclamation.getStatut().canTransitionTo(cible)) {
            throw new BusinessRuleException("Transition impossible : " + reclamation.getStatut() + " → " + cible);
        }
        String reponse = request.reponse() == null ? "" : request.reponse().trim();
        if (cible.isFinal() && reponse.isEmpty()) {
            throw new BusinessRuleException("Une réponse au passager est obligatoire pour clôturer la réclamation");
        }

        reclamation.setStatut(cible);
        if (!reponse.isEmpty()) {
            reclamation.setReponse(reponse);
        }
        if (cible.isFinal()) {
            reclamation.setDateCloture(LocalDateTime.now(clock));
        }
        return ReclamationResponse.from(reclamation);
    }

    private Reclamation findEntity(Long id) {
        return reclamationRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réclamation", id));
    }

    private String generateReference() {
        String reference;
        do {
            reference = "REC-" + CodeGenerator.alphanumeric(6);
        } while (reclamationRepository.existsByReference(reference));
        return reference;
    }
}
