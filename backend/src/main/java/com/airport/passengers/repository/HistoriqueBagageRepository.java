package com.airport.passengers.repository;

import com.airport.passengers.entity.HistoriqueBagage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueBagageRepository extends JpaRepository<HistoriqueBagage, Long> {

    List<HistoriqueBagage> findByBagageIdOrderByDateChangementAscIdAsc(Long bagageId);
}
