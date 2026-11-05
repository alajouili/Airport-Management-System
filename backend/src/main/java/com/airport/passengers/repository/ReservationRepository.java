package com.airport.passengers.repository;

import com.airport.passengers.entity.ClasseVoyage;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * List with optional filters: a null parameter means "no filter".
     * JOIN FETCH loads passenger, flight and airline in the same SQL query,
     * instead of one extra query per reservation (the "N+1 problem").
     */
    @Query("""
            SELECT r FROM Reservation r
            JOIN FETCH r.passager p
            JOIN FETCH r.vol v
            JOIN FETCH v.compagnie
            WHERE (:statut IS NULL OR r.statut = :statut)
              AND (:volId IS NULL OR v.id = :volId)
              AND (:passagerId IS NULL OR p.id = :passagerId)
            ORDER BY r.dateReservation DESC
            """)
    List<Reservation> findWithFilters(@Param("statut") StatutReservation statut,
                                      @Param("volId") Long volId,
                                      @Param("passagerId") Long passagerId);

    @Query("""
            SELECT r FROM Reservation r
            JOIN FETCH r.passager
            JOIN FETCH r.vol v
            JOIN FETCH v.compagnie
            WHERE r.id = :id
            """)
    Optional<Reservation> findDetailById(@Param("id") Long id);

    /** Number of seats already taken in a class (cancelled bookings do not count). */
    long countByVolIdAndClasseAndStatutNot(Long volId, ClasseVoyage classe, StatutReservation statut);

    boolean existsByPassagerIdAndVolIdAndStatutNot(Long passagerId, Long volId, StatutReservation statut);

    boolean existsByPassagerId(Long passagerId);

    boolean existsByReference(String reference);

    /** Passenger space: find a trip by its reference and the passenger's email. */
    @Query("""
            SELECT r FROM Reservation r
            JOIN FETCH r.passager p
            JOIN FETCH r.vol v
            JOIN FETCH v.compagnie
            WHERE UPPER(r.reference) = UPPER(:reference)
              AND LOWER(p.email) = LOWER(:email)
            """)
    Optional<Reservation> findByReferenceAndEmail(@Param("reference") String reference,
                                                  @Param("email") String email);
}
