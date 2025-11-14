package com.kashu.tucash.notifications.domain.services;

import com.kashu.tucash.notifications.domain.model.aggregates.Notification;
import com.kashu.tucash.notifications.domain.model.queries.GetAllNotificationsByUserIdQuery;
import com.kashu.tucash.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.kashu.tucash.notifications.domain.model.queries.GetUnreadNotificationsByUserIdQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NotificationQueryService {
    Optional<Notification> handle(GetNotificationByIdQuery query);
    Page<Notification> handle(GetAllNotificationsByUserIdQuery query, Pageable pageable);
    Page<Notification> handle(GetUnreadNotificationsByUserIdQuery query, Pageable pageable);
}
