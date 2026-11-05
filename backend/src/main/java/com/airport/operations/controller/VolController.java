package com.airport.operations.controller;

import com.airport.operations.dto.VolResponse;
import com.airport.operations.service.VolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * OWNED BY STUDENT A - temporary, read-only version.
 *
 * This is the "flights API contract" the specification asks both students
 * to agree on (section 5). Student A will add POST/PUT/DELETE later.
 *
 *   GET /api/vols        -> upcoming flights
 *   GET /api/vols/{id}   -> one flight
 */
@RestController
@RequestMapping("/api/vols")
public class VolController {

    private final VolService volService;

    public VolController(VolService volService) {
        this.volService = volService;
    }

    @GetMapping
    public List<VolResponse> findUpcoming() {
        return volService.findUpcoming();
    }

    @GetMapping("/{id}")
    public VolResponse findById(@PathVariable Long id) {
        return volService.findById(id);
    }
}
