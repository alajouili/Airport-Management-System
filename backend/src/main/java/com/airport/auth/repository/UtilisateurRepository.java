package com.airport.auth.repository;

import com.airport.auth.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * OWNED BY STUDENT A - temporary minimal version.
 *
 * Extending JpaRepository gives findAll, findById, save, deleteById...
 * without writing any SQL. Spring creates the implementation at startup.
 */
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /** Spring builds the query from the method name: WHERE email = ? */
    Optional<Utilisateur> findByEmail(String email);
}
