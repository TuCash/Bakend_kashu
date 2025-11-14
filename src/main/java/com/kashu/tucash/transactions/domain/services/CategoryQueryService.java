package com.kashu.tucash.transactions.domain.services;

import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.queries.GetAllCategoriesByUserIdAndTypeQuery;

import java.util.List;

public interface CategoryQueryService {
    List<Category> handle(GetAllCategoriesByUserIdAndTypeQuery query);
}
