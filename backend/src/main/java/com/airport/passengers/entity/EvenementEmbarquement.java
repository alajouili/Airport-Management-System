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

/** An event at the boarding gate (table "evenement_embarquement"). */
@Entity
@Table(name = "evenement_embarquement")
public class EvenementEmbarquement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billet_id", nullable = false)
    private Billet billet;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_evenement", nullable = false, length = 20)
    private TypeEvenementEmbarquement typeEvenement;

    /** Gate number, for example "B12" (Student A's porte_embarquement table can replace it). */
    @Column(nullable = false, length = 10)
    private String porte;

    @Column(length = 255)
    private String commentaire;

    @Column(name = "date_evenement", nullable = false, updatable = false)
    private LocalDateTime dateEvenement;

    public EvenementEmbarquement() {
        // Required by JPA
    }

    public EvenementEmbarquement(Billet billet, TypeEvenementEmbarquement typeEvenement,
                                 String porte, String commentaire) {
        this.billet = billet;
        this.typeEvenement = typeEvenement;
        this.porte = porte;
        this.commentaire = commentaire;
    }

    @PrePersist
    void onCreate() {
        if (dateEvenement == null) {
            dateEvenement = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Billet getBillet() { return billet; }

    public TypeEvenementEmbarquement getTypeEvenement() { return typeEvenement; }

    public String getPorte() { return porte; }

    public String getCommentaire() { return commentaire; }

    public LocalDateTime getDateEvenement() { return dateEvenement; }
}
