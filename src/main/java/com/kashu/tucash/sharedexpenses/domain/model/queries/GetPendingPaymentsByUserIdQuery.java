package com.kashu.tucash.sharedexpenses.domain.model.queries;

import org.springframework.data.domain.Pageable;

public record GetPendingPaymentsByUserIdQuery(
        Long userId,
        Pageable pageable
) {
}
