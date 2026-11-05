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
 * A physical handling event of a bag (table "evenement_traitement_bagage"):
 * where and when the bag was scanned.
 *
 * Difference with HistoriqueBagage: an event is what HAPPENED to the bag
 * (a scan at a place); the history is how its STATUS changed as a result.
 */
@Entity
@Table(name = "evenement_traitement_bagage")
public class EvenementTraitementBagage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bagage_id", nullable = false)
    private Bagage bagage;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement", nullable = false, length = 25)
    private TypeEvenementBagage typeEvenement;

    @Column(nullable = false, length = 100)
    private String lieu;

    @Column(length = 255)
    private String commentaire;

    @Column(name = "date_evenement", nullable = false, updatable = false)
    private LocalDateTime dateEvenement;

    public EvenementTraitementBagage() {
        // Required by JPA
    }

    public EvenementTraitementBagage(Bagage bagage, TypeEvenementBagage typeEvenement, String lieu, String commentaire) {
        this.bagage = bagage;
        this.typeEvenement = typeEvenement;
        this.lieu = lieu;
        this.commentaire = commentaire;
    }

    @PrePersist
    void onCreate() {
        if (dateEvenement == null) {
            dateEvenement = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Bagage getBagage() { return bagage; }

    public TypeEvenementBagage getTypeEvenement() { return typeEvenement; }

    public String getLieu() { return lieu; }

    public String getCommentaire() { return commentaire; }

    public LocalDateTime getDateEvenement() { return dateEvenement; }
}
