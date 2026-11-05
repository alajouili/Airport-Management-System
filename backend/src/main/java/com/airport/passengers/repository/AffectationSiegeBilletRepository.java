package com.airport.passengers.repository;

import com.airport.passengers.entity.AffectationSiegeBillet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AffectationSiegeBilletRepository extends JpaRepository<AffectationSiegeBillet, Long> {

    Optional<AffectationSiegeBillet> findByBilletId(Long billetId);

    boolean existsBySiegeId(Long siegeId);

    /** Ids of the seats already taken on a flight. */
    @Query("SELECT a.siege.id FROM AffectationSiegeBillet a WHERE a.siege.vol.id = :volId")
    List<Long> findOccupiedSeatIds(@Param("volId") Long volId);
}
