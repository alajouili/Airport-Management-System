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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * An assistance request for a trip (table "demande_assistance_passager").
 * The passenger is reached through the reservation (no duplicated passager_id).
 */
@Entity
@Table(name = "demande_assistance_passager")
public class DemandeAssistancePassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_assistance", nullable = false, length = 30)
    private TypeAssistance typeAssistance;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAssistance statut = StatutAssistance.DEMANDEE;

    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    public DemandeAssistancePassager() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateDemande == null) {
            dateDemande = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public TypeAssistance getTypeAssistance() { return typeAssistance; }
    public void setTypeAssistance(TypeAssistance typeAssistance) { this.typeAssistance = typeAssistance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutAssistance getStatut() { return statut; }
    public void setStatut(StatutAssistance statut) { this.statut = statut; }

    public LocalDateTime getDateDemande() { return dateDemande; }

    public LocalDateTime getDateMiseAJour() { return dateMiseAJour; }
    public void setDateMiseAJour(LocalDateTime dateMiseAJour) { this.dateMiseAJour = dateMiseAJour; }
}
