package com.kashu.tucash.transactions.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.kashu.tucash.transactions.domain.model.commands.CreateAccountCommand;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
public class Account extends AuditableAbstractAggregateRoot<Account> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String name;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String currency;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    protected Account() {}

    public Account(CreateAccountCommand command, User user) {
        this.user = user;
        this.name = command.name();
        this.currency = command.currency();
        this.balance = BigDecimal.ZERO;
    }

    public void addToBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
