package com.airport.passengers.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatutReclamationAssistanceTest {

    @Test
    void complaintMustBeTakenInChargeBeforeBeingResolved() {
        assertThat(StatutReclamation.OUVERTE.canTransitionTo(StatutReclamation.RESOLUE)).isFalse();
        assertThat(StatutReclamation.OUVERTE.canTransitionTo(StatutReclamation.EN_COURS)).isTrue();
        assertThat(StatutReclamation.EN_COURS.canTransitionTo(StatutReclamation.RESOLUE)).isTrue();
        assertThat(StatutReclamation.OUVERTE.canTransitionTo(StatutReclamation.REJETEE)).isTrue();
    }

    @Test
    void closedComplaintIsFinal() {
        for (StatutReclamation cible : StatutReclamation.values()) {
            assertThat(StatutReclamation.RESOLUE.canTransitionTo(cible)).isFalse();
            assertThat(StatutReclamation.REJETEE.canTransitionTo(cible)).isFalse();
        }
        assertThat(StatutReclamation.RESOLUE.isFinal()).isTrue();
        assertThat(StatutReclamation.EN_COURS.isFinal()).isFalse();
    }

    @Test
    void assistanceFollowsItsLifeCycle() {
        assertThat(StatutAssistance.DEMANDEE.canTransitionTo(StatutAssistance.REALISEE)).isFalse();
        assertThat(StatutAssistance.DEMANDEE.canTransitionTo(StatutAssistance.CONFIRMEE)).isTrue();
        assertThat(StatutAssistance.CONFIRMEE.canTransitionTo(StatutAssistance.REALISEE)).isTrue();
        assertThat(StatutAssistance.CONFIRMEE.canTransitionTo(StatutAssistance.ANNULEE)).isTrue();
        assertThat(StatutAssistance.REALISEE.canTransitionTo(StatutAssistance.ANNULEE)).isFalse();
    }
}
