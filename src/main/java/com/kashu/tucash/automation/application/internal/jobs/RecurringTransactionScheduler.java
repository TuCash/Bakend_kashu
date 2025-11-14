package com.kashu.tucash.automation.application.internal.jobs;

import com.kashu.tucash.automation.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.services.TransactionCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class RecurringTransactionScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringTransactionScheduler.class);

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionCommandService transactionCommandService;

    public RecurringTransactionScheduler(RecurringTransactionRepository recurringTransactionRepository,
                                         TransactionCommandService transactionCommandService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionCommandService = transactionCommandService;
    }

    @Scheduled(fixedDelayString = "${automation.recurring.fixed-delay-ms:3600000}")
    @Transactional
    public void processRecurringTransactions() {
        var today = LocalDate.now();
        var dueTransactions = recurringTransactionRepository.findDueTransactions(today);
        if (dueTransactions.isEmpty()) return;

        dueTransactions.forEach(recurring -> {
            try {
                CreateTransactionCommand command = recurring.toCreateTransactionCommand();
                transactionCommandService.handle(command);
                recurring.scheduleNextExecution();
                recurringTransactionRepository.save(recurring);
            } catch (Exception ex) {
                LOGGER.error("Failed to materialize recurring transaction {}: {}", recurring.getId(), ex.getMessage());
            }
        });
    }
}
