package com.kashu.tucash.iam.domain.model.commands;

public record ChangePasswordCommand(
        Long userId,
        String currentPassword,
        String newPassword
) {
}
