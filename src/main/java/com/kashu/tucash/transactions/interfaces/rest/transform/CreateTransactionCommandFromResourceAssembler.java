package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import com.kashu.tucash.transactions.interfaces.rest.resources.CreateTransactionResource;

public class CreateTransactionCommandFromResourceAssembler {
    public static CreateTransactionCommand toCommandFromResource(Long userId, CreateTransactionResource resource) {
        return new CreateTransactionCommand(
                userId,
                resource.accountId(),
                resource.categoryId(),
                TransactionType.valueOf(resource.type().toUpperCase()),
                resource.amount(),
                resource.description(),
                resource.transactionDate()
        );
    }
}
