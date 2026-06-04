package com.wealthtrack.wealthtrack_api.exception;

import java.math.BigDecimal;

// Thrown by a ContributionStrategy when a contribution would exceed the annual
// limit. Mapped to HTTP 409 Conflict by the GlobalExceptionHandler.
public class ContributionLimitExceededException extends RuntimeException {

    public ContributionLimitExceededException(Long accountId, BigDecimal annualLimit, BigDecimal attemptedTotal) {
        super("Contribution rejected for account " + accountId
                + ": the annual limit of " + annualLimit
                + " would be exceeded (year-to-date total would become " + attemptedTotal + ")");
    }
}
