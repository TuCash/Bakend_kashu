package com.kashu.tucash.iam.domain.model.commands;

public record UpdateUserPreferencesCommand(
        Long userId,
        String currency,
        String theme,
        String locale,
        Boolean notificationsEnabled,
        Boolean emailNotifications,
        Boolean pushNotifications
) {
}
