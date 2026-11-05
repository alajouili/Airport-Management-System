package com.airport.passengers.controller;

import com.airport.passengers.dto.ReclamationRequest;
import com.airport.passengers.dto.ReclamationResponse;
import com.airport.passengers.dto.ReclamationStatutRequest;
import com.airport.passengers.entity.CategorieReclamation;
import com.airport.passengers.entity.StatutReclamation;
import com.airport.passengers.service.ReclamationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET  /api/reclamations?statut=&categorie=   list
 *   GET  /api/reclamations/{id}                 one complaint
 *   POST /api/reclamations                      create -> 201
 *   PUT  /api/reclamations/{id}/statut          process: {statut, reponse}
 */
@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {

    private final ReclamationService reclamationService;

    public ReclamationController(ReclamationService reclamationService) {
        this.reclamationService = reclamationService;
    }

    @GetMapping
    public List<ReclamationResponse> findAll(@RequestParam(required = false) StatutReclamation statut,
                                             @RequestParam(required = false) CategorieReclamation categorie) {
        return reclamationService.findAll(statut, categorie);
    }

    @GetMapping("/{id}")
    public ReclamationResponse findById(@PathVariable Long id) {
        return reclamationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReclamationResponse create(@Valid @RequestBody ReclamationRequest request) {
        return reclamationService.create(request);
    }

    @PutMapping("/{id}/statut")
    public ReclamationResponse changerStatut(@PathVariable Long id,
                                             @Valid @RequestBody ReclamationStatutRequest request) {
        return reclamationService.changerStatut(id, request);
    }
}
