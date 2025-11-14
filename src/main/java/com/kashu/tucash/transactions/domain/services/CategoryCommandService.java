package com.kashu.tucash.transactions.domain.services;

import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.commands.CreateCategoryCommand;

import java.util.Optional;

public interface CategoryCommandService {
    Optional<Category> handle(CreateCategoryCommand command);
}
