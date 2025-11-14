package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.commands.CreateCategoryCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;
import com.kashu.tucash.transactions.interfaces.rest.resources.CreateCategoryResource;

public class CreateCategoryCommandFromResourceAssembler {
    public static CreateCategoryCommand toCommandFromResource(Long userId, CreateCategoryResource resource) {
        return new CreateCategoryCommand(
                userId,
                resource.name(),
                CategoryType.valueOf(resource.type().toUpperCase()),
                resource.icon(),
                resource.color()
        );
    }
}
