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

/** A request to get a found object back (table "demande_restitution_objet"). */
@Entity
@Table(name = "demande_restitution_objet")
public class DemandeRestitutionObjet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "objet_trouve_id", nullable = false)
    private ObjetTrouve objetTrouve;

    /** Optional: the claimant may not be a registered passenger. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passager_id")
    private Passager passager;

    @Column(name = "nom_demandeur", nullable = false, length = 160)
    private String nomDemandeur;

    @Column(nullable = false, length = 150)
    private String contact;

    /** What the claimant says to prove the object is theirs. */
    @Column(name = "description_preuve", nullable = false, length = 500)
    private String descriptionPreuve;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutRestitution statut = StatutRestitution.EN_ATTENTE;

    @Column(name = "commentaire_decision", length = 255)
    private String commentaireDecision;

    @Column(name = "date_demande", nullable = false, updatable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_decision")
    private LocalDateTime dateDecision;

    public DemandeRestitutionObjet() {
        // Required by JPA
    }

    @PrePersist
    void onCreate() {
        if (dateDemande == null) {
            dateDemande = LocalDateTime.now();
        }
    }

    /** Records a decision (accepted or refused) in one call. */
    public void decider(StatutRestitution decision, String commentaire, LocalDateTime date) {
        this.statut = decision;
        this.commentaireDecision = commentaire;
        this.dateDecision = date;
    }

    public Long getId() { return id; }

    public ObjetTrouve getObjetTrouve() { return objetTrouve; }
    public void setObjetTrouve(ObjetTrouve objetTrouve) { this.objetTrouve = objetTrouve; }

    public Passager getPassager() { return passager; }
    public void setPassager(Passager passager) { this.passager = passager; }

    public String getNomDemandeur() { return nomDemandeur; }
    public void setNomDemandeur(String nomDemandeur) { this.nomDemandeur = nomDemandeur; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getDescriptionPreuve() { return descriptionPreuve; }
    public void setDescriptionPreuve(String descriptionPreuve) { this.descriptionPreuve = descriptionPreuve; }

    public StatutRestitution getStatut() { return statut; }

    public String getCommentaireDecision() { return commentaireDecision; }

    public LocalDateTime getDateDemande() { return dateDemande; }

    public LocalDateTime getDateDecision() { return dateDecision; }
}
