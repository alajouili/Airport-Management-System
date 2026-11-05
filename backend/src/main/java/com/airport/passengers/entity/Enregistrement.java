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

/** Check-in of a ticket (table "enregistrement"). A ticket is checked in at most once. */
@Entity
@Table(name = "enregistrement")
public class Enregistrement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billet_id", nullable = false, unique = true)
    private Billet billet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalEnregistrement canal;

    @Column(name = "date_enregistrement", nullable = false, updatable = false)
    private LocalDateTime dateEnregistrement;

    public Enregistrement() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateEnregistrement == null) {
            dateEnregistrement = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Billet getBillet() { return billet; }
    public void setBillet(Billet billet) { this.billet = billet; }

    public CanalEnregistrement getCanal() { return canal; }
    public void setCanal(CanalEnregistrement canal) { this.canal = canal; }

    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; }
}
