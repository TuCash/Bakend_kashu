package com.kashu.tucash.transactions.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.commands.CreateCategoryCommand;
import com.kashu.tucash.transactions.domain.services.CategoryCommandService;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryCommandServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Category> handle(CreateCategoryCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var category = new Category(command, user);
        var createdCategory = categoryRepository.save(category);

        return Optional.of(createdCategory);
    }
}
