package com.kashu.tucash.iam.interfaces.rest.resources;

public record UserPreferencesResource(
        String currency,
        String theme,
        String locale,
        Boolean notificationsEnabled,
        Boolean emailNotifications,
        Boolean pushNotifications
) {
}
