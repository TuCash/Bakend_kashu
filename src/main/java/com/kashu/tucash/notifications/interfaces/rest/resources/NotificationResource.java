package com.kashu.tucash.notifications.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.Date;

public record NotificationResource(
        Long id,
        Long userId,
        String title,
        String message,
        String type,
        Boolean isRead,
        LocalDateTime readAt,
        String relatedEntityType,
        Long relatedEntityId,
        Date createdAt,
        Date updatedAt
) {
}
