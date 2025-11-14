package com.kashu.tucash.transactions.domain.services;

import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.domain.model.queries.GetAllTransactionsByUserIdQuery;
import com.kashu.tucash.transactions.domain.model.queries.GetTransactionByIdQuery;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TransactionQueryService {
    Optional<Transaction> handle(GetTransactionByIdQuery query);
    Page<Transaction> handle(GetAllTransactionsByUserIdQuery query);
}
