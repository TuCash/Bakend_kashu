package com.kashu.tucash.iam.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface BearerTokenService {
    String getBearerTokenFrom(HttpServletRequest request);
    String generateToken(Authentication authentication);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
