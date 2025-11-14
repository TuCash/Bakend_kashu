package com.kashu.tucash.sharedexpenses.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SharedExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Long> {

    @Query("SELECT DISTINCT se FROM SharedExpense se " +
           "LEFT JOIN FETCH se.participants p " +
           "WHERE se.creator.id = :userId OR p.participant.id = :userId")
    Page<SharedExpense> findAllByUserIdAsCreatorOrParticipant(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT se FROM SharedExpense se " +
           "LEFT JOIN FETCH se.participants p " +
           "WHERE (se.creator.id = :userId OR p.participant.id = :userId) " +
           "AND se.status = :status")
    Page<SharedExpense> findAllByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") SharedExpenseStatus status,
            Pageable pageable
    );
}
