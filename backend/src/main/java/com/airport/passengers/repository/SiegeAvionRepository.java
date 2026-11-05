package com.airport.passengers.repository;

import com.airport.passengers.entity.SiegeAvion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiegeAvionRepository extends JpaRepository<SiegeAvion, Long> {

    List<SiegeAvion> findByVolIdOrderByIdAsc(Long volId);
}
