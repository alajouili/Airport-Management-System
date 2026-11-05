package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.operations.entity.CompagnieAerienne;
import com.airport.operations.entity.StatutVol;
import com.airport.operations.entity.Vol;
import com.airport.operations.service.VolService;
import com.airport.passengers.dto.PaiementRequest;
import com.airport.passengers.dto.PaiementResponse;
import com.airport.passengers.dto.ReservationRequest;
import com.airport.passengers.dto.ReservationResponse;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.ClasseVoyage;
import com.airport.passengers.entity.ModePaiement;
import com.airport.passengers.entity.Passager;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutPaiement;
import com.airport.passengers.entity.StatutReservation;
import com.airport.passengers.repository.AffectationSiegeBilletRepository;
import com.airport.passengers.repository.BagageRepository;
import com.airport.passengers.repository.BilletRepository;
import com.airport.passengers.repository.EnregistrementRepository;
import com.airport.passengers.repository.PaiementRepository;
import com.airport.passengers.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests of the booking rules.
 * The repositories are replaced by Mockito "mocks" (fake objects whose answers
 * we choose), so the test needs no database and checks only the service logic.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    /** "Now" is frozen at 29 Oct 2026 10:00 for every test. */
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 29, 10, 0);

    @Mock private ReservationRepository reservationRepository;
    @Mock private PaiementRepository paiementRepository;
    @Mock private BilletRepository billetRepository;
    @Mock private AffectationSiegeBilletRepository affectationRepository;
    @Mock private EnregistrementRepository enregistrementRepository;
    @Mock private BagageRepository bagageRepository;
    @Mock private PassagerService passagerService;
    @Mock private VolService volService;

    private ReservationService service;
    private Passager passager;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new ReservationService(reservationRepository, paiementRepository, billetRepository,
                affectationRepository, enregistrementRepository, bagageRepository,
                passagerService, volService, clock);

        passager = new Passager();
        passager.setNom("Ben Ali");
        passager.setPrenom("Youssef");
        lenient().when(passagerService.findEntity(1L)).thenReturn(passager);
    }

    /** A fake flight. lenient(): a test may not use every stubbed getter. */
    private Vol volMock(StatutVol statut, LocalDateTime depart) {
        CompagnieAerienne compagnie = mock(CompagnieAerienne.class);
        lenient().when(compagnie.getNom()).thenReturn("Tunisair");
        Vol vol = mock(Vol.class);
        lenient().when(vol.getId()).thenReturn(10L);
        lenient().when(vol.getStatut()).thenReturn(statut);
        lenient().when(vol.getDateHeureDepart()).thenReturn(depart);
        lenient().when(vol.getCapacite()).thenReturn(180);
        lenient().when(vol.getCompagnie()).thenReturn(compagnie);
        lenient().when(volService.findEntity(10L)).thenReturn(vol);
        return vol;
    }

    @Test
    void create_refusesDepartedFlight() {
        volMock(StatutVol.PROGRAMME, MAINTENANT.minusHours(1));

        assertThatThrownBy(() -> service.create(new ReservationRequest(1L, 10L, ClasseVoyage.ECONOMIQUE)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("plus ouvert");
    }

    @Test
    void create_refusesCancelledFlight() {
        volMock(StatutVol.ANNULE, MAINTENANT.plusDays(1));

        assertThatThrownBy(() -> service.create(new ReservationRequest(1L, 10L, ClasseVoyage.ECONOMIQUE)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void create_refusesWhenClassIsFull() {
        volMock(StatutVol.PROGRAMME, MAINTENANT.plusDays(1));
        // 180 seats -> 18 business seats, all already taken
        when(reservationRepository.countByVolIdAndClasseAndStatutNot(10L, ClasseVoyage.AFFAIRES, StatutReservation.ANNULEE))
                .thenReturn(18L);

        assertThatThrownBy(() -> service.create(new ReservationRequest(1L, 10L, ClasseVoyage.AFFAIRES)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Plus aucune place");
    }

    @Test
    void create_computesAmountFromClassAndWaitsForPayment() {
        volMock(StatutVol.PROGRAMME, MAINTENANT.plusDays(1));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = service.create(new ReservationRequest(1L, 10L, ClasseVoyage.ECONOMIQUE));

        assertThat(response.montant()).isEqualByComparingTo(new BigDecimal("350.000"));
        assertThat(response.statut()).isEqualTo(StatutReservation.EN_ATTENTE_PAIEMENT);
        assertThat(response.reference()).hasSize(6);
    }

    @Test
    void payer_refusedPaymentKeepsReservationWaitingAndIssuesNoTicket() {
        Reservation reservation = reservationEnAttente();

        PaiementResponse paiement = service.payer(5L, new PaiementRequest(ModePaiement.CARTE, true));

        assertThat(paiement.statut()).isEqualTo(StatutPaiement.REFUSE);
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.EN_ATTENTE_PAIEMENT);
        verify(billetRepository, never()).save(any(Billet.class));
    }

    @Test
    void payer_acceptedPaymentConfirmsReservationAndIssuesTicket() {
        Reservation reservation = reservationEnAttente();

        PaiementResponse paiement = service.payer(5L, new PaiementRequest(ModePaiement.CARTE, false));

        assertThat(paiement.statut()).isEqualTo(StatutPaiement.ACCEPTE);
        assertThat(paiement.montant()).isEqualByComparingTo(reservation.getMontant());
        assertThat(reservation.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
        verify(billetRepository).save(any(Billet.class));
    }

    @Test
    void payer_refusesSecondPaymentOfConfirmedReservation() {
        Reservation reservation = reservationEnAttente();
        reservation.setStatut(StatutReservation.CONFIRMEE);

        assertThatThrownBy(() -> service.payer(5L, new PaiementRequest(ModePaiement.ESPECES, false)))
                .isInstanceOf(BusinessRuleException.class);
    }

    private Reservation reservationEnAttente() {
        Reservation reservation = new Reservation();
        reservation.setPassager(passager);
        reservation.setClasse(ClasseVoyage.ECONOMIQUE);
        reservation.setMontant(new BigDecimal("350.000"));
        reservation.setStatut(StatutReservation.EN_ATTENTE_PAIEMENT);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        return reservation;
    }
}
