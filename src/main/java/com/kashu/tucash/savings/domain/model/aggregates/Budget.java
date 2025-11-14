package com.kashu.tucash.savings.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.savings.domain.model.commands.CreateBudgetCommand;
import com.kashu.tucash.savings.domain.model.valueobjects.BudgetPeriod;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Getter
public class Budget extends AuditableAbstractAggregateRoot<Budget> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private Category category;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    @Setter
    private BigDecimal limitAmount;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private BudgetPeriod period;

    @NotNull
    @Column(nullable = false)
    @Setter
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    @Setter
    private LocalDate endDate;

    protected Budget() {}

    public Budget(CreateBudgetCommand command, User user, Category category) {
        this.user = user;
        this.category = category;
        this.limitAmount = command.limitAmount();
        this.spentAmount = BigDecimal.ZERO;
        this.period = command.period();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
    }

    public void addSpent(BigDecimal amount) {
        this.spentAmount = this.spentAmount.add(amount);
    }

    public void subtractSpent(BigDecimal amount) {
        this.spentAmount = this.spentAmount.subtract(amount);
        if (this.spentAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.spentAmount = BigDecimal.ZERO;
        }
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return limitAmount.subtract(spentAmount);
    }

    public BigDecimal getSpentRatio() {
        if (limitAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spentAmount.divide(limitAmount, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal getSpentPercentage() {
        return getSpentRatio().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isWarning() {
        return getSpentRatio().compareTo(BigDecimal.valueOf(0.85)) >= 0;
    }

    public boolean isExceeded() {
        return getSpentRatio().compareTo(BigDecimal.ONE) >= 0;
    }

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
}
