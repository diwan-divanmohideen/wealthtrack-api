package com.wealthtrack.wealthtrack_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request payload to withdraw funds from an account")
public record WithdrawalRequest(

        @Schema(description = "Amount to withdraw; must be positive", example = "250.00")
        @NotNull
        @DecimalMin(value = "0.01", message = "Withdrawal amount must be positive")
        BigDecimal amount,

        @Schema(description = "Optional note describing the withdrawal", example = "ATM withdrawal")
        @Size(max = 255)
        String description
) {}
