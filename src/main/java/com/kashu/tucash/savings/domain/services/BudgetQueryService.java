package com.kashu.tucash.savings.domain.services;

import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import com.kashu.tucash.savings.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface BudgetQueryService {
    List<Budget> handle(GetAllBudgetsByUserIdQuery query);
    Optional<Budget> handle(GetBudgetByIdQuery query);
}
