package com.airport.passengers.repository;

import com.airport.passengers.entity.Enregistrement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnregistrementRepository extends JpaRepository<Enregistrement, Long> {

    Optional<Enregistrement> findByBilletId(Long billetId);
}
