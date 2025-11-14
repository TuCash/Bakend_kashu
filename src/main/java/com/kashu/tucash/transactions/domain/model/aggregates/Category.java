package com.kashu.tucash.transactions.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.kashu.tucash.transactions.domain.model.commands.CreateCategoryCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Category extends AuditableAbstractAggregateRoot<Category> {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @Setter
    private String icon;

    @Setter
    private String color;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // null = categoría del sistema (predeterminada)

    protected Category() {}

    public Category(CreateCategoryCommand command, User user) {
        this.name = command.name();
        this.type = command.type();
        this.icon = command.icon();
        this.color = command.color();
        this.user = user;
    }

    // Constructor para categorías del sistema (sin usuario)
    public Category(String name, CategoryType type, String icon, String color) {
        this.name = name;
        this.type = type;
        this.icon = icon;
        this.color = color;
        this.user = null;
    }

    public boolean isSystemCategory() {
        return this.user == null;
    }
}
