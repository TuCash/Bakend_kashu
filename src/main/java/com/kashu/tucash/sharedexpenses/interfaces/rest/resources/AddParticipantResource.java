package com.kashu.tucash.sharedexpenses.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddParticipantResource(
        @NotNull(message = "Participant user ID is required")
        Long userId,

        @NotNull(message = "Amount owed is required")
        @Positive(message = "Amount owed must be positive")
        BigDecimal amountOwed
) {
}
