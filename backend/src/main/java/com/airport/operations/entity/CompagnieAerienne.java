package com.airport.operations.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * OWNED BY STUDENT A - temporary minimal version.
 * Maps the table "compagnie_aerienne".
 */
@Entity
@Table(name = "compagnie_aerienne")
public class CompagnieAerienne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Two or three letter airline code, for example "TU" for Tunisair. */
    @Column(name = "code_iata", nullable = false, unique = true, length = 3)
    private String codeIata;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 60)
    private String pays;

    protected CompagnieAerienne() {
        // Required by JPA
    }

    public Long getId() { return id; }

    public String getCodeIata() { return codeIata; }
    public void setCodeIata(String codeIata) { this.codeIata = codeIata; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}
