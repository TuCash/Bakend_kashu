package com.kashu.tucash.iam.application.internal.commandservices;

import com.kashu.tucash.iam.application.internal.outboundservices.hashing.HashingService;
import com.kashu.tucash.iam.application.internal.outboundservices.tokens.TokenService;
import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.domain.model.commands.*;
import com.kashu.tucash.iam.domain.model.entities.PasswordResetToken;
import com.kashu.tucash.iam.domain.services.UserCommandService;
import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.PasswordResetTokenRepository;
import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  HashingService hashingService,
                                  TokenService tokenService,
                                  PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        var user = new User(
                command.email(),
                hashingService.encode(command.password()),
                command.displayName()
        );

        var createdUser = userRepository.save(user);
        return Optional.of(createdUser);
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        var token = tokenService.generateToken(user.get().getEmail());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    public Optional<User> handle(UpdateUserCommand command) {
        var user = userRepository.findById(command.userId());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        user.get().update(command);
        var updatedUser = userRepository.save(user.get());
        return Optional.of(updatedUser);
    }

    @Override
    public Optional<User> handle(UpdateUserPreferencesCommand command) {
        var user = userRepository.findById(command.userId());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        user.get().updatePreferences(command);
        var updatedUser = userRepository.save(user.get());
        return Optional.of(updatedUser);
    }

    @Override
    @Transactional
    public Optional<User> handle(ChangePasswordCommand command) {
        var user = userRepository.findById(command.userId());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validate current password
        if (!hashingService.matches(command.currentPassword(), user.get().getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Update with new password
        user.get().setPassword(hashingService.encode(command.newPassword()));
        var updatedUser = userRepository.save(user.get());
        return Optional.of(updatedUser);
    }

    @Override
    @Transactional
    public Optional<String> handle(RequestPasswordResetCommand command) {
        var user = userRepository.findByEmail(command.email());

        if (user.isEmpty()) {
            // For security reasons, don't reveal if email exists or not
            // Return success anyway to prevent email enumeration
            return Optional.of("reset-token-placeholder");
        }

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUserId(user.get().getId());

        // Create new token (valid for 1 hour)
        var resetToken = new PasswordResetToken(user.get(), 60);
        passwordResetTokenRepository.save(resetToken);

        // In a real application, you would send an email here with the reset link
        // For now, we return the token (in production, this should be sent via email)
        return Optional.of(resetToken.getToken());
    }

    @Override
    @Transactional
    public Optional<User> handle(ResetPasswordCommand command) {
        var resetToken = passwordResetTokenRepository.findByToken(command.token());

        if (resetToken.isEmpty()) {
            throw new IllegalArgumentException("Token de reseteo inválido");
        }

        if (!resetToken.get().isValid()) {
            throw new IllegalArgumentException("Token de reseteo expirado o ya utilizado");
        }

        var user = resetToken.get().getUser();
        user.setPassword(hashingService.encode(command.newPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.get().markAsUsed();
        passwordResetTokenRepository.save(resetToken.get());

        return Optional.of(user);
    }
}
