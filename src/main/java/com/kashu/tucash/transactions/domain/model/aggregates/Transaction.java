package com.kashu.tucash.transactions.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.commands.UpdateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "transactions")
public class Transaction extends AuditableAbstractAggregateRoot<Transaction> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    @Setter
    private Account account;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    @Setter
    private BigDecimal amount;

    @Setter
    @Column(length = 500)
    private String description;

    @NotNull
    @Column(nullable = false)
    @Setter
    private LocalDate transactionDate;

    // Para gastos compartidos (Release 3) - JSON metadata
    @Setter
    @Column(length = 2000)
    private String sharedSplitMetadata;

    // Para contribuciones a metas de ahorro
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_id")
    @Setter
    private Goal linkedGoal;

    protected Transaction() {}

    public Transaction(CreateTransactionCommand command, User user, Account account, Category category) {
        this.user = user;
        this.account = account;
        this.category = category;
        this.type = command.type();
        this.amount = command.amount();
        this.description = command.description();
        this.transactionDate = command.transactionDate();

        // Actualizar balance de la cuenta
        updateAccountBalance(command.type(), command.amount());
    }

    public void update(UpdateTransactionCommand command, Account newAccount, Category newCategory) {
        // Revertir el balance anterior
        revertAccountBalance();

        // Actualizar campos
        if (newAccount != null) {
            this.account = newAccount;
        }
        if (newCategory != null) {
            this.category = newCategory;
        }
        if (command.amount() != null) {
            this.amount = command.amount();
        }
        if (command.description() != null) {
            this.description = command.description();
        }
        if (command.transactionDate() != null) {
            this.transactionDate = command.transactionDate();
        }

        // Aplicar nuevo balance
        updateAccountBalance(this.type, this.amount);
    }

    private void updateAccountBalance(TransactionType type, BigDecimal amount) {
        switch (type) {
            case INCOME:
                account.addToBalance(amount);
                break;
            case EXPENSE:
                account.subtractFromBalance(amount);
                break;
            case TRANSFER:
                // Para transferencias, manejar en el servicio
                break;
        }
    }

    private void revertAccountBalance() {
        switch (this.type) {
            case INCOME:
                account.subtractFromBalance(this.amount);
                break;
            case EXPENSE:
                account.addToBalance(this.amount);
                break;
            case TRANSFER:
                break;
        }
    }
}
