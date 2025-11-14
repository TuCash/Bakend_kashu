package com.kashu.tucash.automation.interfaces.rest.transform;

import com.kashu.tucash.automation.domain.model.commands.UpdateRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.valueobjects.RecurringFrequency;
import com.kashu.tucash.automation.interfaces.rest.resources.UpdateRecurringTransactionResource;

public class UpdateRecurringTransactionCommandFromResourceAssembler {

    private UpdateRecurringTransactionCommandFromResourceAssembler() {
    }

    public static UpdateRecurringTransactionCommand toCommandFromResource(Long recurringTransactionId,
                                                                          UpdateRecurringTransactionResource resource) {
        return new UpdateRecurringTransactionCommand(
                recurringTransactionId,
                resource.amount(),
                resource.description(),
                resource.startDate(),
                resource.endDate(),
                resource.frequency() != null
                        ? RecurringFrequency.valueOf(resource.frequency().toUpperCase())
                        : null
        );
    }
}
