package com.kashu.tucash.transactions.application.internal.queryservices;

import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.queries.GetAllCategoriesByUserIdAndTypeQuery;
import com.kashu.tucash.transactions.domain.services.CategoryQueryService;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public CategoryQueryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> handle(GetAllCategoriesByUserIdAndTypeQuery query) {
        // Retorna categorías del sistema + categorías del usuario
        return categoryRepository.findByUserIdOrSystemAndType(query.userId(), query.type());
    }
}
