package com.airport.passengers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Links a ticket to a seat (table "affectation_siege_billet").
 * Both columns are UNIQUE in SQL: one seat per ticket, one ticket per seat.
 */
@Entity
@Table(name = "affectation_siege_billet")
public class AffectationSiegeBillet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billet_id", nullable = false, unique = true)
    private Billet billet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "siege_id", nullable = false, unique = true)
    private SiegeAvion siege;

    @Column(name = "date_affectation", nullable = false)
    private LocalDateTime dateAffectation;

    public AffectationSiegeBillet() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateAffectation == null) {
            dateAffectation = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Billet getBillet() { return billet; }
    public void setBillet(Billet billet) { this.billet = billet; }

    public SiegeAvion getSiege() { return siege; }

    /** Changing the seat also refreshes the assignment date. */
    public void setSiege(SiegeAvion siege) {
        this.siege = siege;
        this.dateAffectation = LocalDateTime.now();
    }

    public LocalDateTime getDateAffectation() { return dateAffectation; }
}
