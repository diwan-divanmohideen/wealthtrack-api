package com.wealthtrack.wealthtrack_api.dto;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Current balance snapshot for an account")
public record BalanceResponse(
        Long accountId,
        AccountType type,
        BigDecimal balance
) {}
