package com.kashu.tucash.iam.infrastructure.hashing.bcrypt;

import com.kashu.tucash.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
