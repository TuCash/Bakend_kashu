package com.kashu.tucash.sharedexpenses.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParticipantResource(
        Long id,
        Long sharedExpenseId,
        Long participantId,
        String participantName,
        String participantEmail,
        BigDecimal amountOwed,
        BigDecimal amountPaid,
        Boolean isPaid,
        LocalDateTime paidAt
) {
}
