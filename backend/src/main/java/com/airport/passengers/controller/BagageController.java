package com.airport.passengers.controller;

import com.airport.passengers.dto.BagageDetailResponse;
import com.airport.passengers.dto.BagageRequest;
import com.airport.passengers.dto.BagageResponse;
import com.airport.passengers.dto.EvenementBagageRequest;
import com.airport.passengers.entity.StatutBagage;
import com.airport.passengers.service.BagageService;
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
 *   GET  /api/bagages?statut=&etiquette=                 list / search by tag
 *   GET  /api/bagages/{id}                               detail with history and events
 *   POST /api/enregistrements/{id}/bagages               add a bag to a check-in -> 201
 *   POST /api/bagages/{id}/evenements                    record a handling event -> 201
 */
@RestController
public class BagageController {

    private final BagageService bagageService;

    public BagageController(BagageService bagageService) {
        this.bagageService = bagageService;
    }

    @GetMapping("/api/bagages")
    public List<BagageResponse> findAll(@RequestParam(required = false) StatutBagage statut,
                                        @RequestParam(required = false) String etiquette) {
        return bagageService.findAll(statut, etiquette);
    }

    @GetMapping("/api/bagages/{id}")
    public BagageDetailResponse findDetail(@PathVariable Long id) {
        return bagageService.findDetail(id);
    }

    @PostMapping("/api/enregistrements/{enregistrementId}/bagages")
    @ResponseStatus(HttpStatus.CREATED)
    public BagageResponse ajouter(@PathVariable Long enregistrementId, @Valid @RequestBody BagageRequest request) {
        return bagageService.ajouter(enregistrementId, request);
    }

    @PostMapping("/api/bagages/{id}/evenements")
    @ResponseStatus(HttpStatus.CREATED)
    public BagageDetailResponse ajouterEvenement(@PathVariable Long id,
                                                 @Valid @RequestBody EvenementBagageRequest request) {
        return bagageService.ajouterEvenement(id, request);
    }
}
