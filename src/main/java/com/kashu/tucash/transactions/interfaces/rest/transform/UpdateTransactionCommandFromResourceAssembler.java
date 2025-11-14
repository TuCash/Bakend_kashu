package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.commands.UpdateTransactionCommand;
import com.kashu.tucash.transactions.interfaces.rest.resources.UpdateTransactionResource;

public class UpdateTransactionCommandFromResourceAssembler {
    public static UpdateTransactionCommand toCommandFromResource(Long transactionId, UpdateTransactionResource resource) {
        return new UpdateTransactionCommand(
                transactionId,
                resource.accountId(),
                resource.categoryId(),
                resource.amount(),
                resource.description(),
                resource.transactionDate()
        );
    }
}
