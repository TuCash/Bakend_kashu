package com.kashu.tucash.sharedexpenses.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateSharedExpenseResource(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be positive")
        BigDecimal totalAmount,

        String currency,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        @NotNull(message = "Expense date is required")
        LocalDate expenseDate,

        @NotNull(message = "Split method is required")
        String splitMethod,

        @NotNull(message = "Participants list is required")
        List<ParticipantDTO> participants
) {
    public record ParticipantDTO(
            @NotNull(message = "Participant user ID is required")
            Long userId,

            @NotNull(message = "Amount owed is required")
            @Positive(message = "Amount owed must be positive")
            BigDecimal amountOwed
    ) {}
}
