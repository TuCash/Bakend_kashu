package com.kashu.tucash.sharedexpenses.domain.model.queries;

import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SharedExpenseStatus;
import org.springframework.data.domain.Pageable;

public record GetSharedExpensesByStatusQuery(
        Long userId,
        SharedExpenseStatus status,
        Pageable pageable
) {
}
