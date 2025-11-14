package com.kashu.tucash.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(
        Long id,
        String email,
        String displayName,
        String token
) {
}
