package com.airport.passengers.controller;

import com.airport.passengers.dto.PointControleRequest;
import com.airport.passengers.dto.PointControleResponse;
import com.airport.passengers.service.PointControleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *   GET    /api/points-controle        list
 *   POST   /api/points-controle        create -> 201
 *   PUT    /api/points-controle/{id}   update (also activate / deactivate)
 *   DELETE /api/points-controle/{id}   delete (refused if already used) -> 204
 */
@RestController
@RequestMapping("/api/points-controle")
public class PointControleController {

    private final PointControleService pointControleService;

    public PointControleController(PointControleService pointControleService) {
        this.pointControleService = pointControleService;
    }

    @GetMapping
    public List<PointControleResponse> findAll() {
        return pointControleService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PointControleResponse create(@Valid @RequestBody PointControleRequest request) {
        return pointControleService.create(request);
    }

    @PutMapping("/{id}")
    public PointControleResponse update(@PathVariable Long id, @Valid @RequestBody PointControleRequest request) {
        return pointControleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pointControleService.delete(id);
    }
}
