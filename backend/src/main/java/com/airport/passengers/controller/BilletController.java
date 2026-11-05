package com.airport.passengers.controller;

import com.airport.passengers.dto.AffectationSiegeRequest;
import com.airport.passengers.dto.BilletResponse;
import com.airport.passengers.dto.EnregistrementRequest;
import com.airport.passengers.service.BilletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *   GET  /api/billets/{id}                  one ticket
 *   PUT  /api/billets/{id}/siege            choose / change the seat
 *   POST /api/billets/{id}/enregistrement   check in -> 201
 */
@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private final BilletService billetService;

    public BilletController(BilletService billetService) {
        this.billetService = billetService;
    }

    @GetMapping("/{id}")
    public BilletResponse findById(@PathVariable Long id) {
        return billetService.findById(id);
    }

    @PutMapping("/{id}/siege")
    public BilletResponse choisirSiege(@PathVariable Long id, @Valid @RequestBody AffectationSiegeRequest request) {
        return billetService.choisirSiege(id, request);
    }

    @PostMapping("/{id}/enregistrement")
    @ResponseStatus(HttpStatus.CREATED)
    public BilletResponse enregistrer(@PathVariable Long id, @Valid @RequestBody EnregistrementRequest request) {
        return billetService.enregistrer(id, request);
    }
}
