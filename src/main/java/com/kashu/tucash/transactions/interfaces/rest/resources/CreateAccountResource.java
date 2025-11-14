package com.kashu.tucash.transactions.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountResource(
        @NotBlank(message = "El nombre es requerido")
        String name,

        @NotBlank(message = "La moneda es requerida")
        String currency
) {
}
