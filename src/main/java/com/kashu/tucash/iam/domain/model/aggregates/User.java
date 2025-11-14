package com.kashu.tucash.iam.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.commands.UpdateUserCommand;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
public class User extends AuditableAbstractAggregateRoot<User> {

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    @Setter
    private String password;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String displayName;

    @Setter
    private String photoUrl;

    @Column(nullable = false)
    @Setter
    private String currency = "PEN";

    @Column(nullable = false)
    @Setter
    private String theme = "light";

    @Column(nullable = false)
    @Setter
    private String locale = "es";

    @Column(nullable = false)
    @Setter
    private Boolean notificationsEnabled = true;

    @Column(nullable = false)
    @Setter
    private Boolean emailNotifications = true;

    @Column(nullable = false)
    @Setter
    private Boolean pushNotifications = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    protected User() {}

    public User(String email, String password, String displayName) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.roles.add("ROLE_USER");
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    public void update(UpdateUserCommand command) {
        if (command.displayName() != null && !command.displayName().isBlank()) {
            this.displayName = command.displayName();
        }
        if (command.photoUrl() != null) {
            this.photoUrl = command.photoUrl();
        }
        if (command.currency() != null && !command.currency().isBlank()) {
            this.currency = command.currency();
        }
        if (command.theme() != null && !command.theme().isBlank()) {
            this.theme = command.theme();
        }
        if (command.locale() != null && !command.locale().isBlank()) {
            this.locale = command.locale();
        }
    }

    public void updatePreferences(com.kashu.tucash.iam.domain.model.commands.UpdateUserPreferencesCommand command) {
        if (command.currency() != null && !command.currency().isBlank()) {
            this.currency = command.currency();
        }
        if (command.theme() != null && !command.theme().isBlank()) {
            this.theme = command.theme();
        }
        if (command.locale() != null && !command.locale().isBlank()) {
            this.locale = command.locale();
        }
        if (command.notificationsEnabled() != null) {
            this.notificationsEnabled = command.notificationsEnabled();
        }
        if (command.emailNotifications() != null) {
            this.emailNotifications = command.emailNotifications();
        }
        if (command.pushNotifications() != null) {
            this.pushNotifications = command.pushNotifications();
        }
    }
}
