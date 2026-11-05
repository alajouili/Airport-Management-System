package com.airport.passengers.repository;

import com.airport.passengers.entity.DemandeAssistancePassager;
import com.airport.passengers.entity.StatutAssistance;
import com.airport.passengers.entity.TypeAssistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeAssistancePassagerRepository extends JpaRepository<DemandeAssistancePassager, Long> {

    @Query("""
            SELECT d FROM DemandeAssistancePassager d
            JOIN FETCH d.reservation r
            JOIN FETCH r.passager
            JOIN FETCH r.vol v
            WHERE (:statut IS NULL OR d.statut = :statut)
              AND (:volId IS NULL OR v.id = :volId)
            ORDER BY v.dateHeureDepart ASC, d.dateDemande ASC
            """)
    List<DemandeAssistancePassager> findWithFilters(@Param("statut") StatutAssistance statut,
                                                    @Param("volId") Long volId);

    List<DemandeAssistancePassager> findByReservationIdOrderByDateDemandeAsc(Long reservationId);

    boolean existsByReservationIdAndTypeAssistanceAndStatutNot(Long reservationId, TypeAssistance type,
                                                               StatutAssistance statut);
}
