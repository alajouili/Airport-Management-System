package com.airport.operations.repository;

import com.airport.operations.entity.Vol;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/** OWNED BY STUDENT A - temporary minimal version. */
public interface VolRepository extends JpaRepository<Vol, Long> {

    /**
     * Flights departing after the given date, earliest first.
     * The method name is translated by Spring Data into:
     *   WHERE date_heure_depart >= ? ORDER BY date_heure_depart ASC
     * @EntityGraph loads the airline in the same query (avoids the "N+1 queries" problem).
     */
    @EntityGraph(attributePaths = "compagnie")
    List<Vol> findByDateHeureDepartGreaterThanEqualOrderByDateHeureDepartAsc(LocalDateTime from);
}
