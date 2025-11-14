package com.kashu.tucash.dashboard.interfaces.rest.resources;

import java.util.List;

public record CategoryLeaksResource(
        String currency,
        String period,
        List<CategoryLeakResource> leaks
) {
}
