package com.airport.passengers.controller;

import com.airport.passengers.dto.SiegeResponse;
import com.airport.passengers.service.SiegeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET /api/vols/{volId}/sieges   seat map of a flight
 *
 * The URL starts with /api/vols because seats belong to a flight,
 * but this endpoint is Student B's (it does not change Student A's VolController).
 */
@RestController
public class SiegeController {

    private final SiegeService siegeService;

    public SiegeController(SiegeService siegeService) {
        this.siegeService = siegeService;
    }

    @GetMapping("/api/vols/{volId}/sieges")
    public List<SiegeResponse> getPlanSieges(@PathVariable Long volId) {
        return siegeService.getPlanSieges(volId);
    }
}
