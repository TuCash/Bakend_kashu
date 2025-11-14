package com.kashu.tucash.sharedexpenses.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;
import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SharedExpenseStatus;
import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SplitMethod;
import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shared_expenses")
@Getter
public class SharedExpense extends AuditableAbstractAggregateRoot<SharedExpense> {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String title;

    @Setter
    @Column(length = 1000)
    private String description;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 2)
    @Setter
    private BigDecimal totalAmount;

    @NotNull
    @Column(nullable = false, length = 3)
    @Setter
    private String currency = "PEN";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private Category category;

    @NotNull
    @Column(nullable = false)
    @Setter
    private LocalDate expenseDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private SharedExpenseStatus status = SharedExpenseStatus.PENDING;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private SplitMethod splitMethod;

    @Setter
    @Column
    private LocalDateTime settledAt;

    @OneToMany(mappedBy = "sharedExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SharedExpenseParticipant> participants = new ArrayList<>();

    protected SharedExpense() {}

    public SharedExpense(
            User creator,
            String title,
            String description,
            BigDecimal totalAmount,
            String currency,
            Category category,
            LocalDate expenseDate,
            SplitMethod splitMethod) {
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.totalAmount = totalAmount;
        this.currency = currency != null ? currency : "PEN";
        this.category = category;
        this.expenseDate = expenseDate;
        this.splitMethod = splitMethod;
        this.status = SharedExpenseStatus.PENDING;
    }

    public void settle() {
        if (this.status == SharedExpenseStatus.CANCELLED) {
            throw new IllegalStateException("Cannot settle a cancelled expense");
        }
        this.status = SharedExpenseStatus.COMPLETED;
        this.settledAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == SharedExpenseStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed expense");
        }
        this.status = SharedExpenseStatus.CANCELLED;
    }

    public void addParticipant(SharedExpenseParticipant participant) {
        if (this.status != SharedExpenseStatus.PENDING) {
            throw new IllegalStateException("Cannot add participants to a non-pending expense");
        }
        this.participants.add(participant);
    }

    public void removeParticipant(SharedExpenseParticipant participant) {
        if (this.status != SharedExpenseStatus.PENDING) {
            throw new IllegalStateException("Cannot remove participants from a non-pending expense");
        }
        this.participants.remove(participant);
    }
}
