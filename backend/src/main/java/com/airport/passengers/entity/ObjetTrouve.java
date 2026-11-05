package com.airport.passengers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** An object found in the airport (table "objet_trouve"). */
@Entity
@Table(name = "objet_trouve")
public class ObjetTrouve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String reference;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategorieObjet categorie;

    @Column(name = "lieu_trouve", nullable = false, length = 100)
    private String lieuTrouve;

    @Column(name = "date_trouve", nullable = false)
    private LocalDateTime dateTrouve;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutObjet statut = StatutObjet.EN_STOCK;

    @Column(name = "date_enregistrement", nullable = false, updatable = false)
    private LocalDateTime dateEnregistrement;

    /** When the object left the lost-and-found office (given back or eliminated). */
    @Column(name = "date_sortie")
    private LocalDateTime dateSortie;

    public ObjetTrouve() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateEnregistrement == null) {
            dateEnregistrement = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CategorieObjet getCategorie() { return categorie; }
    public void setCategorie(CategorieObjet categorie) { this.categorie = categorie; }

    public String getLieuTrouve() { return lieuTrouve; }
    public void setLieuTrouve(String lieuTrouve) { this.lieuTrouve = lieuTrouve; }

    public LocalDateTime getDateTrouve() { return dateTrouve; }
    public void setDateTrouve(LocalDateTime dateTrouve) { this.dateTrouve = dateTrouve; }

    public StatutObjet getStatut() { return statut; }
    public void setStatut(StatutObjet statut) { this.statut = statut; }

    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; }

    public LocalDateTime getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDateTime dateSortie) { this.dateSortie = dateSortie; }
}
