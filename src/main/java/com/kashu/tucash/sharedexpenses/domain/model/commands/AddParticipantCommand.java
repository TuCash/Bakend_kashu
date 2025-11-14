package com.kashu.tucash.sharedexpenses.domain.model.commands;

import java.math.BigDecimal;

public record AddParticipantCommand(
        Long sharedExpenseId,
        Long participantUserId,
        BigDecimal amountOwed
) {
}
