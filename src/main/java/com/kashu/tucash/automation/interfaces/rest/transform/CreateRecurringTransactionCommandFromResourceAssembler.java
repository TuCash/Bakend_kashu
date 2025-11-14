package com.kashu.tucash.automation.interfaces.rest.transform;

import com.kashu.tucash.automation.domain.model.commands.CreateRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.valueobjects.RecurringFrequency;
import com.kashu.tucash.automation.interfaces.rest.resources.CreateRecurringTransactionResource;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;

public class CreateRecurringTransactionCommandFromResourceAssembler {

    private CreateRecurringTransactionCommandFromResourceAssembler() {
    }

    public static CreateRecurringTransactionCommand toCommandFromResource(Long userId, CreateRecurringTransactionResource resource) {
        return new CreateRecurringTransactionCommand(
                userId,
                resource.accountId(),
                resource.categoryId(),
                TransactionType.valueOf(resource.type().toUpperCase()),
                resource.amount(),
                resource.description(),
                resource.startDate(),
                resource.endDate(),
                RecurringFrequency.valueOf(resource.frequency().toUpperCase())
        );
    }
}
