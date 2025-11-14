package com.kashu.tucash.automation.domain.services;

import com.kashu.tucash.automation.domain.model.aggregates.RecurringTransaction;
import com.kashu.tucash.automation.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.kashu.tucash.automation.domain.model.queries.GetRecurringTransactionsByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface RecurringTransactionQueryService {
    Optional<RecurringTransaction> handle(GetRecurringTransactionByIdQuery query);
    List<RecurringTransaction> handle(GetRecurringTransactionsByUserIdQuery query);
}
