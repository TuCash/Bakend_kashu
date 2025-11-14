package com.kashu.tucash.sharedexpenses.domain.model.entities;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shared_expense_participants")
@Getter
public class SharedExpenseParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_expense_id", nullable = false)
    private SharedExpense sharedExpense;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    @Setter
    private BigDecimal amountOwed;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    @Setter
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    @Setter
    private Boolean isPaid = false;

    @Setter
    @Column
    private LocalDateTime paidAt;

    protected SharedExpenseParticipant() {}

    public SharedExpenseParticipant(SharedExpense sharedExpense, User participant, BigDecimal amountOwed) {
        this.sharedExpense = sharedExpense;
        this.participant = participant;
        this.amountOwed = amountOwed;
        this.amountPaid = BigDecimal.ZERO;
        this.isPaid = false;
    }

    public void markAsPaid() {
        this.isPaid = true;
        this.amountPaid = this.amountOwed;
        this.paidAt = LocalDateTime.now();
    }

    public BigDecimal getBalanceDue() {
        return this.amountOwed.subtract(this.amountPaid);
    }
}
