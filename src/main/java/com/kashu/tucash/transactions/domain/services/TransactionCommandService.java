package com.kashu.tucash.transactions.domain.services;

import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.commands.DeleteTransactionCommand;
import com.kashu.tucash.transactions.domain.model.commands.UpdateTransactionCommand;

import java.util.Optional;

public interface TransactionCommandService {
    Optional<Transaction> handle(CreateTransactionCommand command);
    Optional<Transaction> handle(UpdateTransactionCommand command);
    void handle(DeleteTransactionCommand command);
}
