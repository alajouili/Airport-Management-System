package com.airport.passengers.controller;

import com.airport.passengers.dto.AssistanceRequest;
import com.airport.passengers.dto.AssistanceResponse;
import com.airport.passengers.dto.AssistanceStatutRequest;
import com.airport.passengers.entity.StatutAssistance;
import com.airport.passengers.service.AssistanceService;
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
 *   GET  /api/assistances?statut=&volId=   list
 *   POST /api/assistances                  create -> 201
 *   PUT  /api/assistances/{id}/statut      change status
 */
@RestController
@RequestMapping("/api/assistances")
public class AssistanceController {

    private final AssistanceService assistanceService;

    public AssistanceController(AssistanceService assistanceService) {
        this.assistanceService = assistanceService;
    }

    @GetMapping
    public List<AssistanceResponse> findAll(@RequestParam(required = false) StatutAssistance statut,
                                            @RequestParam(required = false) Long volId) {
        return assistanceService.findAll(statut, volId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssistanceResponse create(@Valid @RequestBody AssistanceRequest request) {
        return assistanceService.create(request);
    }

    @PutMapping("/{id}/statut")
    public AssistanceResponse changerStatut(@PathVariable Long id, @Valid @RequestBody AssistanceStatutRequest request) {
        return assistanceService.changerStatut(id, request.statut());
    }
}
