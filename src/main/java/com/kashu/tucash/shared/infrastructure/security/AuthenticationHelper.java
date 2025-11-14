package com.kashu.tucash.shared.infrastructure.security;

import com.kashu.tucash.iam.domain.model.queries.GetUserByEmailQuery;
import com.kashu.tucash.iam.domain.services.UserQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    private final UserQueryService userQueryService;

    public AuthenticationHelper(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            var query = new GetUserByEmailQuery(email);
            var user = userQueryService.handle(query);

            if (user.isEmpty()) {
                throw new IllegalStateException("Usuario no encontrado");
            }

            return user.get().getId();
        }

        throw new IllegalStateException("Principal no es un UserDetails");
    }
}
