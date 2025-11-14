package com.kashu.tucash.iam.interfaces.rest.resources;

public record UpdateUserPreferencesResource(
        String currency,
        String theme,
        String locale,
        Boolean notificationsEnabled,
        Boolean emailNotifications,
        Boolean pushNotifications
) {
}
