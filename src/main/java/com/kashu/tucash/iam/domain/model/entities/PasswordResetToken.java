package com.kashu.tucash.iam.domain.model.entities;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    @Setter
    private Boolean used = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected PasswordResetToken() {
    }

    public PasswordResetToken(User user, int expirationMinutes) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiryDate = LocalDateTime.now().plusMinutes(expirationMinutes);
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    public boolean isValid() {
        return !this.used && !isExpired();
    }

    public void markAsUsed() {
        this.used = true;
    }
}
