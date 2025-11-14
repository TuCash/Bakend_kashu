package com.kashu.tucash.iam.interfaces.rest.resources;

public record UpdateUserResource(
        String displayName,
        String photoUrl,
        String currency,
        String theme,
        String locale
) {
}
