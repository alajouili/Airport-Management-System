package com.airport.passengers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** The travel document issued after an accepted payment (table "billet"). */
@Entity
@Table(name = "billet")
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_billet", nullable = false, unique = true, length = 13)
    private String numeroBillet;

    /** One ticket per reservation: reservation_id is UNIQUE in the table. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBillet statut = StatutBillet.EMIS;

    @Column(name = "date_emission", nullable = false, updatable = false)
    private LocalDateTime dateEmission;

    public Billet() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateEmission == null) {
            dateEmission = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getNumeroBillet() { return numeroBillet; }
    public void setNumeroBillet(String numeroBillet) { this.numeroBillet = numeroBillet; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public StatutBillet getStatut() { return statut; }
    public void setStatut(StatutBillet statut) { this.statut = statut; }

    public LocalDateTime getDateEmission() { return dateEmission; }
}
