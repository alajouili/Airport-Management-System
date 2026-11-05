package com.airport.passengers.controller;

import com.airport.passengers.dto.PassagerRequest;
import com.airport.passengers.dto.PassagerResponse;
import com.airport.passengers.service.PassagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * REST API for passengers. A controller only translates HTTP into Java calls:
 * no business rule here, they are all in PassagerService.
 *
 *   GET    /api/passagers?recherche=ben   list / search
 *   GET    /api/passagers/{id}            one passenger
 *   POST   /api/passagers                 create  -> 201 Created
 *   PUT    /api/passagers/{id}            update
 *   DELETE /api/passagers/{id}            delete  -> 204 No Content
 */
@RestController
@RequestMapping("/api/passagers")
public class PassagerController {

    private final PassagerService passagerService;

    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @GetMapping
    public List<PassagerResponse> findAll(@RequestParam(required = false) String recherche) {
        return passagerService.findAll(recherche);
    }

    @GetMapping("/{id}")
    public PassagerResponse findById(@PathVariable Long id) {
        return passagerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassagerResponse create(@Valid @RequestBody PassagerRequest request) {
        return passagerService.create(request);
    }

    @PutMapping("/{id}")
    public PassagerResponse update(@PathVariable Long id, @Valid @RequestBody PassagerRequest request) {
        return passagerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        passagerService.delete(id);
    }
}
