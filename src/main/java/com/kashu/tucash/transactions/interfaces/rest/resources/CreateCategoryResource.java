package com.kashu.tucash.transactions.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryResource(
        @NotBlank(message = "El nombre es requerido")
        String name,

        @NotNull(message = "El tipo es requerido")
        String type,

        String icon,
        String color
) {
}
