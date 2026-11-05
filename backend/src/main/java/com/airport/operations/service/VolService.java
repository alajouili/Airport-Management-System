package com.airport.operations.service;

import com.airport.common.exception.ResourceNotFoundException;
import com.airport.operations.dto.VolResponse;
import com.airport.operations.entity.Vol;
import com.airport.operations.repository.VolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OWNED BY STUDENT A - temporary, read-only version.
 *
 * Other modules (reservations, cargo...) call findEntity() to get a flight
 * and check it exists, instead of accessing VolRepository directly.
 */
@Service
@Transactional(readOnly = true)
public class VolService {

    private final VolRepository volRepository;

    public VolService(VolRepository volRepository) {
        this.volRepository = volRepository;
    }

    /** Upcoming flights (from today 00:00), used by the passenger booking screens. */
    public List<VolResponse> findUpcoming() {
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        return volRepository.findByDateHeureDepartGreaterThanEqualOrderByDateHeureDepartAsc(startOfToday)
                .stream()
                .map(VolResponse::from)
                .toList();
    }

    public VolResponse findById(Long id) {
        return VolResponse.from(findEntity(id));
    }

    /** Returns the entity or throws a 404. Used by Student B's services. */
    public Vol findEntity(Long id) {
        return volRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vol", id));
    }
}
