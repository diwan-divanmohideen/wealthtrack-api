package com.wealthtrack.wealthtrack_api.service;

import com.wealthtrack.wealthtrack_api.dto.AccountRequest;
import com.wealthtrack.wealthtrack_api.dto.AccountResponse;
import com.wealthtrack.wealthtrack_api.dto.BalanceResponse;
import com.wealthtrack.wealthtrack_api.dto.ContributionRequest;
import com.wealthtrack.wealthtrack_api.dto.TransactionResponse;
import com.wealthtrack.wealthtrack_api.dto.WithdrawalRequest;
import com.wealthtrack.wealthtrack_api.enums.TransactionType;
import com.wealthtrack.wealthtrack_api.exception.AccountNotFoundException;
import com.wealthtrack.wealthtrack_api.exception.InsufficientFundsException;
import com.wealthtrack.wealthtrack_api.factory.ContributionStrategyFactory;
import com.wealthtrack.wealthtrack_api.model.Account;
import com.wealthtrack.wealthtrack_api.model.Transaction;
import com.wealthtrack.wealthtrack_api.repository.AccountRepository;
import com.wealthtrack.wealthtrack_api.repository.TransactionRepository;
import com.wealthtrack.wealthtrack_api.strategy.ContributionStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.List;

// @Service marks this as the business-logic bean. It implements the
// AccountService interface, so the controller depends on the abstraction
// (interface) and Spring injects this implementation at runtime.
@Service
public class AccountServiceImpl implements AccountService {

    // All dependencies are final + set via the constructor (constructor injection).
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ContributionStrategyFactory strategyFactory;

    // Constructor injection: Spring passes these beans in automatically.
    // No @Autowired needed because there is exactly one constructor.
    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              ContributionStrategyFactory strategyFactory) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional // wraps the method in a DB transaction (commit on success, rollback on exception)
    public AccountResponse createAccount(AccountRequest request) {
        // Build the entity from the request using the Builder pattern.
        Account account = Account.builder()
                .name(request.name())
                .ownerName(request.ownerName())
                .ownerAge(request.ownerAge())
                .type(request.type())
                .balance(request.initialBalance())
                .createdAt(Instant.now())
                .build();

        // Persist, then map the saved entity (now with a generated id) to a DTO.
        return toAccountResponse(accountRepository.save(account));
    }

    @Override
    @Transactional(readOnly = true) // read-only: a hint that lets the DB/JPA optimise (no dirty checking)
    public List<AccountResponse> getAllAccounts() {
        // Stream: map each entity to its response DTO, collect to a List.
        return accountRepository.findAll().stream()
                .map(this::toAccountResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long id) {
        // findAccountOrThrow centralises the "404 if missing" rule.
        return toAccountResponse(findAccountOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long id) {
        Account account = findAccountOrThrow(id);
        return new BalanceResponse(account.getId(), account.getType(), account.getBalance());
    }

    @Override
    @Transactional
    public TransactionResponse contribute(Long id, ContributionRequest request) {
        // 1. Load the account (or 404).
        Account account = findAccountOrThrow(id);

        // 2. Stream: sum THIS YEAR's contributions to feed the limit check.
        //    filter -> keep only this calendar year, map -> amounts, reduce -> total.
        BigDecimal contributedThisYear = transactionRepository
                .findByAccount_IdAndTypeOrderByTimestampDesc(id, TransactionType.CONTRIBUTION).stream()
                .filter(tx -> tx.getTimestamp().atZone(ZoneOffset.UTC).getYear() == Year.now().getValue())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Factory picks the right strategy for this account type; strategy validates.
        //    (Throws ContributionLimitExceededException -> 409 if over the limit.)
        ContributionStrategy strategy = strategyFactory.getStrategy(account.getType());
        strategy.validateContribution(account, request.amount(), contributedThisYear);

        // 4. Apply the contribution to the balance and save the account.
        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        // 5. Record the movement as a CONTRIBUTION transaction.
        Transaction transaction = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(TransactionType.CONTRIBUTION)
                .amount(request.amount())
                .description(request.description())
                .timestamp(Instant.now())
                .build());

        return toTransactionResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getContributions(Long id) {
        findAccountOrThrow(id); // ensure the account exists, else 404
        return transactionRepository
                .findByAccount_IdAndTypeOrderByTimestampDesc(id, TransactionType.CONTRIBUTION).stream()
                .map(this::toTransactionResponse)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(Long id, WithdrawalRequest request) {
        Account account = findAccountOrThrow(id);

        // Guard: balance must be >= requested amount. compareTo < 0 means "less than".
        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(id, account.getBalance(), request.amount());
        }

        // Debit the balance and save.
        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);

        // Record the movement as a WITHDRAWAL transaction.
        Transaction transaction = transactionRepository.save(Transaction.builder()
                .account(account)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.amount())
                .description(request.description())
                .timestamp(Instant.now())
                .build());

        return toTransactionResponse(transaction);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        // Load (or 404) first so deleting a missing account returns a clean 404.
        accountRepository.delete(findAccountOrThrow(id));
    }

    // --- private helpers -------------------------------------------------

    // Single source of truth for "fetch by id or throw 404".
    // Optional.orElseThrow turns the empty Optional into a thrown exception,
    // which is how we honour the "never return null" rule.
    private Account findAccountOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    // Entity -> DTO mapping. Entities never leave the service layer.
    private AccountResponse toAccountResponse(Account a) {
        return new AccountResponse(a.getId(), a.getName(), a.getOwnerName(),
                a.getOwnerAge(), a.getType(), a.getBalance(), a.getCreatedAt());
    }

    private TransactionResponse toTransactionResponse(Transaction t) {
        return new TransactionResponse(t.getId(), t.getAccount().getId(), t.getType(),
                t.getAmount(), t.getDescription(), t.getTimestamp());
    }
}
