package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.passengers.dto.ReclamationRequest;
import com.airport.passengers.dto.ReclamationStatutRequest;
import com.airport.passengers.entity.CategorieReclamation;
import com.airport.passengers.entity.Passager;
import com.airport.passengers.entity.Reclamation;
import com.airport.passengers.entity.StatutReclamation;
import com.airport.passengers.repository.BagageRepository;
import com.airport.passengers.repository.ReclamationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReclamationServiceTest {

    @Mock private ReclamationRepository reclamationRepository;
    @Mock private BagageRepository bagageRepository;
    @Mock private PassagerService passagerService;
    @Mock private ReservationService reservationService;

    private ReclamationService service;

    @BeforeEach
    void setUp() {
        service = new ReclamationService(reclamationRepository, bagageRepository, passagerService,
                reservationService, Clock.systemDefaultZone());
    }

    @Test
    void baggageComplaintWithoutBagIsRefused() {
        when(passagerService.findEntity(1L)).thenReturn(new Passager());

        assertThatThrownBy(() -> service.create(new ReclamationRequest(
                1L, null, null, CategorieReclamation.BAGAGE, "Valise abîmée", "La poignée est cassée")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("bagage");
    }

    @Test
    void closingAComplaintRequiresAnAnswer() {
        Reclamation reclamation = reclamation(StatutReclamation.EN_COURS);

        assertThatThrownBy(() -> service.changerStatut(7L, new ReclamationStatutRequest(StatutReclamation.RESOLUE, " ")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("réponse");
        assertThat(reclamation.getStatut()).isEqualTo(StatutReclamation.EN_COURS);
    }

    @Test
    void resolvingStoresTheAnswerAndTheClosingDate() {
        Reclamation reclamation = reclamation(StatutReclamation.EN_COURS);
        reclamation.setPassager(new Passager());

        service.changerStatut(7L, new ReclamationStatutRequest(StatutReclamation.RESOLUE, "Remboursement de 50 TND"));

        assertThat(reclamation.getStatut()).isEqualTo(StatutReclamation.RESOLUE);
        assertThat(reclamation.getReponse()).isEqualTo("Remboursement de 50 TND");
        assertThat(reclamation.getDateCloture()).isNotNull();
    }

    private Reclamation reclamation(StatutReclamation statut) {
        Reclamation reclamation = new Reclamation();
        reclamation.setStatut(statut);
        when(reclamationRepository.findDetailById(7L)).thenReturn(Optional.of(reclamation));
        return reclamation;
    }
}
