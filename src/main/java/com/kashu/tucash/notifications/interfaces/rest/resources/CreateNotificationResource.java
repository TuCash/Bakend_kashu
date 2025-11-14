package com.kashu.tucash.notifications.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNotificationResource(
        @NotNull(message = "El ID de usuario es requerido")
        Long userId,

        @NotBlank(message = "El titulo es requerido")
        String title,

        @NotBlank(message = "El mensaje es requerido")
        String message,

        @NotNull(message = "El tipo es requerido")
        String type,

        String relatedEntityType,

        Long relatedEntityId
) {
}
