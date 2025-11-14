package com.kashu.tucash.iam.interfaces.rest.resources;

import java.util.Date;

public record UserResource(
        Long id,
        String email,
        String displayName,
        String photoUrl,
        String currency,
        String theme,
        String locale,
        Date createdAt,
        Date updatedAt
) {
}
