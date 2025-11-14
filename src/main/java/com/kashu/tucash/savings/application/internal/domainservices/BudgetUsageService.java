package com.kashu.tucash.savings.application.internal.domainservices;

import com.kashu.tucash.notifications.domain.model.commands.CreateNotificationCommand;
import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;
import com.kashu.tucash.notifications.domain.services.NotificationCommandService;
import com.kashu.tucash.reminders.domain.model.commands.CreateReminderCommand;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderFrequency;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import com.kashu.tucash.reminders.domain.services.ReminderCommandService;
import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories.BudgetRepository;
import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Synchronizes expense transactions with active budgets and emits alerts/reminders when thresholds are crossed.
 */
@Service
public class BudgetUsageService {

    private static final BigDecimal WARNING_THRESHOLD = BigDecimal.valueOf(0.85);
    private static final BigDecimal LIMIT_THRESHOLD = BigDecimal.ONE;

    private final BudgetRepository budgetRepository;
    private final NotificationCommandService notificationCommandService;
    private final ReminderCommandService reminderCommandService;

    public BudgetUsageService(BudgetRepository budgetRepository,
                              NotificationCommandService notificationCommandService,
                              ReminderCommandService reminderCommandService) {
        this.budgetRepository = budgetRepository;
        this.notificationCommandService = notificationCommandService;
        this.reminderCommandService = reminderCommandService;
    }

    @Transactional
    public void registerExpenseCreation(Transaction transaction) {
        applyDelta(transaction.getUser().getId(),
                transaction.getCategory().getId(),
                transaction.getTransactionDate(),
                transaction.getAmount(),
                transaction.getId());
    }

    @Transactional
    public void registerExpenseRemoval(Transaction transaction) {
        applyDelta(transaction.getUser().getId(),
                transaction.getCategory().getId(),
                transaction.getTransactionDate(),
                transaction.getAmount().negate(),
                transaction.getId());
    }

    @Transactional
    public void registerExpenseUpdate(Transaction transaction,
                                      Long previousCategoryId,
                                      LocalDate previousDate,
                                      BigDecimal previousAmount) {
        // revert previous allocation
        applyDelta(transaction.getUser().getId(),
                previousCategoryId,
                previousDate,
                previousAmount.negate(),
                transaction.getId());

        // apply new allocation
        applyDelta(transaction.getUser().getId(),
                transaction.getCategory().getId(),
                transaction.getTransactionDate(),
                transaction.getAmount(),
                transaction.getId());
    }

    private void applyDelta(Long userId,
                            Long categoryId,
                            LocalDate transactionDate,
                            BigDecimal delta,
                            Long transactionId) {
        if (categoryId == null || delta == null || delta.compareTo(BigDecimal.ZERO) == 0) return;

        List<Budget> budgets = budgetRepository
                .findActiveByUserIdAndCategoryId(userId, categoryId, transactionDate);

        for (Budget budget : budgets) {
            BigDecimal previousSpent = budget.getSpentAmount();
            BigDecimal newSpent = previousSpent.add(delta);
            if (newSpent.compareTo(BigDecimal.ZERO) < 0) {
                newSpent = BigDecimal.ZERO;
            }
            budget.setSpentAmount(newSpent);
            budgetRepository.save(budget);
            evaluateAlerts(budget, previousSpent, transactionId);
        }
    }

    private void evaluateAlerts(Budget budget, BigDecimal previousSpent, Long transactionId) {
        if (budget.getLimitAmount() == null || budget.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal previousRatio = ratio(previousSpent, budget.getLimitAmount());
        BigDecimal currentRatio = ratio(budget.getSpentAmount(), budget.getLimitAmount());

        if (crossed(previousRatio, currentRatio, WARNING_THRESHOLD)) {
            notificationCommandService.handle(new CreateNotificationCommand(
                    budget.getUser().getId(),
                    "Presupuesto en riesgo",
                    "Estas a punto de alcanzar el 85% del presupuesto para "
                            + budget.getCategory().getName(),
                    NotificationType.BUDGET_ALERT,
                    "Budget",
                    budget.getId()
            ));
        }

        if (crossed(previousRatio, currentRatio, LIMIT_THRESHOLD)) {
            notificationCommandService.handle(new CreateNotificationCommand(
                    budget.getUser().getId(),
                    "Presupuesto excedido",
                    "Superaste el 100% del presupuesto asignado a "
                            + budget.getCategory().getName(),
                    NotificationType.BUDGET_ALERT,
                    "Budget",
                    budget.getId()
            ));

            reminderCommandService.handle(new CreateReminderCommand(
                    budget.getUser().getId(),
                    "Revisa tu presupuesto " + budget.getCategory().getName(),
                    "Has excedido tu presupuesto, ajusta tus gastos o redefine el monto.",
                    ReminderType.BUDGET,
                    LocalDate.now(),
                    LocalDateTime.now().plusMinutes(5),
                    ReminderFrequency.ONCE,
                    transactionId
            ));
        }
    }

    private BigDecimal ratio(BigDecimal spent, BigDecimal limit) {
        if (limit.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return spent.divide(limit, 4, RoundingMode.HALF_UP);
    }

    private boolean crossed(BigDecimal previousRatio, BigDecimal currentRatio, BigDecimal threshold) {
        return previousRatio.compareTo(threshold) < 0 && currentRatio.compareTo(threshold) >= 0;
    }
}
