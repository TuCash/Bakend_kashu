package com.kashu.tucash.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpResource(
        @NotBlank(message = "El email es requerido")
        @Email(message = "El email debe ser válido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El nombre es requerido")
        String displayName
) {
}
