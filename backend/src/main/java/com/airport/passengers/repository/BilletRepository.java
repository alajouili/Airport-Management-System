package com.airport.passengers.repository;

import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.StatutBillet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BilletRepository extends JpaRepository<Billet, Long> {

    Optional<Billet> findByReservationId(Long reservationId);

    boolean existsByNumeroBillet(String numeroBillet);

    /**
     * Tickets of a flight that are checked in (they have an enregistrement row)
     * and not cancelled: the passengers expected at the gate.
     */
    @Query("""
            SELECT b FROM Billet b
            JOIN FETCH b.reservation r
            JOIN FETCH r.passager p
            WHERE r.vol.id = :volId
              AND b.statut <> :annule
              AND EXISTS (SELECT 1 FROM Enregistrement e WHERE e.billet = b)
            ORDER BY p.nom, p.prenom
            """)
    List<Billet> findCheckedInByVolId(@Param("volId") Long volId, @Param("annule") StatutBillet annule);
}
