package com.wealthtrack.wealthtrack_api.dto;

import com.wealthtrack.wealthtrack_api.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "A single transaction record")
public record TransactionResponse(
        Long id,
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        String description,
        Instant timestamp
) {}
