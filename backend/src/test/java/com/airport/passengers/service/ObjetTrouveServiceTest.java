package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.passengers.dto.DecisionRestitutionRequest;
import com.airport.passengers.entity.DemandeRestitutionObjet;
import com.airport.passengers.entity.ObjetTrouve;
import com.airport.passengers.entity.StatutObjet;
import com.airport.passengers.entity.StatutRestitution;
import com.airport.passengers.repository.DemandeRestitutionObjetRepository;
import com.airport.passengers.repository.ObjetTrouveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjetTrouveServiceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 10, 29, 10, 0);

    @Mock private ObjetTrouveRepository objetRepository;
    @Mock private DemandeRestitutionObjetRepository demandeRepository;
    @Mock private PassagerService passagerService;

    private ObjetTrouveService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new ObjetTrouveService(objetRepository, demandeRepository, passagerService, clock);
    }

    private ObjetTrouve objetTrouveIlYA(int jours) {
        ObjetTrouve objet = mock(ObjetTrouve.class);
        lenient().when(objet.getId()).thenReturn(1L);
        lenient().when(objet.getStatut()).thenReturn(StatutObjet.EN_STOCK);
        lenient().when(objet.getDateTrouve()).thenReturn(MAINTENANT.minusDays(jours));
        lenient().when(objetRepository.findById(1L)).thenReturn(Optional.of(objet));
        return objet;
    }

    @Test
    void objectKeptLessThan90DaysCannotBeEliminated() {
        objetTrouveIlYA(30);

        assertThatThrownBy(() -> service.eliminer(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("90 jours");
    }

    @Test
    void objectKeptMoreThan90DaysCanBeEliminated() {
        ObjetTrouve objet = objetTrouveIlYA(120);

        service.eliminer(1L);

        org.mockito.Mockito.verify(objet).setStatut(StatutObjet.ELIMINE);
    }

    @Test
    void acceptingAClaimRefusesTheOtherPendingClaims() {
        ObjetTrouve objet = objetTrouveIlYA(2);
        DemandeRestitutionObjet acceptee = demande(10L, objet);
        DemandeRestitutionObjet autre = demande(11L, objet);
        when(demandeRepository.findById(10L)).thenReturn(Optional.of(acceptee));
        when(demandeRepository.findByObjetTrouveIdAndStatut(1L, StatutRestitution.EN_ATTENTE))
                .thenReturn(List.of(acceptee, autre));

        service.decider(10L, new DecisionRestitutionRequest(true, null));

        org.mockito.Mockito.verify(acceptee).decider(org.mockito.ArgumentMatchers.eq(StatutRestitution.ACCEPTEE),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        org.mockito.Mockito.verify(autre).decider(org.mockito.ArgumentMatchers.eq(StatutRestitution.REFUSEE),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        org.mockito.Mockito.verify(objet).setStatut(StatutObjet.RESTITUE);
    }

    @Test
    void refusingAClaimRequiresAComment() {
        ObjetTrouve objet = objetTrouveIlYA(2);
        DemandeRestitutionObjet demande = demande(10L, objet);
        when(demandeRepository.findById(10L)).thenReturn(Optional.of(demande));

        assertThatThrownBy(() -> service.decider(10L, new DecisionRestitutionRequest(false, "")))
                .isInstanceOf(BusinessRuleException.class);
        assertThat(demande.getStatut()).isEqualTo(StatutRestitution.EN_ATTENTE);
    }

    private DemandeRestitutionObjet demande(Long id, ObjetTrouve objet) {
        DemandeRestitutionObjet demande = mock(DemandeRestitutionObjet.class);
        lenient().when(demande.getId()).thenReturn(id);
        lenient().when(demande.getStatut()).thenReturn(StatutRestitution.EN_ATTENTE);
        lenient().when(demande.getObjetTrouve()).thenReturn(objet);
        return demande;
    }
}
