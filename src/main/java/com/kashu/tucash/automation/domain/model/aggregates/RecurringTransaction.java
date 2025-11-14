package com.kashu.tucash.automation.domain.model.aggregates;

import com.kashu.tucash.automation.domain.model.valueobjects.RecurringFrequency;
import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.kashu.tucash.transactions.domain.model.aggregates.Account;
import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_transactions")
@Getter
public class RecurringTransaction extends AuditableAbstractAggregateRoot<RecurringTransaction> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
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
    private LocalDate startDate;

    @Column
    @Setter
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private RecurringFrequency frequency;

    @NotNull
    @Column(nullable = false)
    @Setter
    private LocalDate nextExecutionDate;

    @NotNull
    @Column(nullable = false)
    @Setter
    private Boolean active = true;

    protected RecurringTransaction() {
    }

    public RecurringTransaction(User user,
                                Account account,
                                Category category,
                                TransactionType type,
                                BigDecimal amount,
                                String description,
                                LocalDate startDate,
                                LocalDate endDate,
                                RecurringFrequency frequency) {
        this.user = user;
        this.account = account;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.nextExecutionDate = startDate != null ? startDate : LocalDate.now();
        this.active = true;
    }

    public boolean shouldExecute(LocalDate referenceDate) {
        if (!Boolean.TRUE.equals(active)) return false;
        if (nextExecutionDate == null) return false;
        if (referenceDate.isBefore(nextExecutionDate)) return false;
        if (endDate != null && referenceDate.isAfter(endDate)) return false;
        return true;
    }

    public void scheduleNextExecution() {
        if (nextExecutionDate == null) {
            nextExecutionDate = LocalDate.now();
        }
        switch (frequency) {
            case DAILY -> nextExecutionDate = nextExecutionDate.plusDays(1);
            case WEEKLY -> nextExecutionDate = nextExecutionDate.plusWeeks(1);
            case MONTHLY -> nextExecutionDate = nextExecutionDate.plusMonths(1);
        }
        if (endDate != null && nextExecutionDate.isAfter(endDate)) {
            this.active = false;
        }
    }

    public CreateTransactionCommand toCreateTransactionCommand() {
        return new CreateTransactionCommand(
                this.user.getId(),
                this.account.getId(),
                this.category.getId(),
                this.type,
                this.amount,
                this.description,
                LocalDate.now()
        );
    }
}
