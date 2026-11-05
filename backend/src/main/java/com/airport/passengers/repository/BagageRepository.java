package com.airport.passengers.repository;

import com.airport.passengers.entity.Bagage;
import com.airport.passengers.entity.StatutBagage;
import com.airport.passengers.entity.TypeBagage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BagageRepository extends JpaRepository<Bagage, Long> {

    /** Bags with everything needed for the list (passenger, flight) in one query. */
    @Query("""
            SELECT b FROM Bagage b
            JOIN FETCH b.enregistrement e
            JOIN FETCH e.billet bi
            JOIN FETCH bi.reservation r
            JOIN FETCH r.passager
            JOIN FETCH r.vol
            WHERE (:statut IS NULL OR b.statut = :statut)
              AND UPPER(b.numeroEtiquette) LIKE UPPER(CONCAT('%', :etiquette, '%'))
            ORDER BY b.dateCreation DESC
            """)
    List<Bagage> findWithFilters(@Param("statut") StatutBagage statut,
                                 @Param("etiquette") String etiquette);

    @Query("""
            SELECT b FROM Bagage b
            JOIN FETCH b.enregistrement e
            JOIN FETCH e.billet bi
            JOIN FETCH bi.reservation r
            JOIN FETCH r.passager
            JOIN FETCH r.vol
            WHERE b.id = :id
            """)
    Optional<Bagage> findDetailById(@Param("id") Long id);

    List<Bagage> findByEnregistrementIdOrderByIdAsc(Long enregistrementId);

    long countByEnregistrementIdAndType(Long enregistrementId, TypeBagage type);

    boolean existsByNumeroEtiquette(String numeroEtiquette);
}
