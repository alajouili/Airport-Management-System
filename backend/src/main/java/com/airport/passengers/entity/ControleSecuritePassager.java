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

/** One pass of a passenger through security (table "controle_securite_passager"). */
@Entity
@Table(name = "controle_securite_passager")
public class ControleSecuritePassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "billet_id", nullable = false)
    private Billet billet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_controle_id", nullable = false)
    private PointControleSecurite pointControle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private ResultatControle resultat;

    @Column(length = 255)
    private String commentaire;

    @Column(name = "date_controle", nullable = false, updatable = false)
    private LocalDateTime dateControle;

    public ControleSecuritePassager() {
        // Required by JPA
    }

    public ControleSecuritePassager(Billet billet, PointControleSecurite pointControle,
                                    ResultatControle resultat, String commentaire) {
        this.billet = billet;
        this.pointControle = pointControle;
        this.resultat = resultat;
        this.commentaire = commentaire;
    }

    @PrePersist
    void onCreate() {
        if (dateControle == null) {
            dateControle = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Billet getBillet() { return billet; }

    public PointControleSecurite getPointControle() { return pointControle; }

    public ResultatControle getResultat() { return resultat; }

    public String getCommentaire() { return commentaire; }

    public LocalDateTime getDateControle() { return dateControle; }
}
