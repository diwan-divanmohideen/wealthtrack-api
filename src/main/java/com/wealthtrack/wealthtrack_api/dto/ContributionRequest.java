package com.wealthtrack.wealthtrack_api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload to make a contribution to an account")
public record ContributionRequest(

    @Schema(description = "Amount to contribute; must be positive", example = "500.00")
    @NotNull
    @DecimalMin(value = "0.01", message = "Contribution amount must be positive")
        BigDecimal amount,

    @Schema(description = "Optional note describing the contribution", example = "Monthly auto-contribution")
        @Size(max = 255)
        String description

) {
} 