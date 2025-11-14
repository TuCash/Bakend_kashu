package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.interfaces.rest.resources.CategoryResource;

public class CategoryResourceFromEntityAssembler {
    public static CategoryResource toResourceFromEntity(Category entity) {
        return new CategoryResource(
                entity.getId(),
                entity.getName(),
                entity.getType().name(),
                entity.getIcon(),
                entity.getColor(),
                entity.isSystemCategory()
        );
    }
}
