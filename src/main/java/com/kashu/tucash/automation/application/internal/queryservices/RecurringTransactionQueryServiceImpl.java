package com.kashu.tucash.automation.application.internal.queryservices;

import com.kashu.tucash.automation.domain.model.aggregates.RecurringTransaction;
import com.kashu.tucash.automation.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.kashu.tucash.automation.domain.model.queries.GetRecurringTransactionsByUserIdQuery;
import com.kashu.tucash.automation.domain.services.RecurringTransactionQueryService;
import com.kashu.tucash.automation.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionQueryServiceImpl implements RecurringTransactionQueryService {

    private final RecurringTransactionRepository recurringTransactionRepository;

    public RecurringTransactionQueryServiceImpl(RecurringTransactionRepository recurringTransactionRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    @Override
    public Optional<RecurringTransaction> handle(GetRecurringTransactionByIdQuery query) {
        return recurringTransactionRepository.findById(query.recurringTransactionId());
    }

    @Override
    public List<RecurringTransaction> handle(GetRecurringTransactionsByUserIdQuery query) {
        return recurringTransactionRepository.findByUserId(query.userId());
    }
}
