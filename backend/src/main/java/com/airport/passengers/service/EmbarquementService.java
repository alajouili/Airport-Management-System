package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.operations.entity.StatutVol;
import com.airport.operations.entity.Vol;
import com.airport.operations.service.VolService;
import com.airport.passengers.dto.ControleSecuriteRequest;
import com.airport.passengers.dto.ControleSecuriteResponse;
import com.airport.passengers.dto.EmbarquementRequest;
import com.airport.passengers.dto.EvenementEmbarquementResponse;
import com.airport.passengers.dto.PassagerEmbarquementResponse;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.ControleSecuritePassager;
import com.airport.passengers.entity.EvenementEmbarquement;
import com.airport.passengers.entity.PointControleSecurite;
import com.airport.passengers.entity.ResultatControle;
import com.airport.passengers.entity.StatutBillet;
import com.airport.passengers.entity.TypeEvenementEmbarquement;
import com.airport.passengers.repository.AffectationSiegeBilletRepository;
import com.airport.passengers.repository.BilletRepository;
import com.airport.passengers.repository.ControleSecuritePassagerRepository;
import com.airport.passengers.repository.EnregistrementRepository;
import com.airport.passengers.repository.EvenementEmbarquementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Security checks and boarding.
 *
 * Rules:
 *  - only a checked-in, valid ticket (EMIS) on a flight that has not left can be controlled or boarded;
 *  - the checkpoint must be active;
 *  - boarding (EMBARQUE) requires that the LATEST security check is CONFORME;
 *  - EMBARQUE is final: the ticket becomes UTILISE (the review becomes possible);
 *  - refusing boarding requires a reason.
 */
@Service
@Transactional
public class EmbarquementService {

    private final BilletRepository billetRepository;
    private final EnregistrementRepository enregistrementRepository;
    private final AffectationSiegeBilletRepository affectationRepository;
    private final ControleSecuritePassagerRepository controleRepository;
    private final EvenementEmbarquementRepository evenementRepository;
    private final PointControleService pointControleService;
    private final BilletService billetService;
    private final VolService volService;

    public EmbarquementService(BilletRepository billetRepository,
                               EnregistrementRepository enregistrementRepository,
                               AffectationSiegeBilletRepository affectationRepository,
                               ControleSecuritePassagerRepository controleRepository,
                               EvenementEmbarquementRepository evenementRepository,
                               PointControleService pointControleService,
                               BilletService billetService,
                               VolService volService) {
        this.billetRepository = billetRepository;
        this.enregistrementRepository = enregistrementRepository;
        this.affectationRepository = affectationRepository;
        this.controleRepository = controleRepository;
        this.evenementRepository = evenementRepository;
        this.pointControleService = pointControleService;
        this.billetService = billetService;
        this.volService = volService;
    }

    /** The gate screen: every checked-in passenger of a flight with their current state. */
    @Transactional(readOnly = true)
    public List<PassagerEmbarquementResponse> getListe(Long volId) {
        volService.findEntity(volId); // 404 if the flight does not exist

        // Lists are sorted newest first, so putIfAbsent keeps the LATEST row per ticket.
        Map<Long, ControleSecuritePassager> derniersControles = new HashMap<>();
        for (ControleSecuritePassager c : controleRepository.findByVolIdNewestFirst(volId)) {
            derniersControles.putIfAbsent(c.getBillet().getId(), c);
        }
        Map<Long, EvenementEmbarquement> derniersEvenements = new HashMap<>();
        for (EvenementEmbarquement e : evenementRepository.findByVolIdNewestFirst(volId)) {
            derniersEvenements.putIfAbsent(e.getBillet().getId(), e);
        }

        return billetRepository.findCheckedInByVolId(volId, StatutBillet.ANNULE).stream()
                .map(billet -> {
                    ControleSecuritePassager controle = derniersControles.get(billet.getId());
                    EvenementEmbarquement evenement = derniersEvenements.get(billet.getId());
                    String siege = affectationRepository.findByBilletId(billet.getId())
                            .map(a -> a.getSiege().getNumero()).orElse(null);
                    boolean peutEmbarquer = billet.getStatut() == StatutBillet.EMIS
                            && controle != null && controle.getResultat() == ResultatControle.CONFORME;
                    return new PassagerEmbarquementResponse(
                            billet.getId(), billet.getNumeroBillet(), billet.getStatut(),
                            billet.getReservation().getId(), billet.getReservation().getReference(),
                            billet.getReservation().getPassager().getNomComplet(), billet.getReservation().getClasse(),
                            siege,
                            controle != null ? ControleSecuriteResponse.from(controle) : null,
                            evenement != null ? EvenementEmbarquementResponse.from(evenement) : null,
                            peutEmbarquer);
                })
                .toList();
    }

    public ControleSecuriteResponse enregistrerControle(Long billetId, ControleSecuriteRequest request) {
        Billet billet = billetService.findEntity(billetId);
        verifierBilletAuDepart(billet);

        PointControleSecurite point = pointControleService.findEntity(request.pointControleId());
        if (!Boolean.TRUE.equals(point.getActif())) {
            throw new BusinessRuleException("Ce point de contrôle est désactivé");
        }

        ControleSecuritePassager controle = controleRepository.save(
                new ControleSecuritePassager(billet, point, request.resultat(), blankToNull(request.commentaire())));
        return ControleSecuriteResponse.from(controle);
    }

    public EvenementEmbarquementResponse enregistrerEvenement(Long billetId, EmbarquementRequest request) {
        Billet billet = billetService.findEntity(billetId);
        verifierBilletAuDepart(billet);
        String commentaire = blankToNull(request.commentaire());

        if (request.typeEvenement() == TypeEvenementEmbarquement.EMBARQUE) {
            boolean controleConforme = controleRepository.findFirstByBilletIdOrderByDateControleDescIdDesc(billetId)
                    .map(c -> c.getResultat() == ResultatControle.CONFORME)
                    .orElse(false);
            if (!controleConforme) {
                throw new BusinessRuleException(
                        "Le passager doit d'abord passer le contrôle de sécurité avec un résultat conforme");
            }
            billet.setStatut(StatutBillet.UTILISE);
        }
        if (request.typeEvenement() == TypeEvenementEmbarquement.REFUSE && commentaire == null) {
            throw new BusinessRuleException("Indiquez le motif du refus d'embarquement");
        }

        EvenementEmbarquement evenement = evenementRepository.save(
                new EvenementEmbarquement(billet, request.typeEvenement(), request.porte().trim().toUpperCase(), commentaire));
        return EvenementEmbarquementResponse.from(evenement);
    }

    private void verifierBilletAuDepart(Billet billet) {
        if (billet.getStatut() == StatutBillet.UTILISE) {
            throw new BusinessRuleException("Ce passager a déjà embarqué");
        }
        if (billet.getStatut() != StatutBillet.EMIS) {
            throw new BusinessRuleException("Ce billet n'est plus valide");
        }
        if (enregistrementRepository.findByBilletId(billet.getId()).isEmpty()) {
            throw new BusinessRuleException("Le passager n'est pas encore enregistré");
        }
        Vol vol = billet.getReservation().getVol();
        if (vol.getStatut() == StatutVol.PARTI || vol.getStatut() == StatutVol.ARRIVE
                || vol.getStatut() == StatutVol.ANNULE) {
            throw new BusinessRuleException("Le vol n'est plus à l'embarquement (statut " + vol.getStatut() + ")");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
