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
import jakarta.persistence.Table;

/**
 * A seat of a flight (table "siege_avion"), for example "12A".
 * Seats are generated automatically from the flight capacity (see SeatLayout).
 */
@Entity
@Table(name = "siege_avion")
public class SiegeAvion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vol_id", nullable = false)
    private Vol vol;

    @Column(nullable = false, length = 4)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ClasseVoyage classe;

    public SiegeAvion() {
        // Required by JPA
    }

    public SiegeAvion(Vol vol, String numero, ClasseVoyage classe) {
        this.vol = vol;
        this.numero = numero;
        this.classe = classe;
    }

    public Long getId() { return id; }

    public Vol getVol() { return vol; }

    public String getNumero() { return numero; }

    public ClasseVoyage getClasse() { return classe; }
}
