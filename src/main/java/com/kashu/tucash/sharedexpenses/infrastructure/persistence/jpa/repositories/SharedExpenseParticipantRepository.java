package com.kashu.tucash.sharedexpenses.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedExpenseParticipantRepository extends JpaRepository<SharedExpenseParticipant, Long> {

    @Query("SELECT p FROM SharedExpenseParticipant p " +
           "WHERE p.participant.id = :userId " +
           "AND p.isPaid = false " +
           "AND p.sharedExpense.status = 'PENDING'")
    Page<SharedExpenseParticipant> findPendingPaymentsByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
