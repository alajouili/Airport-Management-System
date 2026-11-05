package com.airport.passengers.repository;

import com.airport.passengers.entity.PointControleSecurite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointControleSecuriteRepository extends JpaRepository<PointControleSecurite, Long> {

    List<PointControleSecurite> findAllByOrderByTerminalAscCodeAsc();

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
