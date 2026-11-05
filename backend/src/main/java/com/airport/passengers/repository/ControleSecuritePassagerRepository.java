package com.airport.passengers.repository;

import com.airport.passengers.entity.ControleSecuritePassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ControleSecuritePassagerRepository extends JpaRepository<ControleSecuritePassager, Long> {

    /** The most recent check of a ticket: it decides if boarding is allowed. */
    Optional<ControleSecuritePassager> findFirstByBilletIdOrderByDateControleDescIdDesc(Long billetId);

    /** All checks of the tickets of a flight, newest first (for the gate screen). */
    @Query("""
            SELECT c FROM ControleSecuritePassager c
            JOIN FETCH c.pointControle
            WHERE c.billet.reservation.vol.id = :volId
            ORDER BY c.dateControle DESC, c.id DESC
            """)
    List<ControleSecuritePassager> findByVolIdNewestFirst(@Param("volId") Long volId);

    boolean existsByPointControleId(Long pointControleId);
}
