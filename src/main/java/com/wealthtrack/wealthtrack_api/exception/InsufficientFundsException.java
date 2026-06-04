package com.wealthtrack.wealthtrack_api.exception;

import java.math.BigDecimal;

// Thrown by the service when a withdrawal exceeds the available balance.
// Mapped to HTTP 409 Conflict (a business-rule violation, not a 404/400).
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId, BigDecimal balance, BigDecimal requested) {
        super("Withdrawal rejected for account " + accountId
                + ": balance " + balance + " is insufficient for requested amount " + requested);
    }
}
