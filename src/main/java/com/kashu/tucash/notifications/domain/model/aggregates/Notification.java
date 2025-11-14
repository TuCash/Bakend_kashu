package com.kashu.tucash.notifications.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    @Setter
    private Boolean isRead = false;

    @Setter
    private LocalDateTime readAt;

    @Setter
    private String relatedEntityType;

    @Setter
    private Long relatedEntityId;

    protected Notification() {
    }

    public Notification(User user, String title, String message, NotificationType type) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
    }

    public Notification(User user, String title, String message, NotificationType type,
                       String relatedEntityType, Long relatedEntityId) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.isRead = false;
    }

    public Notification(com.kashu.tucash.notifications.domain.model.commands.CreateNotificationCommand command, User user) {
        this.user = user;
        this.title = command.title();
        this.message = command.message();
        this.type = command.type();
        this.relatedEntityType = command.relatedEntityType();
        this.relatedEntityId = command.relatedEntityId();
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }
}
