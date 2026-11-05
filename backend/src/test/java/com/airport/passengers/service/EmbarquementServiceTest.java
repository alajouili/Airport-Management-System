package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.operations.entity.StatutVol;
import com.airport.operations.entity.Vol;
import com.airport.operations.service.VolService;
import com.airport.passengers.dto.EmbarquementRequest;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.ControleSecuritePassager;
import com.airport.passengers.entity.Enregistrement;
import com.airport.passengers.entity.EvenementEmbarquement;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.ResultatControle;
import com.airport.passengers.entity.StatutBillet;
import com.airport.passengers.entity.TypeEvenementEmbarquement;
import com.airport.passengers.repository.AffectationSiegeBilletRepository;
import com.airport.passengers.repository.BilletRepository;
import com.airport.passengers.repository.ControleSecuritePassagerRepository;
import com.airport.passengers.repository.EnregistrementRepository;
import com.airport.passengers.repository.EvenementEmbarquementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmbarquementServiceTest {

    @Mock private BilletRepository billetRepository;
    @Mock private EnregistrementRepository enregistrementRepository;
    @Mock private AffectationSiegeBilletRepository affectationRepository;
    @Mock private ControleSecuritePassagerRepository controleRepository;
    @Mock private EvenementEmbarquementRepository evenementRepository;
    @Mock private PointControleService pointControleService;
    @Mock private BilletService billetService;
    @Mock private VolService volService;

    private EmbarquementService service;
    private Billet billet;

    @BeforeEach
    void setUp() {
        service = new EmbarquementService(billetRepository, enregistrementRepository, affectationRepository,
                controleRepository, evenementRepository, pointControleService, billetService, volService);

        Vol vol = mock(Vol.class);
        lenient().when(vol.getStatut()).thenReturn(StatutVol.EMBARQUEMENT);
        Reservation reservation = new Reservation();
        reservation.setVol(vol);

        billet = mock(Billet.class);
        lenient().when(billet.getId()).thenReturn(3L);
        lenient().when(billet.getStatut()).thenReturn(StatutBillet.EMIS);
        lenient().when(billet.getReservation()).thenReturn(reservation);
        lenient().when(billetService.findEntity(3L)).thenReturn(billet);
        // the passenger is checked in
        lenient().when(enregistrementRepository.findByBilletId(3L)).thenReturn(Optional.of(new Enregistrement()));
        lenient().when(evenementRepository.save(any(EvenementEmbarquement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void dernierControle(ResultatControle resultat) {
        ControleSecuritePassager controle = mock(ControleSecuritePassager.class);
        when(controle.getResultat()).thenReturn(resultat);
        when(controleRepository.findFirstByBilletIdOrderByDateControleDescIdDesc(3L)).thenReturn(Optional.of(controle));
    }

    @Test
    void boardingWithoutSecurityCheckIsRefused() {
        when(controleRepository.findFirstByBilletIdOrderByDateControleDescIdDesc(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enregistrerEvenement(3L,
                new EmbarquementRequest(TypeEvenementEmbarquement.EMBARQUE, "B12", null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("contrôle de sécurité");
    }

    @Test
    void boardingAfterAnExtraSearchResultIsRefused() {
        dernierControle(ResultatControle.FOUILLE_COMPLEMENTAIRE);

        assertThatThrownBy(() -> service.enregistrerEvenement(3L,
                new EmbarquementRequest(TypeEvenementEmbarquement.EMBARQUE, "B12", null)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void boardingAfterConformingCheckMarksTicketUsed() {
        dernierControle(ResultatControle.CONFORME);

        var evenement = service.enregistrerEvenement(3L,
                new EmbarquementRequest(TypeEvenementEmbarquement.EMBARQUE, "b12", null));

        assertThat(evenement.porte()).isEqualTo("B12");
        org.mockito.Mockito.verify(billet).setStatut(StatutBillet.UTILISE);
    }

    @Test
    void refusingBoardingRequiresAReason() {
        assertThatThrownBy(() -> service.enregistrerEvenement(3L,
                new EmbarquementRequest(TypeEvenementEmbarquement.REFUSE, "B12", "  ")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("motif");
    }
}
