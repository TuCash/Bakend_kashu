package com.kashu.tucash.savings.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.savings.domain.services.BudgetCommandService;
import com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories.BudgetRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetCommandServiceImpl implements BudgetCommandService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetCommandServiceImpl(BudgetRepository budgetRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Budget> handle(CreateBudgetCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        var budget = new Budget(command, user, category);
        var createdBudget = budgetRepository.save(budget);
        return Optional.of(createdBudget);
    }

    @Override
    public void handle(DeleteBudgetCommand command) {
        budgetRepository.deleteById(command.budgetId());
    }
}
