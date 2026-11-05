package com.airport.operations.entity;

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
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * OWNED BY STUDENT A - temporary minimal version.
 *
 * Student B never creates or changes flights. Student B's tables
 * (reservation, billet, expedition_fret...) only reference a flight by its id,
 * as the project specification asks (section 4: "defined only once").
 */
@Entity
@Table(name = "vol")
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_vol", nullable = false, length = 10)
    private String numeroVol;

    /** Many flights belong to one airline. LAZY = loaded only when needed. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compagnie_id", nullable = false)
    private CompagnieAerienne compagnie;

    @Column(name = "aeroport_depart", nullable = false, length = 60)
    private String aeroportDepart;

    @Column(name = "aeroport_arrivee", nullable = false, length = 60)
    private String aeroportArrivee;

    @Column(name = "date_heure_depart", nullable = false)
    private LocalDateTime dateHeureDepart;

    @Column(name = "date_heure_arrivee", nullable = false)
    private LocalDateTime dateHeureArrivee;

    @Column(nullable = false)
    private Integer capacite;

    /** Stored as text ("PROGRAMME"...) instead of a number, so the table stays readable. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVol statut = StatutVol.PROGRAMME;

    protected Vol() {
        // Required by JPA
    }

    public Long getId() { return id; }

    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }

    public CompagnieAerienne getCompagnie() { return compagnie; }
    public void setCompagnie(CompagnieAerienne compagnie) { this.compagnie = compagnie; }

    public String getAeroportDepart() { return aeroportDepart; }
    public void setAeroportDepart(String aeroportDepart) { this.aeroportDepart = aeroportDepart; }

    public String getAeroportArrivee() { return aeroportArrivee; }
    public void setAeroportArrivee(String aeroportArrivee) { this.aeroportArrivee = aeroportArrivee; }

    public LocalDateTime getDateHeureDepart() { return dateHeureDepart; }
    public void setDateHeureDepart(LocalDateTime dateHeureDepart) { this.dateHeureDepart = dateHeureDepart; }

    public LocalDateTime getDateHeureArrivee() { return dateHeureArrivee; }
    public void setDateHeureArrivee(LocalDateTime dateHeureArrivee) { this.dateHeureArrivee = dateHeureArrivee; }

    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    public StatutVol getStatut() { return statut; }
    public void setStatut(StatutVol statut) { this.statut = statut; }
}
