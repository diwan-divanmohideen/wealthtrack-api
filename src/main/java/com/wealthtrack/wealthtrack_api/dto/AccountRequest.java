package com.wealthtrack.wealthtrack_api.dto;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// dtos are the Request/Response payloads
// You're getting a guaranteed-correct immutable value type from the compiler itself. 
// That's why we use records for DTOs and reserve Lombok for the entities that genuinely can't be records.
@Schema(description = "Request payload to open a new account")
public record AccountRequest (

    @Schema(description = "Display name for the account", example = "My Roth IRA")
    @NotBlank
    String name,

    @Schema(description = "Full name of the account owner", example = "Jane Doe")
    @NotBlank
    String ownerName,

    @Schema(description = "Owner's age in years (drives the contribution-limit strategy)", example = "35")
    @Min(18)
    @Max(120)
    int ownerAge,

    @Schema(description = "Type of account to open", example = "RETIREMENT")
    @NotNull
    AccountType type,

    @Schema(description = "Opening balance; zero or positive", example = "1000.00")
    @NotNull
    @DecimalMin(value = "0.00", message = "Initial balance cannot be negative")
    BigDecimal initialBalance
) {}
