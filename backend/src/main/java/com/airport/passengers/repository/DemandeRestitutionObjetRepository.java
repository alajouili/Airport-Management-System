package com.airport.passengers.repository;

import com.airport.passengers.entity.DemandeRestitutionObjet;
import com.airport.passengers.entity.StatutRestitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeRestitutionObjetRepository extends JpaRepository<DemandeRestitutionObjet, Long> {

    List<DemandeRestitutionObjet> findByObjetTrouveIdOrderByDateDemandeAsc(Long objetTrouveId);

    List<DemandeRestitutionObjet> findByObjetTrouveIdAndStatut(Long objetTrouveId, StatutRestitution statut);

    boolean existsByObjetTrouveIdAndStatut(Long objetTrouveId, StatutRestitution statut);

    boolean existsByPassagerId(Long passagerId);
}
