package com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.valueobjects.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);
    Optional<Goal> findByIdAndUserId(Long id, Long userId);
}
