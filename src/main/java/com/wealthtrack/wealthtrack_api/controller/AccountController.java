package com.wealthtrack.wealthtrack_api.controller;

import com.wealthtrack.wealthtrack_api.dto.AccountRequest;
import com.wealthtrack.wealthtrack_api.dto.AccountResponse;
import com.wealthtrack.wealthtrack_api.dto.BalanceResponse;
import com.wealthtrack.wealthtrack_api.dto.ContributionRequest;
import com.wealthtrack.wealthtrack_api.dto.TransactionResponse;
import com.wealthtrack.wealthtrack_api.dto.WithdrawalRequest;
import com.wealthtrack.wealthtrack_api.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

// @RestController = @Controller + @ResponseBody. Every method's return value is
//   serialized straight to the HTTP response body as JSON (no HTML view).
// @RequestMapping sets the base path; method mappings below append to it.
// @Tag groups these endpoints under "Accounts" in the Swagger UI.
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Manage investment accounts, contributions, and withdrawals")
public class AccountController {

    // Depend on the AccountService INTERFACE, not the implementation.
    private final AccountService accountService;

    // Constructor injection (CLAUDE.md: no @Autowired field injection).
    // One constructor => Spring autowires it without needing @Autowired.
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // POST /api/v1/accounts  -> create a new account.
    // @Valid runs the Bean Validation on AccountRequest BEFORE this body runs;
    //   a violation throws -> GlobalExceptionHandler returns 400.
    // @RequestBody deserializes the JSON body into the AccountRequest record.
    // We return 201 Created plus a Location header pointing at the new resource.
    @Operation(summary = "Open a new account",
            description = "Creates an account of the given type and returns it with a generated id.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            UriComponentsBuilder uriBuilder) {

        AccountResponse created = accountService.createAccount(request);
        // Build the URL of the newly created account: /api/v1/accounts/{id}
        URI location = uriBuilder.path("/api/v1/accounts/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // GET /api/v1/accounts  -> list all accounts. Always 200 with a (possibly empty) list.
    @Operation(summary = "List all accounts")
    @ApiResponse(responseCode = "200", description = "Accounts returned")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // GET /api/v1/accounts/{id}  -> fetch one account.
    // @PathVariable binds the {id} segment from the URL to the method parameter.
    @Operation(summary = "Get an account by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    // GET /api/v1/accounts/{id}/balance  -> just the balance snapshot (a sub-resource).
    @Operation(summary = "Get an account's current balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance returned"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    // POST /api/v1/accounts/{id}/contributions  -> make a contribution.
    // Combines a path variable (which account) with a validated body (how much).
    // Returns 201 because it creates a new transaction resource.
    // 409 if the strategy rejects it (annual limit exceeded).
    @Operation(summary = "Make a contribution",
            description = "Records a contribution; rejected with 409 if it would exceed the annual limit.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contribution recorded"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Annual contribution limit exceeded")
    })
    @PostMapping("/{id}/contributions")
    public ResponseEntity<TransactionResponse> contribute(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id,
            @Valid @RequestBody ContributionRequest request) {

        TransactionResponse transaction = accountService.contribute(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    // GET /api/v1/accounts/{id}/contributions  -> this account's contribution history.
    @Operation(summary = "List an account's contributions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contributions returned"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/contributions")
    public ResponseEntity<List<TransactionResponse>> getContributions(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getContributions(id));
    }

    // POST /api/v1/accounts/{id}/withdrawals  -> withdraw funds.
    // Returns 200 (modeled as a command on the balance); 409 if insufficient funds.
    @Operation(summary = "Withdraw funds",
            description = "Debits the account; rejected with 409 if the balance is insufficient.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal completed"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient funds")
    })
    @PostMapping("/{id}/withdrawals")
    public ResponseEntity<TransactionResponse> withdraw(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id,
            @Valid @RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok(accountService.withdraw(id, request));
    }

    // DELETE /api/v1/accounts/{id}  -> remove an account. 200 on success, 404 if missing.
    // ResponseEntity<Void> = a response with no body.
    @Operation(summary = "Delete an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account deleted"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }
}
