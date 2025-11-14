package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User entity, String token) {
        return new AuthenticatedUserResource(
                entity.getId(),
                entity.getEmail(),
                entity.getDisplayName(),
                token
        );
    }
}
