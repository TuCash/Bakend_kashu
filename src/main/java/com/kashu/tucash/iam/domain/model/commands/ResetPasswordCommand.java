package com.kashu.tucash.iam.domain.model.commands;

public record ResetPasswordCommand(
        String token,
        String newPassword
) {
}
