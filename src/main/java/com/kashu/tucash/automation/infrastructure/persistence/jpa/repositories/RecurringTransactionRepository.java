package com.kashu.tucash.automation.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.automation.domain.model.aggregates.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    List<RecurringTransaction> findByUserId(Long userId);

    @Query("SELECT r FROM RecurringTransaction r WHERE r.active = true AND r.nextExecutionDate <= :referenceDate")
    List<RecurringTransaction> findDueTransactions(LocalDate referenceDate);
}
