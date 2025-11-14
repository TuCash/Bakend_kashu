package com.kashu.tucash.sharedexpenses.domain.model.queries;

import org.springframework.data.domain.Pageable;

public record GetAllSharedExpensesByUserIdQuery(
        Long userId,
        Pageable pageable
) {
}
