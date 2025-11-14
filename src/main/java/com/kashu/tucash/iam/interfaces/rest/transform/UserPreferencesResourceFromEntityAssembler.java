package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.interfaces.rest.resources.UserPreferencesResource;

public class UserPreferencesResourceFromEntityAssembler {
    public static UserPreferencesResource toResourceFromEntity(User user) {
        return new UserPreferencesResource(
                user.getCurrency(),
                user.getTheme(),
                user.getLocale(),
                user.getNotificationsEnabled(),
                user.getEmailNotifications(),
                user.getPushNotifications()
        );
    }
}
