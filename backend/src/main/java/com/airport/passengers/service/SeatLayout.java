package com.airport.passengers.service;

import com.airport.passengers.entity.ClasseVoyage;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the seat map of a flight from its capacity, with no database access
 * (a "pure" class: easy to unit test).
 *
 * Rules (simplified single-aisle aircraft):
 *  - 6 seats per row, letters A to F;
 *  - the first 10% of rows (at least 1) are business class (AFFAIRES);
 *  - the last row may be incomplete so the total equals the capacity.
 *
 * Example, capacity 180: 30 rows, rows 1-3 = 18 business seats, 162 economy seats.
 */
public final class SeatLayout {

    public static final String LETTRES = "ABCDEF";
    public static final int SIEGES_PAR_RANGEE = LETTRES.length();

    /** One generated seat position. */
    public record Position(String numero, ClasseVoyage classe) {
    }

    private SeatLayout() {
    }

    public static int nombreRangees(int capacite) {
        return (capacite + SIEGES_PAR_RANGEE - 1) / SIEGES_PAR_RANGEE; // integer ceiling
    }

    public static int rangeesAffaires(int capacite) {
        int rangees = nombreRangees(capacite);
        return Math.max(1, (rangees + 9) / 10); // ceiling of 10%
    }

    /** Number of seats available in a class: used to refuse overbooking. */
    public static int nombreSieges(int capacite, ClasseVoyage classe) {
        int affaires = Math.min(capacite, rangeesAffaires(capacite) * SIEGES_PAR_RANGEE);
        return classe == ClasseVoyage.AFFAIRES ? affaires : capacite - affaires;
    }

    public static List<Position> generer(int capacite) {
        List<Position> positions = new ArrayList<>(capacite);
        int rangeesAffaires = rangeesAffaires(capacite);
        for (int index = 0; index < capacite; index++) {
            int rangee = index / SIEGES_PAR_RANGEE + 1;
            char lettre = LETTRES.charAt(index % SIEGES_PAR_RANGEE);
            ClasseVoyage classe = rangee <= rangeesAffaires ? ClasseVoyage.AFFAIRES : ClasseVoyage.ECONOMIQUE;
            positions.add(new Position(rangee + String.valueOf(lettre), classe));
        }
        return positions;
    }
}
