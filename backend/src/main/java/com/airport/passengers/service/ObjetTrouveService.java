package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.common.util.CodeGenerator;
import com.airport.passengers.dto.DecisionRestitutionRequest;
import com.airport.passengers.dto.ObjetTrouveDetailResponse;
import com.airport.passengers.dto.ObjetTrouveRequest;
import com.airport.passengers.dto.ObjetTrouveResponse;
import com.airport.passengers.dto.RestitutionRequest;
import com.airport.passengers.dto.RestitutionResponse;
import com.airport.passengers.entity.CategorieObjet;
import com.airport.passengers.entity.DemandeRestitutionObjet;
import com.airport.passengers.entity.ObjetTrouve;
import com.airport.passengers.entity.StatutObjet;
import com.airport.passengers.entity.StatutRestitution;
import com.airport.passengers.repository.DemandeRestitutionObjetRepository;
import com.airport.passengers.repository.ObjetTrouveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lost and found.
 *
 * Rules:
 *  - a claim can be made only for an object still in stock;
 *  - accepting a claim gives the object back (RESTITUE) and automatically refuses
 *    the other pending claims; an object is given back only once;
 *  - refusing a claim requires a comment;
 *  - an object can be eliminated only after 90 days in stock and with no pending claim.
 */
@Service
@Transactional
public class ObjetTrouveService {

    public static final Duration DUREE_CONSERVATION = Duration.ofDays(90);

    private final ObjetTrouveRepository objetRepository;
    private final DemandeRestitutionObjetRepository demandeRepository;
    private final PassagerService passagerService;
    private final Clock clock;

    public ObjetTrouveService(ObjetTrouveRepository objetRepository,
                              DemandeRestitutionObjetRepository demandeRepository,
                              PassagerService passagerService,
                              Clock clock) {
        this.objetRepository = objetRepository;
        this.demandeRepository = demandeRepository;
        this.passagerService = passagerService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<ObjetTrouveResponse> findAll(StatutObjet statut, CategorieObjet categorie, String recherche) {
        String texte = recherche == null ? "" : recherche.trim();
        return objetRepository.findWithFilters(statut, categorie, texte).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ObjetTrouveDetailResponse findDetail(Long id) {
        return toDetail(findEntity(id));
    }

    public ObjetTrouveResponse create(ObjetTrouveRequest request) {
        if (request.dateTrouve().isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("La date de découverte ne peut pas être dans le futur");
        }
        ObjetTrouve objet = new ObjetTrouve();
        objet.setReference(generateReference());
        objet.setDescription(request.description().trim());
        objet.setCategorie(request.categorie());
        objet.setLieuTrouve(request.lieuTrouve().trim());
        objet.setDateTrouve(request.dateTrouve());
        objet.setStatut(StatutObjet.EN_STOCK);
        return toResponse(objetRepository.save(objet));
    }

    public ObjetTrouveDetailResponse ajouterDemande(Long objetId, RestitutionRequest request) {
        ObjetTrouve objet = findEntity(objetId);
        if (objet.getStatut() != StatutObjet.EN_STOCK) {
            throw new BusinessRuleException("Cet objet n'est plus disponible");
        }

        DemandeRestitutionObjet demande = new DemandeRestitutionObjet();
        demande.setObjetTrouve(objet);
        if (request.passagerId() != null) {
            demande.setPassager(passagerService.findEntity(request.passagerId()));
        }
        demande.setNomDemandeur(request.nomDemandeur().trim());
        demande.setContact(request.contact().trim());
        demande.setDescriptionPreuve(request.descriptionPreuve().trim());
        demandeRepository.save(demande);
        return toDetail(objet);
    }

    public ObjetTrouveDetailResponse decider(Long demandeId, DecisionRestitutionRequest request) {
        DemandeRestitutionObjet demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de restitution", demandeId));
        if (demande.getStatut() != StatutRestitution.EN_ATTENTE) {
            throw new BusinessRuleException("Cette demande a déjà été traitée");
        }
        ObjetTrouve objet = demande.getObjetTrouve();
        LocalDateTime maintenant = LocalDateTime.now(clock);
        String commentaire = request.commentaire() == null || request.commentaire().isBlank()
                ? null : request.commentaire().trim();

        if (request.acceptee()) {
            if (objet.getStatut() != StatutObjet.EN_STOCK) {
                throw new BusinessRuleException("Cet objet n'est plus disponible");
            }
            demande.decider(StatutRestitution.ACCEPTEE, commentaire, maintenant);
            objet.setStatut(StatutObjet.RESTITUE);
            objet.setDateSortie(maintenant);
            for (DemandeRestitutionObjet autre : demandeRepository.findByObjetTrouveIdAndStatut(
                    objet.getId(), StatutRestitution.EN_ATTENTE)) {
                if (!autre.getId().equals(demande.getId())) {
                    autre.decider(StatutRestitution.REFUSEE, "Objet restitué à un autre demandeur", maintenant);
                }
            }
        } else {
            if (commentaire == null) {
                throw new BusinessRuleException("Indiquez le motif du refus");
            }
            demande.decider(StatutRestitution.REFUSEE, commentaire, maintenant);
        }
        return toDetail(objet);
    }

    public ObjetTrouveDetailResponse eliminer(Long objetId) {
        ObjetTrouve objet = findEntity(objetId);
        if (objet.getStatut() != StatutObjet.EN_STOCK) {
            throw new BusinessRuleException("Cet objet n'est plus en stock");
        }
        if (!delaiConservationDepasse(objet)) {
            throw new BusinessRuleException("Un objet doit être conservé au moins 90 jours avant d'être éliminé");
        }
        if (demandeRepository.existsByObjetTrouveIdAndStatut(objetId, StatutRestitution.EN_ATTENTE)) {
            throw new BusinessRuleException("Une demande de restitution est en attente pour cet objet");
        }
        objet.setStatut(StatutObjet.ELIMINE);
        objet.setDateSortie(LocalDateTime.now(clock));
        return toDetail(objet);
    }

    private ObjetTrouve findEntity(Long id) {
        return objetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Objet trouvé", id));
    }

    private boolean delaiConservationDepasse(ObjetTrouve objet) {
        return !objet.getDateTrouve().plus(DUREE_CONSERVATION).isAfter(LocalDateTime.now(clock));
    }

    private ObjetTrouveResponse toResponse(ObjetTrouve objet) {
        boolean eliminable = objet.getStatut() == StatutObjet.EN_STOCK && delaiConservationDepasse(objet);
        return ObjetTrouveResponse.from(objet, eliminable);
    }

    private ObjetTrouveDetailResponse toDetail(ObjetTrouve objet) {
        List<RestitutionResponse> demandes = demandeRepository.findByObjetTrouveIdOrderByDateDemandeAsc(objet.getId())
                .stream().map(RestitutionResponse::from).toList();
        return new ObjetTrouveDetailResponse(toResponse(objet), demandes);
    }

    private String generateReference() {
        String reference;
        do {
            reference = "OBJ-" + CodeGenerator.alphanumeric(6);
        } while (objetRepository.existsByReference(reference));
        return reference;
    }
}
