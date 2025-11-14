package com.kashu.tucash.sharedexpenses.interfaces.rest.transform;

import com.kashu.tucash.sharedexpenses.domain.model.commands.UpdateSharedExpenseCommand;
import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SplitMethod;
import com.kashu.tucash.sharedexpenses.interfaces.rest.resources.UpdateSharedExpenseResource;

public class UpdateSharedExpenseCommandFromResourceAssembler {

    public static UpdateSharedExpenseCommand toCommandFromResource(
            Long sharedExpenseId,
            UpdateSharedExpenseResource resource) {

        SplitMethod splitMethod = resource.splitMethod() != null
                ? SplitMethod.valueOf(resource.splitMethod().toUpperCase())
                : null;

        return new UpdateSharedExpenseCommand(
                sharedExpenseId,
                resource.title(),
                resource.description(),
                resource.totalAmount(),
                resource.currency(),
                resource.categoryId(),
                resource.expenseDate(),
                splitMethod
        );
    }
}
