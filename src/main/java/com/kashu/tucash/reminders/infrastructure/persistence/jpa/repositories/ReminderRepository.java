package com.kashu.tucash.reminders.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.reminders.domain.model.aggregates.Reminder;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Optional<Reminder> findByIdAndUserId(Long id, Long userId);

    List<Reminder> findByUserIdOrderByReminderDateDescReminderTimeDesc(Long userId);

    List<Reminder> findByUserIdAndIsActiveOrderByReminderDateDescReminderTimeDesc(Long userId, Boolean isActive);

    @Query("SELECT r FROM Reminder r WHERE r.user.id = :userId " +
            "AND (:type IS NULL OR r.type = :type) " +
            "AND (:isActive IS NULL OR r.isActive = :isActive) " +
            "ORDER BY r.reminderDate DESC, r.reminderTime DESC")
    List<Reminder> findByFilters(
            @Param("userId") Long userId,
            @Param("type") ReminderType type,
            @Param("isActive") Boolean isActive
    );

    List<Reminder> findByUserIdAndIsActiveTrueAndIsNotifiedFalseOrderByReminderDateAscReminderTimeAsc(Long userId);
}
