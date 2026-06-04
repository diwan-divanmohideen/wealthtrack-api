package com.wealthtrack.wealthtrack_api.exception;

// Custom domain exception. Extends RuntimeException (unchecked) so the service
// can throw it without `throws` clauses everywhere. It carries a meaningful
// message; the GlobalExceptionHandler maps it to HTTP 404.
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long accountId) {
        super("Account with id " + accountId + " was not found");
    }
}
