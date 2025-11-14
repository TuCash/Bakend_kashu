package com.kashu.tucash.notifications.domain.model.queries;

import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;

public record GetAllNotificationsByUserIdQuery(
        Long userId,
        NotificationType type,
        Boolean isRead
) {
}
