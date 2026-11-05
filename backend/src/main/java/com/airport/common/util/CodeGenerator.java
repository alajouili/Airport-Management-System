package com.airport.common.util;

import java.security.SecureRandom;

/**
 * Generates random codes: reservation references ("K7QZ2M"), ticket numbers,
 * baggage tags, transaction references.
 *
 * SecureRandom is used instead of Random because its values cannot be predicted,
 * so nobody can guess another customer's reservation reference.
 * Services still check in the database that a generated code is not already used.
 */
public final class CodeGenerator {

    /** No 0/O and 1/I: easier to read aloud at a check-in desk. */
    private static final String LETTERS_AND_DIGITS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodeGenerator() {
        // Utility class: only static methods, no instances.
    }

    public static String alphanumeric(int length) {
        return random(LETTERS_AND_DIGITS, length);
    }

    public static String digits(int length) {
        return random(DIGITS, length);
    }

    private static String random(String alphabet, int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        return code.toString();
    }
}
