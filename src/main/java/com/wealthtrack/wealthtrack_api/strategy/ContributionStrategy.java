package com.wealthtrack.wealthtrack_api.strategy;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import com.wealthtrack.wealthtrack_api.model.Account;

import java.math.BigDecimal;

// STRATEGY PATTERN (GoF, behavioural):
// Defines a family of interchangeable algorithms — here, "how is a contribution
// validated for this account type?". Each account type provides its own
// implementation, and callers depend ONLY on this interface, never on the
// concrete classes. This is what lets us avoid an if/else-on-type chain.
public interface ContributionStrategy {

    // Throws a ContributionLimitExceededException if the contribution is not allowed.
    // 'contributedThisYear' is supplied by the service (computed with a Stream),
    // keeping this strategy pure arithmetic with no database access.
    void validateContribution(Account account, BigDecimal amount, BigDecimal contributedThisYear);

    // Tells the factory which AccountType this strategy handles.
    // The factory uses this to build its lookup map automatically.
    AccountType supportedType();
}
