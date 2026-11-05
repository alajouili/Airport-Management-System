package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.passengers.dto.PointControleRequest;
import com.airport.passengers.dto.PointControleResponse;
import com.airport.passengers.entity.PointControleSecurite;
import com.airport.passengers.repository.ControleSecuritePassagerRepository;
import com.airport.passengers.repository.PointControleSecuriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Security checkpoints. A checkpoint already used by controls cannot be deleted
 * (it would break their history): it can only be deactivated.
 */
@Service
@Transactional
public class PointControleService {

    private final PointControleSecuriteRepository pointRepository;
    private final ControleSecuritePassagerRepository controleRepository;

    public PointControleService(PointControleSecuriteRepository pointRepository,
                                ControleSecuritePassagerRepository controleRepository) {
        this.pointRepository = pointRepository;
        this.controleRepository = controleRepository;
    }

    @Transactional(readOnly = true)
    public List<PointControleResponse> findAll() {
        return pointRepository.findAllByOrderByTerminalAscCodeAsc().stream().map(PointControleResponse::from).toList();
    }

    public PointControleResponse create(PointControleRequest request) {
        String code = request.code().trim().toUpperCase();
        if (pointRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessRuleException("Un point de contrôle utilise déjà ce code");
        }
        PointControleSecurite point = new PointControleSecurite();
        apply(point, request, code);
        return PointControleResponse.from(pointRepository.save(point));
    }

    public PointControleResponse update(Long id, PointControleRequest request) {
        PointControleSecurite point = findEntity(id);
        String code = request.code().trim().toUpperCase();
        if (pointRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new BusinessRuleException("Un autre point de contrôle utilise déjà ce code");
        }
        apply(point, request, code);
        return PointControleResponse.from(point);
    }

    public void delete(Long id) {
        PointControleSecurite point = findEntity(id);
        if (controleRepository.existsByPointControleId(id)) {
            throw new BusinessRuleException("Ce point a déjà été utilisé : désactivez-le au lieu de le supprimer");
        }
        pointRepository.delete(point);
    }

    public PointControleSecurite findEntity(Long id) {
        return pointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Point de contrôle", id));
    }

    private void apply(PointControleSecurite point, PointControleRequest request, String code) {
        point.setCode(code);
        point.setNom(request.nom().trim());
        point.setTerminal(request.terminal().trim().toUpperCase());
        point.setType(request.type());
        point.setActif(request.actif());
    }
}
