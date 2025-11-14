package com.kashu.tucash.transactions.domain.model.queries;

import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public record GetAllTransactionsByUserIdQuery(
        Long userId,
        TransactionType type,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate,
        Pageable pageable
) {
}
