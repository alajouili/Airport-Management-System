package com.airport.passengers.repository;

import com.airport.passengers.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByReservationIdOrderByDatePaiementDesc(Long reservationId);

    boolean existsByReferenceTransaction(String referenceTransaction);
}
