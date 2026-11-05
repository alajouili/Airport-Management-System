package com.airport.passengers.repository;

import com.airport.passengers.entity.CategorieReclamation;
import com.airport.passengers.entity.Reclamation;
import com.airport.passengers.entity.StatutReclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    /** LEFT JOIN FETCH: reservation and bag are optional, a normal JOIN would hide those rows. */
    @Query("""
            SELECT rc FROM Reclamation rc
            JOIN FETCH rc.passager
            LEFT JOIN FETCH rc.reservation
            LEFT JOIN FETCH rc.bagage
            WHERE (:statut IS NULL OR rc.statut = :statut)
              AND (:categorie IS NULL OR rc.categorie = :categorie)
            ORDER BY rc.dateCreation DESC
            """)
    List<Reclamation> findWithFilters(@Param("statut") StatutReclamation statut,
                                      @Param("categorie") CategorieReclamation categorie);

    @Query("""
            SELECT rc FROM Reclamation rc
            JOIN FETCH rc.passager
            LEFT JOIN FETCH rc.reservation
            LEFT JOIN FETCH rc.bagage
            WHERE rc.id = :id
            """)
    Optional<Reclamation> findDetailById(@Param("id") Long id);

    List<Reclamation> findByReservationIdOrderByDateCreationDesc(Long reservationId);

    boolean existsByPassagerId(Long passagerId);

    boolean existsByReference(String reference);
}
