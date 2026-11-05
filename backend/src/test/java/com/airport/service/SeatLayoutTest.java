package com.airport.passengers.service;

import com.airport.passengers.entity.ClasseVoyage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Pure unit test: no Spring, no database, runs in milliseconds. */
class SeatLayoutTest {

    @Test
    void capacity180_gives30RowsAnd18BusinessSeats() {
        assertThat(SeatLayout.nombreRangees(180)).isEqualTo(30);
        assertThat(SeatLayout.nombreSieges(180, ClasseVoyage.AFFAIRES)).isEqualTo(18);
        assertThat(SeatLayout.nombreSieges(180, ClasseVoyage.ECONOMIQUE)).isEqualTo(162);
    }

    @Test
    void generatedSeatsMatchCapacityAndClasses() {
        List<SeatLayout.Position> sieges = SeatLayout.generer(160);

        assertThat(sieges).hasSize(160);
        assertThat(sieges.get(0).numero()).isEqualTo("1A");
        assertThat(sieges.get(0).classe()).isEqualTo(ClasseVoyage.AFFAIRES);
        assertThat(sieges.get(159).numero()).isEqualTo("27D"); // last row is incomplete
        assertThat(sieges.get(159).classe()).isEqualTo(ClasseVoyage.ECONOMIQUE);
        long affaires = sieges.stream().filter(s -> s.classe() == ClasseVoyage.AFFAIRES).count();
        assertThat(affaires).isEqualTo(SeatLayout.nombreSieges(160, ClasseVoyage.AFFAIRES));
    }

    @Test
    void verySmallAircraftHasAtLeastOneBusinessRow() {
        assertThat(SeatLayout.rangeesAffaires(12)).isEqualTo(1);
        assertThat(SeatLayout.nombreSieges(4, ClasseVoyage.AFFAIRES)).isEqualTo(4);
        assertThat(SeatLayout.nombreSieges(4, ClasseVoyage.ECONOMIQUE)).isZero();
    }
}
