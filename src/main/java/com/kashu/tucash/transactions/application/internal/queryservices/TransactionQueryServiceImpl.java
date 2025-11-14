package com.kashu.tucash.transactions.application.internal.queryservices;

import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.domain.model.queries.GetAllTransactionsByUserIdQuery;
import com.kashu.tucash.transactions.domain.model.queries.GetTransactionByIdQuery;
import com.kashu.tucash.transactions.domain.services.TransactionQueryService;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionQueryServiceImpl implements TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public TransactionQueryServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<Transaction> handle(GetTransactionByIdQuery query) {
        return transactionRepository.findById(query.transactionId());
    }

    @Override
    public Page<Transaction> handle(GetAllTransactionsByUserIdQuery query) {
        return transactionRepository.findByFilters(
                query.userId(),
                query.type(),
                query.categoryId(),
                query.fromDate(),
                query.toDate(),
                query.pageable()
        );
    }
}
