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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** A bag registered during check-in (table "bagage"). */
@Entity
@Table(name = "bagage")
public class Bagage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enregistrement_id", nullable = false)
    private Enregistrement enregistrement;

    /** Tag printed and attached to the bag, for example "TU482913". */
    @Column(name = "numero_etiquette", nullable = false, unique = true, length = 12)
    private String numeroEtiquette;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TypeBagage type;

    @Column(name = "poids_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal poidsKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutBagage statut = StatutBagage.ENREGISTRE;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public Bagage() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Enregistrement getEnregistrement() { return enregistrement; }
    public void setEnregistrement(Enregistrement enregistrement) { this.enregistrement = enregistrement; }

    public String getNumeroEtiquette() { return numeroEtiquette; }
    public void setNumeroEtiquette(String numeroEtiquette) { this.numeroEtiquette = numeroEtiquette; }

    public TypeBagage getType() { return type; }
    public void setType(TypeBagage type) { this.type = type; }

    public BigDecimal getPoidsKg() { return poidsKg; }
    public void setPoidsKg(BigDecimal poidsKg) { this.poidsKg = poidsKg; }

    public StatutBagage getStatut() { return statut; }
    public void setStatut(StatutBagage statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
}
