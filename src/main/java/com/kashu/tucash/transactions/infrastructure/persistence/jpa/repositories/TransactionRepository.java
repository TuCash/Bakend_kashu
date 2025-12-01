package com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    // Buscar contribuciones a una meta
    List<Transaction> findByLinkedGoalIdOrderByTransactionDateDesc(Long goalId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
            "AND (:type IS NULL OR t.type = :type) " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "AND (:fromDate IS NULL OR t.transactionDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.transactionDate <= :toDate) " +
            "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    Page<Transaction> findByFilters(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
