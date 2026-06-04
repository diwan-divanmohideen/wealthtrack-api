package com.wealthtrack.wealthtrack_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

// @RestControllerAdvice: one GLOBAL place that catches exceptions thrown by ANY
// controller and turns them into HTTP responses. This keeps controllers and the
// service free of try/catch — they just throw domain exceptions, this maps them.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Account doesn't exist -> 404 Not Found.
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AccountNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", ex.getMessage());
    }

    // Business-rule violation (not enough money) -> 409 Conflict.
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return build(HttpStatus.CONFLICT, "INSUFFICIENT_FUNDS", ex.getMessage());
    }

    // Business-rule violation (over the annual limit) -> 409 Conflict.
    @ExceptionHandler(ContributionLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleLimit(ContributionLimitExceededException ex) {
        return build(HttpStatus.CONFLICT, "CONTRIBUTION_LIMIT_EXCEEDED", ex.getMessage());
    }

    // @Valid failed on a request body -> 400 Bad Request.
    // We stream the field errors into one readable message.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message);
    }

    // Anything unexpected -> 500, but NEVER leak the stack trace to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    // Small helper so every handler builds the response the same way.
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, Instant.now()));
    }
}
