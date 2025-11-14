package com.kashu.tucash.transactions.domain.model.commands;

import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;

public record CreateCategoryCommand(
        Long userId,
        String name,
        CategoryType type,
        String icon,
        String color
) {
}
