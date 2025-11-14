package com.kashu.tucash.notifications.infrastructure.persistence.jpa.repositories;

import com.kashu.tucash.notifications.domain.model.aggregates.Notification;
import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "AND (:type IS NULL OR n.type = :type) " +
            "AND (:isRead IS NULL OR n.isRead = :isRead) " +
            "ORDER BY n.createdAt DESC")
    Page<Notification> findByFilters(
            @Param("userId") Long userId,
            @Param("type") NotificationType type,
            @Param("isRead") Boolean isRead,
            Pageable pageable
    );

    Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Long countByUserIdAndIsReadFalse(Long userId);
}
