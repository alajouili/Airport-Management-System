package com.airport.passengers.repository;

import com.airport.passengers.entity.EvenementTraitementBagage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementTraitementBagageRepository extends JpaRepository<EvenementTraitementBagage, Long> {

    List<EvenementTraitementBagage> findByBagageIdOrderByDateEvenementAscIdAsc(Long bagageId);
}
