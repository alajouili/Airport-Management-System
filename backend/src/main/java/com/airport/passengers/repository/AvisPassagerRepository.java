package com.airport.passengers.repository;

import com.airport.passengers.entity.AvisPassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AvisPassagerRepository extends JpaRepository<AvisPassager, Long> {

    @Query("""
            SELECT a FROM AvisPassager a
            JOIN FETCH a.reservation r
            JOIN FETCH r.passager
            JOIN FETCH r.vol v
            JOIN FETCH v.compagnie
            WHERE (:note IS NULL OR a.note = :note)
            ORDER BY a.dateAvis DESC
            """)
    List<AvisPassager> findWithFilters(@Param("note") Integer note);

    Optional<AvisPassager> findByReservationId(Long reservationId);

    boolean existsByReservationId(Long reservationId);
}
