package com.airport.passengers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A security checkpoint (table "point_controle_securite"). */
@Entity
@Table(name = "point_controle_securite")
public class PointControleSecurite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 100)
    private String nom;

    /** Text for now; Student A's "terminal" table can replace it later. */
    @Column(nullable = false, length = 20)
    private String terminal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypePointControle type;

    @Column(nullable = false)
    private Boolean actif = true;

    public PointControleSecurite() {
        // Required by JPA
    }

    public Long getId() { return id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    public TypePointControle getType() { return type; }
    public void setType(TypePointControle type) { this.type = type; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}
