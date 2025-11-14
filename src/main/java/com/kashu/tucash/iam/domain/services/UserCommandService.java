package com.kashu.tucash.iam.domain.services;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.domain.model.commands.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
    Optional<User> handle(UpdateUserCommand command);
    Optional<User> handle(UpdateUserPreferencesCommand command);
    Optional<User> handle(ChangePasswordCommand command);
    Optional<String> handle(RequestPasswordResetCommand command);
    Optional<User> handle(ResetPasswordCommand command);
}
