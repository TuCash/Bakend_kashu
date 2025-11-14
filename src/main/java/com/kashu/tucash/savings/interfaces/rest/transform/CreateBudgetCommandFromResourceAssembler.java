package com.kashu.tucash.savings.interfaces.rest.transform;

import com.kashu.tucash.savings.domain.model.commands.CreateBudgetCommand;
import com.kashu.tucash.savings.domain.model.valueobjects.BudgetPeriod;
import com.kashu.tucash.savings.interfaces.rest.resources.CreateBudgetResource;

public class CreateBudgetCommandFromResourceAssembler {
    public static CreateBudgetCommand toCommandFromResource(Long userId, CreateBudgetResource resource) {
        return new CreateBudgetCommand(
                userId,
                resource.categoryId(),
                resource.limitAmount(),
                BudgetPeriod.valueOf(resource.period().toUpperCase()),
                resource.startDate(),
                resource.endDate()
        );
    }
}
