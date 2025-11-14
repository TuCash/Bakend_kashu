package com.kashu.tucash.savings.application.internal.queryservices;

import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import com.kashu.tucash.savings.domain.model.queries.*;
import com.kashu.tucash.savings.domain.services.BudgetQueryService;
import com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetQueryServiceImpl implements BudgetQueryService {

    private final BudgetRepository budgetRepository;

    public BudgetQueryServiceImpl(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public List<Budget> handle(GetAllBudgetsByUserIdQuery query) {
        return budgetRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<Budget> handle(GetBudgetByIdQuery query) {
        return budgetRepository.findById(query.budgetId());
    }
}
