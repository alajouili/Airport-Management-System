package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.common.util.CodeGenerator;
import com.airport.operations.entity.StatutVol;
import com.airport.operations.entity.Vol;
import com.airport.passengers.dto.BagageDetailResponse;
import com.airport.passengers.dto.BagageRequest;
import com.airport.passengers.dto.BagageResponse;
import com.airport.passengers.dto.EvenementBagageRequest;
import com.airport.passengers.dto.EvenementBagageResponse;
import com.airport.passengers.dto.HistoriqueBagageResponse;
import com.airport.passengers.entity.Bagage;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.Enregistrement;
import com.airport.passengers.entity.EvenementTraitementBagage;
import com.airport.passengers.entity.HistoriqueBagage;
import com.airport.passengers.entity.StatutBagage;
import com.airport.passengers.entity.StatutBillet;
import com.airport.passengers.entity.TypeBagage;
import com.airport.passengers.repository.BagageRepository;
import com.airport.passengers.repository.EnregistrementRepository;
import com.airport.passengers.repository.EvenementTraitementBagageRepository;
import com.airport.passengers.repository.HistoriqueBagageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Baggage registration and tracking.
 *
 * Rules:
 *  - a bag is added to a check-in whose ticket is valid and whose flight has not left;
 *  - max weight: 32 kg in the hold (SOUTE), 10 kg in the cabin (CABINE);
 *  - max 3 hold bags and 1 cabin bag per passenger;
 *  - every status change follows StatutBagage.canTransitionTo() and is written
 *    to historique_bagage automatically.
 */
@Service
@Transactional
public class BagageService {

    public static final int MAX_BAGAGES_SOUTE = 3;
    public static final int MAX_BAGAGES_CABINE = 1;

    private final BagageRepository bagageRepository;
    private final EnregistrementRepository enregistrementRepository;
    private final HistoriqueBagageRepository historiqueRepository;
    private final EvenementTraitementBagageRepository evenementRepository;

    public BagageService(BagageRepository bagageRepository,
                         EnregistrementRepository enregistrementRepository,
                         HistoriqueBagageRepository historiqueRepository,
                         EvenementTraitementBagageRepository evenementRepository) {
        this.bagageRepository = bagageRepository;
        this.enregistrementRepository = enregistrementRepository;
        this.historiqueRepository = historiqueRepository;
        this.evenementRepository = evenementRepository;
    }

    @Transactional(readOnly = true)
    public List<BagageResponse> findAll(StatutBagage statut, String etiquette) {
        String texte = etiquette == null ? "" : etiquette.trim();
        return bagageRepository.findWithFilters(statut, texte)
                .stream()
                .map(BagageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BagageDetailResponse findDetail(Long id) {
        Bagage bagage = bagageRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage", id));
        return toDetail(bagage);
    }

    public BagageResponse ajouter(Long enregistrementId, BagageRequest request) {
        Enregistrement enregistrement = enregistrementRepository.findById(enregistrementId)
                .orElseThrow(() -> new ResourceNotFoundException("Enregistrement", enregistrementId));
        Billet billet = enregistrement.getBillet();
        Vol vol = billet.getReservation().getVol();

        if (billet.getStatut() != StatutBillet.EMIS) {
            throw new BusinessRuleException("Le billet n'est plus valide");
        }
        if (vol.getStatut() == StatutVol.PARTI || vol.getStatut() == StatutVol.ARRIVE
                || vol.getStatut() == StatutVol.ANNULE) {
            throw new BusinessRuleException("Le vol n'accepte plus de bagages (statut " + vol.getStatut() + ")");
        }
        if (request.poidsKg().compareTo(request.type().getPoidsMaxKg()) > 0) {
            throw new BusinessRuleException("Poids maximum pour un bagage " + request.type()
                    + " : " + request.type().getPoidsMaxKg() + " kg");
        }
        long dejaEnregistres = bagageRepository.countByEnregistrementIdAndType(enregistrementId, request.type());
        int maximum = request.type() == TypeBagage.SOUTE ? MAX_BAGAGES_SOUTE : MAX_BAGAGES_CABINE;
        if (dejaEnregistres >= maximum) {
            throw new BusinessRuleException("Nombre maximum de bagages " + request.type() + " atteint (" + maximum + ")");
        }

        Bagage bagage = new Bagage();
        bagage.setEnregistrement(enregistrement);
        bagage.setType(request.type());
        bagage.setPoidsKg(request.poidsKg());
        bagage.setStatut(StatutBagage.ENREGISTRE);
        bagage.setNumeroEtiquette(generateEtiquette(vol.getCompagnie().getCodeIata()));
        bagageRepository.save(bagage);

        historiqueRepository.save(new HistoriqueBagage(bagage, null, StatutBagage.ENREGISTRE,
                "Bagage enregistré"));
        return BagageResponse.from(bagage);
    }

    public BagageDetailResponse ajouterEvenement(Long bagageId, EvenementBagageRequest request) {
        Bagage bagage = bagageRepository.findDetailById(bagageId)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage", bagageId));

        StatutBagage cible = request.typeEvenement().getStatutCible();
        StatutBagage actuel = bagage.getStatut();
        if (cible != null && !actuel.canTransitionTo(cible)) {
            throw new BusinessRuleException("Transition impossible : " + actuel + " → " + cible);
        }

        evenementRepository.save(new EvenementTraitementBagage(
                bagage, request.typeEvenement(), request.lieu().trim(), request.commentaire()));

        if (cible != null) {
            bagage.setStatut(cible);
            String commentaire = request.typeEvenement() + " à " + request.lieu().trim();
            historiqueRepository.save(new HistoriqueBagage(bagage, actuel, cible, commentaire));
        }
        return toDetail(bagage);
    }

    private BagageDetailResponse toDetail(Bagage bagage) {
        List<HistoriqueBagageResponse> historique = historiqueRepository
                .findByBagageIdOrderByDateChangementAscIdAsc(bagage.getId())
                .stream().map(HistoriqueBagageResponse::from).toList();
        List<EvenementBagageResponse> evenements = evenementRepository
                .findByBagageIdOrderByDateEvenementAscIdAsc(bagage.getId())
                .stream().map(EvenementBagageResponse::from).toList();
        return new BagageDetailResponse(BagageResponse.from(bagage), historique, evenements);
    }

    /** Airline code + 6 digits, for example "TU482913". */
    private String generateEtiquette(String codeCompagnie) {
        String etiquette;
        do {
            etiquette = codeCompagnie + CodeGenerator.digits(6);
        } while (bagageRepository.existsByNumeroEtiquette(etiquette));
        return etiquette;
    }
}
