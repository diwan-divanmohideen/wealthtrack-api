package com.wealthtrack.wealthtrack_api.factory;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import com.wealthtrack.wealthtrack_api.strategy.ContributionStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// FACTORY PATTERN: centralises the "which strategy do I use?" decision so the
// service never does `new RetirementContributionStrategy()` or a switch on type.
@Component
public class ContributionStrategyFactory {

    // A pre-built lookup: AccountType -> the strategy that handles it.
    private final Map<AccountType, ContributionStrategy> strategies;

    // Spring injects a List of EVERY ContributionStrategy bean it found
    // (Retirement + Brokerage today; more if we add @Component strategies later).
    public ContributionStrategyFactory(List<ContributionStrategy> strategyBeans) {
        // Stream: turn the List into a Map keyed by each strategy's supportedType().
        //   - ContributionStrategy::supportedType -> the map KEY
        //   - Function.identity()                 -> the map VALUE (the strategy itself)
        this.strategies = strategyBeans.stream()
                .collect(Collectors.toMap(ContributionStrategy::supportedType, Function.identity()));
    }

    // Look up the strategy for a type. If a type has no registered strategy
    // (e.g. HSA/SAVINGS, not implemented yet) we fail loudly rather than return null.
    public ContributionStrategy getStrategy(AccountType type) {
        ContributionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalStateException("No contribution strategy registered for type: " + type);
        }
        return strategy;
    }
}
