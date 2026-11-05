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
 * One status change of a bag (table "historique_bagage").
 * Rows are only ever added, never modified: it is an audit trail.
 */
@Entity
@Table(name = "historique_bagage")
public class HistoriqueBagage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bagage_id", nullable = false)
    private Bagage bagage;

    /** null for the first row (bag just created). */
    @Enumerated(EnumType.STRING)
    @Column(name = "ancien_statut", length = 20)
    private StatutBagage ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut", nullable = false, length = 20)
    private StatutBagage nouveauStatut;

    @Column(length = 255)
    private String commentaire;

    @Column(name = "date_changement", nullable = false, updatable = false)
    private LocalDateTime dateChangement;

    public HistoriqueBagage() {
        // Required by JPA
    }

    public HistoriqueBagage(Bagage bagage, StatutBagage ancienStatut, StatutBagage nouveauStatut, String commentaire) {
        this.bagage = bagage;
        this.ancienStatut = ancienStatut;
        this.nouveauStatut = nouveauStatut;
        this.commentaire = commentaire;
    }

    @PrePersist
    void onCreate() {
        if (dateChangement == null) {
            dateChangement = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Bagage getBagage() { return bagage; }

    public StatutBagage getAncienStatut() { return ancienStatut; }

    public StatutBagage getNouveauStatut() { return nouveauStatut; }

    public String getCommentaire() { return commentaire; }

    public LocalDateTime getDateChangement() { return dateChangement; }
}
