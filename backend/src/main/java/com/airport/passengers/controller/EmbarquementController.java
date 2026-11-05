package com.airport.passengers.controller;

import com.airport.passengers.dto.ControleSecuriteRequest;
import com.airport.passengers.dto.ControleSecuriteResponse;
import com.airport.passengers.dto.EmbarquementRequest;
import com.airport.passengers.dto.EvenementEmbarquementResponse;
import com.airport.passengers.dto.PassagerEmbarquementResponse;
import com.airport.passengers.service.EmbarquementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET  /api/vols/{volId}/embarquement        gate screen: checked-in passengers and their state
 *   POST /api/billets/{id}/controles-securite  record a security check -> 201
 *   POST /api/billets/{id}/embarquement        record a gate event (call, board, refuse, no-show) -> 201
 */
@RestController
public class EmbarquementController {

    private final EmbarquementService embarquementService;

    public EmbarquementController(EmbarquementService embarquementService) {
        this.embarquementService = embarquementService;
    }

    @GetMapping("/api/vols/{volId}/embarquement")
    public List<PassagerEmbarquementResponse> getListe(@PathVariable Long volId) {
        return embarquementService.getListe(volId);
    }

    @PostMapping("/api/billets/{billetId}/controles-securite")
    @ResponseStatus(HttpStatus.CREATED)
    public ControleSecuriteResponse enregistrerControle(@PathVariable Long billetId,
                                                        @Valid @RequestBody ControleSecuriteRequest request) {
        return embarquementService.enregistrerControle(billetId, request);
    }

    @PostMapping("/api/billets/{billetId}/embarquement")
    @ResponseStatus(HttpStatus.CREATED)
    public EvenementEmbarquementResponse enregistrerEvenement(@PathVariable Long billetId,
                                                              @Valid @RequestBody EmbarquementRequest request) {
        return embarquementService.enregistrerEvenement(billetId, request);
    }
}
