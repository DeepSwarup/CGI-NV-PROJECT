package com.bank.bankApp.utils;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Random;

@Component
public class AccountNumberGenerator {

    private final Random random = new SecureRandom();
    private static final long MIN_VALUE = 10000000000L; // Smallest 11-digit number
    private static final long MAX_VALUE = 99999999999L; // Largest 11-digit number

    /**
     * Generates a random, positive 11-digit number.
     * @return A long representing the 11-digit account number.
     */
    public long generate() {
        // Generates a long between 0 (inclusive) and MAX_VALUE - MIN_VALUE + 1 (exclusive)
        // then adds MIN_VALUE to shift it into the 11-digit range.
        return MIN_VALUE + (long)(random.nextDouble() * (MAX_VALUE - MIN_VALUE + 1));
    }
}