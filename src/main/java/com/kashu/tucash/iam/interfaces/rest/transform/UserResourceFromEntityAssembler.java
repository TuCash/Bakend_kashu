package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
                entity.getId(),
                entity.getEmail(),
                entity.getDisplayName(),
                entity.getPhotoUrl(),
                entity.getCurrency(),
                entity.getTheme(),
                entity.getLocale(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
