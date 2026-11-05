package com.airport.passengers.repository;

import com.airport.passengers.entity.EvenementEmbarquement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvenementEmbarquementRepository extends JpaRepository<EvenementEmbarquement, Long> {

    List<EvenementEmbarquement> findByBilletIdOrderByDateEvenementAscIdAsc(Long billetId);

    @Query("""
            SELECT e FROM EvenementEmbarquement e
            WHERE e.billet.reservation.vol.id = :volId
            ORDER BY e.dateEvenement DESC, e.id DESC
            """)
    List<EvenementEmbarquement> findByVolIdNewestFirst(@Param("volId") Long volId);
}
