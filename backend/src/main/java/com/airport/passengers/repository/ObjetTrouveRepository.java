package com.airport.passengers.repository;

import com.airport.passengers.entity.CategorieObjet;
import com.airport.passengers.entity.ObjetTrouve;
import com.airport.passengers.entity.StatutObjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjetTrouveRepository extends JpaRepository<ObjetTrouve, Long> {

    @Query("""
            SELECT o FROM ObjetTrouve o
            WHERE (:statut IS NULL OR o.statut = :statut)
              AND (:categorie IS NULL OR o.categorie = :categorie)
              AND LOWER(o.description) LIKE LOWER(CONCAT('%', :texte, '%'))
            ORDER BY o.dateTrouve DESC
            """)
    List<ObjetTrouve> findWithFilters(@Param("statut") StatutObjet statut,
                                      @Param("categorie") CategorieObjet categorie,
                                      @Param("texte") String texte);

    boolean existsByReference(String reference);
}
