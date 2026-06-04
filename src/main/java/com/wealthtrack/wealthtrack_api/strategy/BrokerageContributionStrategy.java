package com.wealthtrack.wealthtrack_api.strategy;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import com.wealthtrack.wealthtrack_api.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Brokerage ("investment") accounts have NO annual contribution limit.
// We still implement the same interface so the service can treat every
// account type uniformly (no "does this type have a limit?" special-casing).
@Component
public class BrokerageContributionStrategy implements ContributionStrategy {

    @Override
    public void validateContribution(Account account, BigDecimal amount, BigDecimal contributedThisYear) {
        // Intentionally empty: nothing to validate, all contributions are allowed.
    }

    @Override
    public AccountType supportedType() {
        return AccountType.BROKERAGE;
    }
}
