package com.kashu.tucash.sharedexpenses.domain.model.commands;

import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SplitMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateSharedExpenseCommand(
        Long userId,
        String title,
        String description,
        BigDecimal totalAmount,
        String currency,
        Long categoryId,
        LocalDate expenseDate,
        SplitMethod splitMethod,
        List<ParticipantDTO> participants
) {
    public record ParticipantDTO(
            Long userId,
            BigDecimal amountOwed
    ) {}
}
