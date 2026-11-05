package com.airport.passengers.controller;

import com.airport.passengers.dto.EspacePassagerRequest;
import com.airport.passengers.dto.EspacePassagerResponse;
import com.airport.passengers.service.EspacePassagerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *   POST /api/espace-passager/recherche   {reference, email} -> the passenger's trip
 *
 * POST (not GET) so the email travels in the request body, not in the URL.
 */
@RestController
public class EspacePassagerController {

    private final EspacePassagerService espacePassagerService;

    public EspacePassagerController(EspacePassagerService espacePassagerService) {
        this.espacePassagerService = espacePassagerService;
    }

    @PostMapping("/api/espace-passager/recherche")
    public EspacePassagerResponse rechercher(@Valid @RequestBody EspacePassagerRequest request) {
        return espacePassagerService.rechercher(request);
    }
}
