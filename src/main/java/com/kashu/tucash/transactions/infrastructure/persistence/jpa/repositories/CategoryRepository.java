package com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Categorías del sistema (sin usuario) + categorías del usuario
    @Query("SELECT c FROM Category c WHERE (c.user IS NULL OR c.user.id = :userId) AND c.type = :type")
    List<Category> findByUserIdOrSystemAndType(Long userId, CategoryType type);

    // Solo categorías del usuario
    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    // Categorías del sistema
    List<Category> findByUserIsNullAndType(CategoryType type);
}
