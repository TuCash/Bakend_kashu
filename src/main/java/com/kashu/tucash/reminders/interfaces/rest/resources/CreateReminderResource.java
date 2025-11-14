package com.kashu.tucash.reminders.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateReminderResource(
        @NotBlank(message = "El título es requerido")
        String title,

        String description,

        @NotNull(message = "El tipo es requerido")
        String type,

        @NotNull(message = "La fecha del recordatorio es requerida")
        LocalDate reminderDate,

        LocalDateTime reminderTime,

        @NotNull(message = "La frecuencia es requerida")
        String frequency,

        Long relatedEntityId
) {
}
