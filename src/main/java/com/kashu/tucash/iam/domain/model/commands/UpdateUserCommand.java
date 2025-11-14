package com.kashu.tucash.iam.domain.model.commands;

public record UpdateUserCommand(
        Long userId,
        String displayName,
        String photoUrl,
        String currency,
        String theme,
        String locale
) {
}
