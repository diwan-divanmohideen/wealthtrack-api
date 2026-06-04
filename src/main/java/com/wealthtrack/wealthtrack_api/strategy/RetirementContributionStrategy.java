package com.wealthtrack.wealthtrack_api.strategy;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import com.wealthtrack.wealthtrack_api.exception.ContributionLimitExceededException;
import com.wealthtrack.wealthtrack_api.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// @Component makes this a Spring bean. The factory will receive it (along with
// every other ContributionStrategy bean) injected as a List and register it.
@Component
public class RetirementContributionStrategy implements ContributionStrategy {

    // No magic numbers (CLAUDE.md): the limits live in named constants.
    // BigDecimal is built from a String, never a double, to avoid float noise.
    private static final BigDecimal BASE_ANNUAL_LIMIT = new BigDecimal("23000.00");
    private static final BigDecimal CATCH_UP_CONTRIBUTION = new BigDecimal("7500.00");
    private static final int CATCH_UP_AGE = 50;

    @Override
    public void validateContribution(Account account, BigDecimal amount, BigDecimal contributedThisYear) {
        // 1. Resolve the limit, which depends on the owner's age (catch-up rule).
        BigDecimal annualLimit = resolveAnnualLimit(account.getOwnerAge());

        // 2. What the year-to-date total WOULD become if we allowed this contribution.
        BigDecimal projectedTotal = contributedThisYear.add(amount);

        // 3. Compare with compareTo (NOT equals) — BigDecimal.equals also compares
        //    scale, so 2.0 != 2.00. compareTo > 0 means "strictly greater than".
        if (projectedTotal.compareTo(annualLimit) > 0) {
            throw new ContributionLimitExceededException(account.getId(), annualLimit, projectedTotal);
        }
    }

    // Age 50+ unlocks the catch-up contribution on top of the base limit.
    private BigDecimal resolveAnnualLimit(int ownerAge) {
        return ownerAge >= CATCH_UP_AGE
                ? BASE_ANNUAL_LIMIT.add(CATCH_UP_CONTRIBUTION)
                : BASE_ANNUAL_LIMIT;
    }

    @Override
    public AccountType supportedType() {
        return AccountType.RETIREMENT;
    }
}
