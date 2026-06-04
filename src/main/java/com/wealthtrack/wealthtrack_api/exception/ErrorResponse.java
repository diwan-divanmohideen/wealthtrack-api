package com.wealthtrack.wealthtrack_api.exception;

import java.time.Instant;

// The single, consistent error shape every failed request returns,
// exactly as CLAUDE.md mandates: { code, message, timestamp }.
// A record because it is immutable data with no behaviour.
public record ErrorResponse(String code, String message, Instant timestamp) {}
