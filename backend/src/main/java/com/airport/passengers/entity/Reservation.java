package com.airport.passengers.entity;

import com.airport.operations.entity.Vol;
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

/** A booking: one passenger, one flight, one travel class (table "reservation"). */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Short code given to the customer, for example "K7QZ2M". */
    @Column(nullable = false, unique = true, length = 6)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passager_id", nullable = false)
    private Passager passager;

    /** Student A's flight: referenced by its id, never copied. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClasseVoyage classe;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private StatutReservation statut = StatutReservation.EN_ATTENTE_PAIEMENT;

    @Column(name = "date_reservation", nullable = false, updatable = false)
    private LocalDateTime dateReservation;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    public Reservation() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateReservation == null) {
            dateReservation = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Passager getPassager() { return passager; }
    public void setPassager(Passager passager) { this.passager = passager; }

    public Vol getVol() { return vol; }
    public void setVol(Vol vol) { this.vol = vol; }

    public ClasseVoyage getClasse() { return classe; }
    public void setClasse(ClasseVoyage classe) { this.classe = classe; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public LocalDateTime getDateReservation() { return dateReservation; }

    public LocalDateTime getDateAnnulation() { return dateAnnulation; }
    public void setDateAnnulation(LocalDateTime dateAnnulation) { this.dateAnnulation = dateAnnulation; }
}
