package com.kashu.tucash.savings.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.savings.domain.model.commands.CreateGoalCommand;
import com.kashu.tucash.savings.domain.model.commands.UpdateGoalProgressCommand;
import com.kashu.tucash.savings.domain.model.valueobjects.GoalStatus;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
public class Goal extends AuditableAbstractAggregateRoot<Goal> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String name;

    @Setter
    @Column(length = 500)
    private String description;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    @Setter
    private BigDecimal targetAmount;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Setter
    @Column(nullable = false)
    private LocalDate deadline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.ACTIVE;

    @Setter
    private Date celebratedAt;

    protected Goal() {}

    public Goal(CreateGoalCommand command, User user) {
        this.user = user;
        this.name = command.name();
        this.description = command.description();
        this.targetAmount = command.targetAmount();
        this.currentAmount = BigDecimal.ZERO;
        this.deadline = command.deadline();
        this.status = GoalStatus.ACTIVE;
    }

    public void addProgress(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        checkIfCompleted();
    }

    public void subtractProgress(BigDecimal amount) {
        this.currentAmount = this.currentAmount.subtract(amount);
        if (this.currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.currentAmount = BigDecimal.ZERO;
        }
        // Si estaba completada y ahora no, volver a activa
        if (this.status == GoalStatus.COMPLETED && this.currentAmount.compareTo(this.targetAmount) < 0) {
            this.status = GoalStatus.ACTIVE;
            this.celebratedAt = null;
        }
    }

    public void updateProgress(UpdateGoalProgressCommand command) {
        this.currentAmount = command.currentAmount();
        checkIfCompleted();
    }

    public void cancel() {
        this.status = GoalStatus.CANCELLED;
    }

    public void celebrate() {
        if (this.status == GoalStatus.COMPLETED && this.celebratedAt == null) {
            this.celebratedAt = new Date();
        }
    }

    private void checkIfCompleted() {
        if (this.currentAmount.compareTo(this.targetAmount) >= 0 && this.status == GoalStatus.ACTIVE) {
            this.status = GoalStatus.COMPLETED;
        }
    }

    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
