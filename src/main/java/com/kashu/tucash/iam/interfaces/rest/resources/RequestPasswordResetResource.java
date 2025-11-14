package com.kashu.tucash.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestPasswordResetResource(
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email")
        String email
) {
}
