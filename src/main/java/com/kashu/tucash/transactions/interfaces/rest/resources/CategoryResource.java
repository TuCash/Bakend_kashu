package com.kashu.tucash.transactions.interfaces.rest.resources;

public record CategoryResource(
        Long id,
        String name,
        String type,
        String icon,
        String color,
        boolean isSystemCategory
) {
}
