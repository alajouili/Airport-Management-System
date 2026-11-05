package com.airport.passengers.service;

import com.airport.operations.entity.Vol;
import com.airport.operations.service.VolService;
import com.airport.passengers.dto.SiegeResponse;
import com.airport.passengers.entity.SiegeAvion;
import com.airport.passengers.repository.AffectationSiegeBilletRepository;
import com.airport.passengers.repository.SiegeAvionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Seat map of a flight. Seats are created the first time someone asks for them. */
@Service
@Transactional
public class SiegeService {

    private final SiegeAvionRepository siegeRepository;
    private final AffectationSiegeBilletRepository affectationRepository;
    private final VolService volService;

    public SiegeService(SiegeAvionRepository siegeRepository,
                        AffectationSiegeBilletRepository affectationRepository,
                        VolService volService) {
        this.siegeRepository = siegeRepository;
        this.affectationRepository = affectationRepository;
        this.volService = volService;
    }

    public List<SiegeResponse> getPlanSieges(Long volId) {
        Vol vol = volService.findEntity(volId);
        List<SiegeAvion> sieges = ensureSieges(vol);
        Set<Long> occupes = new HashSet<>(affectationRepository.findOccupiedSeatIds(volId));
        return sieges.stream()
                .map(s -> new SiegeResponse(s.getId(), s.getNumero(), s.getClasse(), occupes.contains(s.getId())))
                .toList();
    }

    /** Returns the seats of the flight, generating them from its capacity if needed. */
    public List<SiegeAvion> ensureSieges(Vol vol) {
        List<SiegeAvion> sieges = siegeRepository.findByVolIdOrderByIdAsc(vol.getId());
        if (!sieges.isEmpty()) {
            return sieges;
        }
        List<SiegeAvion> nouveaux = SeatLayout.generer(vol.getCapacite()).stream()
                .map(position -> new SiegeAvion(vol, position.numero(), position.classe()))
                .toList();
        return siegeRepository.saveAll(nouveaux);
    }
}
