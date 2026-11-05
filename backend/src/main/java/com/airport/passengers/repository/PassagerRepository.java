package com.airport.passengers.repository;

import com.airport.passengers.entity.Passager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PassagerRepository extends JpaRepository<Passager, Long> {

    /**
     * Search by last name, first name, email or passport number (case-insensitive).
     * An empty search text ("") matches every passenger.
     * JPQL works on entity and field names (Passager, nom), not table names.
     */
    @Query("""
            SELECT p FROM Passager p
            WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :texte, '%'))
               OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :texte, '%'))
               OR LOWER(p.email) LIKE LOWER(CONCAT('%', :texte, '%'))
               OR LOWER(COALESCE(p.numeroPasseport, '')) LIKE LOWER(CONCAT('%', :texte, '%'))
            """)
    List<Passager> search(@Param("texte") String texte, Sort sort);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByNumeroPasseport(String numeroPasseport);

    boolean existsByNumeroPasseportAndIdNot(String numeroPasseport, Long id);
}
