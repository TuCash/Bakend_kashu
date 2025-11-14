package com.kashu.tucash.savings.interfaces.rest.transform;

import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import com.kashu.tucash.savings.interfaces.rest.resources.BudgetResource;

public class BudgetResourceFromEntityAssembler {
    public static BudgetResource toResourceFromEntity(Budget entity) {
        return new BudgetResource(
                entity.getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getLimitAmount(),
                entity.getSpentAmount(),
                entity.getRemainingAmount(),
                entity.getSpentPercentage(),
                entity.getPeriod().name(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isWarning(),
                entity.isExceeded(),
                entity.isActive()
        );
    }
}
