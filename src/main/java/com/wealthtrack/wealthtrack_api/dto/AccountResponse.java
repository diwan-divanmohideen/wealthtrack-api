package com.wealthtrack.wealthtrack_api.dto;

import com.wealthtrack.wealthtrack_api.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Account details returned to the client")
public record AccountResponse(
    Long id,
    String name,
    String ownerName,
    int ownerAge,
    AccountType type,
    BigDecimal balance,
    Instant createdAt

) {}
    

