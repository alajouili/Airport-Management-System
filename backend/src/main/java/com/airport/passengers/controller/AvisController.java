package com.airport.passengers.controller;

import com.airport.passengers.dto.AvisRequest;
import com.airport.passengers.dto.AvisResponse;
import com.airport.passengers.service.AvisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET  /api/avis?note=   list (optional filter on the number of stars)
 *   POST /api/avis         give a review -> 201
 */
@RestController
@RequestMapping("/api/avis")
public class AvisController {

    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @GetMapping
    public List<AvisResponse> findAll(@RequestParam(required = false) Integer note) {
        return avisService.findAll(note);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvisResponse create(@Valid @RequestBody AvisRequest request) {
        return avisService.create(request);
    }
}
