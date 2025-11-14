package com.kashu.tucash.notifications.application.internal.queryservices;

import com.kashu.tucash.notifications.domain.model.aggregates.Notification;
import com.kashu.tucash.notifications.domain.model.queries.GetAllNotificationsByUserIdQuery;
import com.kashu.tucash.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.kashu.tucash.notifications.domain.model.queries.GetUnreadNotificationsByUserIdQuery;
import com.kashu.tucash.notifications.domain.services.NotificationQueryService;
import com.kashu.tucash.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }

    @Override
    public Page<Notification> handle(GetAllNotificationsByUserIdQuery query, Pageable pageable) {
        return notificationRepository.findByFilters(
                query.userId(),
                query.type(),
                query.isRead(),
                pageable
        );
    }

    @Override
    public Page<Notification> handle(GetUnreadNotificationsByUserIdQuery query, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(
                query.userId(),
                pageable
        );
    }
}
