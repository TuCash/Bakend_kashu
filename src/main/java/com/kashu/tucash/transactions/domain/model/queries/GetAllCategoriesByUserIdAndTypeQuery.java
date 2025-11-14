package com.kashu.tucash.transactions.domain.model.queries;

import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;

public record GetAllCategoriesByUserIdAndTypeQuery(Long userId, CategoryType type) {
}
