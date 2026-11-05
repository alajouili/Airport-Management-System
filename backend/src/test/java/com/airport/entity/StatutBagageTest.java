package com.airport.passengers.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatutBagageTest {

    @Test
    void normalJourneyIsAllowed() {
        assertThat(StatutBagage.ENREGISTRE.canTransitionTo(StatutBagage.TRIE)).isTrue();
        assertThat(StatutBagage.TRIE.canTransitionTo(StatutBagage.CHARGE)).isTrue();
        assertThat(StatutBagage.CHARGE.canTransitionTo(StatutBagage.DECHARGE)).isTrue();
        assertThat(StatutBagage.DECHARGE.canTransitionTo(StatutBagage.LIVRE)).isTrue();
    }

    @Test
    void stepsCannotBeSkipped() {
        assertThat(StatutBagage.ENREGISTRE.canTransitionTo(StatutBagage.LIVRE)).isFalse();
        assertThat(StatutBagage.TRIE.canTransitionTo(StatutBagage.DECHARGE)).isFalse();
    }

    @Test
    void lostBagCanOnlyBeFoundThenDelivered() {
        assertThat(StatutBagage.CHARGE.canTransitionTo(StatutBagage.PERDU)).isTrue();
        assertThat(StatutBagage.PERDU.canTransitionTo(StatutBagage.LIVRE)).isFalse();
        assertThat(StatutBagage.PERDU.canTransitionTo(StatutBagage.RETROUVE)).isTrue();
        assertThat(StatutBagage.RETROUVE.canTransitionTo(StatutBagage.LIVRE)).isTrue();
    }

    @Test
    void deliveredIsFinal() {
        for (StatutBagage cible : StatutBagage.values()) {
            assertThat(StatutBagage.LIVRE.canTransitionTo(cible)).isFalse();
        }
    }
}
