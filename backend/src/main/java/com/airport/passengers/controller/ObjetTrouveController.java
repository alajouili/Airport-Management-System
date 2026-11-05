package com.airport.passengers.controller;

import com.airport.passengers.dto.DecisionRestitutionRequest;
import com.airport.passengers.dto.ObjetTrouveDetailResponse;
import com.airport.passengers.dto.ObjetTrouveRequest;
import com.airport.passengers.dto.ObjetTrouveResponse;
import com.airport.passengers.dto.RestitutionRequest;
import com.airport.passengers.entity.CategorieObjet;
import com.airport.passengers.entity.StatutObjet;
import com.airport.passengers.service.ObjetTrouveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET  /api/objets-trouves?statut=&categorie=&recherche=   list
 *   GET  /api/objets-trouves/{id}                            detail with claims
 *   POST /api/objets-trouves                                 register a found object -> 201
 *   POST /api/objets-trouves/{id}/demandes                   claim an object -> 201
 *   POST /api/objets-trouves/{id}/elimination                eliminate (after 90 days)
 *   POST /api/demandes-restitution/{id}/decision             accept or refuse a claim
 */
@RestController
public class ObjetTrouveController {

    private final ObjetTrouveService objetTrouveService;

    public ObjetTrouveController(ObjetTrouveService objetTrouveService) {
        this.objetTrouveService = objetTrouveService;
    }

    @GetMapping("/api/objets-trouves")
    public List<ObjetTrouveResponse> findAll(@RequestParam(required = false) StatutObjet statut,
                                             @RequestParam(required = false) CategorieObjet categorie,
                                             @RequestParam(required = false) String recherche) {
        return objetTrouveService.findAll(statut, categorie, recherche);
    }

    @GetMapping("/api/objets-trouves/{id}")
    public ObjetTrouveDetailResponse findDetail(@PathVariable Long id) {
        return objetTrouveService.findDetail(id);
    }

    @PostMapping("/api/objets-trouves")
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetTrouveResponse create(@Valid @RequestBody ObjetTrouveRequest request) {
        return objetTrouveService.create(request);
    }

    @PostMapping("/api/objets-trouves/{id}/demandes")
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetTrouveDetailResponse ajouterDemande(@PathVariable Long id, @Valid @RequestBody RestitutionRequest request) {
        return objetTrouveService.ajouterDemande(id, request);
    }

    @PostMapping("/api/objets-trouves/{id}/elimination")
    public ObjetTrouveDetailResponse eliminer(@PathVariable Long id) {
        return objetTrouveService.eliminer(id);
    }

    @PostMapping("/api/demandes-restitution/{id}/decision")
    public ObjetTrouveDetailResponse decider(@PathVariable Long id, @Valid @RequestBody DecisionRestitutionRequest request) {
        return objetTrouveService.decider(id, request);
    }
}
