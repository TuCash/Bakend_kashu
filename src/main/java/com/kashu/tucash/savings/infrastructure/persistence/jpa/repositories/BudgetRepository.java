package com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.startDate <= :date AND b.endDate >= :date")
    List<Budget> findActiveByUserIdAndDate(Long userId, LocalDate date);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.category.id = :categoryId AND b.startDate <= :date AND b.endDate >= :date")
    List<Budget> findActiveByUserIdAndCategoryId(Long userId, Long categoryId, LocalDate date);
}
