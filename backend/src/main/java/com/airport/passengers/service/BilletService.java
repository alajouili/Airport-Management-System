package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.operations.entity.Vol;
import com.airport.passengers.dto.AffectationSiegeRequest;
import com.airport.passengers.dto.BilletResponse;
import com.airport.passengers.dto.EnregistrementRequest;
import com.airport.passengers.entity.AffectationSiegeBillet;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.Enregistrement;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.SiegeAvion;
import com.airport.passengers.entity.StatutBillet;
import com.airport.passengers.repository.AffectationSiegeBilletRepository;
import com.airport.passengers.repository.BilletRepository;
import com.airport.passengers.repository.EnregistrementRepository;
import com.airport.passengers.repository.SiegeAvionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Seat selection and check-in of a ticket.
 *
 * Rules:
 *  - only a valid ticket (EMIS) can choose a seat or check in;
 *  - the seat must belong to the same flight and the same class, and be free;
 *  - the seat can no longer change after check-in;
 *  - check-in is open from 48 h to 45 min before departure, and happens once;
 *  - a passenger without a seat gets the first free seat of his class at check-in.
 */
@Service
@Transactional
public class BilletService {

    public static final Duration OUVERTURE_ENREGISTREMENT = Duration.ofHours(48);
    public static final Duration FERMETURE_ENREGISTREMENT = Duration.ofMinutes(45);

    private final BilletRepository billetRepository;
    private final SiegeAvionRepository siegeRepository;
    private final AffectationSiegeBilletRepository affectationRepository;
    private final EnregistrementRepository enregistrementRepository;
    private final SiegeService siegeService;
    private final Clock clock;

    public BilletService(BilletRepository billetRepository,
                         SiegeAvionRepository siegeRepository,
                         AffectationSiegeBilletRepository affectationRepository,
                         EnregistrementRepository enregistrementRepository,
                         SiegeService siegeService,
                         Clock clock) {
        this.billetRepository = billetRepository;
        this.siegeRepository = siegeRepository;
        this.affectationRepository = affectationRepository;
        this.enregistrementRepository = enregistrementRepository;
        this.siegeService = siegeService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public BilletResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    public BilletResponse choisirSiege(Long billetId, AffectationSiegeRequest request) {
        Billet billet = findEntity(billetId);
        verifierBilletValide(billet);
        if (enregistrementRepository.findByBilletId(billetId).isPresent()) {
            throw new BusinessRuleException("Le passager est déjà enregistré : le siège ne peut plus changer");
        }

        Reservation reservation = billet.getReservation();
        SiegeAvion siege = siegeRepository.findById(request.siegeId())
                .orElseThrow(() -> new ResourceNotFoundException("Siège", request.siegeId()));

        if (!siege.getVol().getId().equals(reservation.getVol().getId())) {
            throw new BusinessRuleException("Ce siège n'appartient pas au vol de la réservation");
        }
        if (siege.getClasse() != reservation.getClasse()) {
            throw new BusinessRuleException("Ce siège n'est pas dans la classe réservée");
        }

        AffectationSiegeBillet affectation = affectationRepository.findByBilletId(billetId).orElse(null);
        boolean memeSiege = affectation != null && affectation.getSiege().getId().equals(siege.getId());
        if (!memeSiege && affectationRepository.existsBySiegeId(siege.getId())) {
            throw new BusinessRuleException("Ce siège est déjà occupé");
        }

        if (affectation == null) {
            affectation = new AffectationSiegeBillet();
            affectation.setBillet(billet);
        }
        affectation.setSiege(siege);
        affectationRepository.save(affectation);
        return toResponse(billet);
    }

    public BilletResponse enregistrer(Long billetId, EnregistrementRequest request) {
        Billet billet = findEntity(billetId);
        verifierBilletValide(billet);
        if (enregistrementRepository.findByBilletId(billetId).isPresent()) {
            throw new BusinessRuleException("Ce billet est déjà enregistré");
        }

        Vol vol = billet.getReservation().getVol();
        LocalDateTime maintenant = LocalDateTime.now(clock);
        LocalDateTime ouverture = vol.getDateHeureDepart().minus(OUVERTURE_ENREGISTREMENT);
        LocalDateTime fermeture = vol.getDateHeureDepart().minus(FERMETURE_ENREGISTREMENT);
        if (maintenant.isBefore(ouverture)) {
            throw new BusinessRuleException("L'enregistrement ouvre 48 h avant le départ");
        }
        if (maintenant.isAfter(fermeture)) {
            throw new BusinessRuleException("L'enregistrement est fermé (45 min avant le départ)");
        }

        if (affectationRepository.findByBilletId(billetId).isEmpty()) {
            attribuerPremierSiegeLibre(billet);
        }

        Enregistrement enregistrement = new Enregistrement();
        enregistrement.setBillet(billet);
        enregistrement.setCanal(request.canal());
        enregistrementRepository.save(enregistrement);
        return toResponse(billet);
    }

    // ------------------------------------------------------------ helpers

    public Billet findEntity(Long id) {
        return billetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billet", id));
    }

    private void verifierBilletValide(Billet billet) {
        if (billet.getStatut() != StatutBillet.EMIS) {
            throw new BusinessRuleException("Ce billet n'est plus valide (statut " + billet.getStatut() + ")");
        }
    }

    private void attribuerPremierSiegeLibre(Billet billet) {
        Reservation reservation = billet.getReservation();
        List<SiegeAvion> sieges = siegeService.ensureSieges(reservation.getVol());
        Set<Long> occupes = new HashSet<>(affectationRepository.findOccupiedSeatIds(reservation.getVol().getId()));

        SiegeAvion siegeLibre = sieges.stream()
                .filter(s -> s.getClasse() == reservation.getClasse())
                .filter(s -> !occupes.contains(s.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Aucun siège libre dans cette classe"));

        AffectationSiegeBillet affectation = new AffectationSiegeBillet();
        affectation.setBillet(billet);
        affectation.setSiege(siegeLibre);
        affectationRepository.save(affectation);
    }

    private BilletResponse toResponse(Billet billet) {
        AffectationSiegeBillet affectation = affectationRepository.findByBilletId(billet.getId()).orElse(null);
        Enregistrement enregistrement = enregistrementRepository.findByBilletId(billet.getId()).orElse(null);
        return BilletResponse.from(billet, affectation, enregistrement);
    }
}
