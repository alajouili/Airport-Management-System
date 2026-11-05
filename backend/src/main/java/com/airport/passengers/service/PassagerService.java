package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.passengers.dto.PassagerRequest;
import com.airport.passengers.dto.PassagerResponse;
import com.airport.passengers.entity.Passager;
import com.airport.passengers.repository.DemandeRestitutionObjetRepository;
import com.airport.passengers.repository.PassagerRepository;
import com.airport.passengers.repository.ReclamationRepository;
import com.airport.passengers.repository.ReservationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for passengers.
 * @Transactional: each public method runs in one database transaction;
 * if an exception is thrown, every change of the method is cancelled (rollback).
 */
@Service
@Transactional
public class PassagerService {

    private final PassagerRepository passagerRepository;
    private final ReservationRepository reservationRepository;
    private final ReclamationRepository reclamationRepository;
    private final DemandeRestitutionObjetRepository restitutionRepository;

    public PassagerService(PassagerRepository passagerRepository,
                           ReservationRepository reservationRepository,
                           ReclamationRepository reclamationRepository,
                           DemandeRestitutionObjetRepository restitutionRepository) {
        this.passagerRepository = passagerRepository;
        this.reservationRepository = reservationRepository;
        this.reclamationRepository = reclamationRepository;
        this.restitutionRepository = restitutionRepository;
    }

    @Transactional(readOnly = true)
    public List<PassagerResponse> findAll(String recherche) {
        String texte = recherche == null ? "" : recherche.trim();
        return passagerRepository.search(texte, Sort.by("nom", "prenom"))
                .stream()
                .map(PassagerResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PassagerResponse findById(Long id) {
        return PassagerResponse.from(findEntity(id));
    }

    public PassagerResponse create(PassagerRequest request) {
        String email = request.email().trim().toLowerCase();
        String passeport = blankToNull(request.numeroPasseport());

        if (passagerRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("Un passager utilise déjà cet email");
        }
        if (passeport != null && passagerRepository.existsByNumeroPasseport(passeport)) {
            throw new BusinessRuleException("Un passager utilise déjà ce numéro de passeport");
        }

        Passager passager = new Passager();
        apply(passager, request, email, passeport);
        return PassagerResponse.from(passagerRepository.save(passager));
    }

    public PassagerResponse update(Long id, PassagerRequest request) {
        Passager passager = findEntity(id);
        String email = request.email().trim().toLowerCase();
        String passeport = blankToNull(request.numeroPasseport());

        if (passagerRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new BusinessRuleException("Un autre passager utilise déjà cet email");
        }
        if (passeport != null && passagerRepository.existsByNumeroPasseportAndIdNot(passeport, id)) {
            throw new BusinessRuleException("Un autre passager utilise déjà ce numéro de passeport");
        }

        apply(passager, request, email, passeport);
        // No save() needed: the entity is "managed" by JPA inside the transaction,
        // so its changes are written automatically at commit.
        return PassagerResponse.from(passager);
    }

    public void delete(Long id) {
        Passager passager = findEntity(id);
        if (reservationRepository.existsByPassagerId(id)
                || reclamationRepository.existsByPassagerId(id)
                || restitutionRepository.existsByPassagerId(id)) {
            throw new BusinessRuleException(
                    "Ce passager a un historique (réservations, réclamations ou objets) : il ne peut pas être supprimé");
        }
        passagerRepository.delete(passager);
    }

    /** Used by other services of the module. */
    @Transactional(readOnly = true)
    public Passager findEntity(Long id) {
        return passagerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passager", id));
    }

    private void apply(Passager passager, PassagerRequest request, String email, String passeport) {
        passager.setNom(request.nom().trim());
        passager.setPrenom(request.prenom().trim());
        passager.setEmail(email);
        passager.setTelephone(blankToNull(request.telephone()));
        passager.setDateNaissance(request.dateNaissance());
        passager.setNationalite(blankToNull(request.nationalite()));
        passager.setNumeroPasseport(passeport);
    }

    /** Angular sends "" for an empty optional field; the database must store NULL
     *  (two "" passports would break the UNIQUE constraint). */
    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
