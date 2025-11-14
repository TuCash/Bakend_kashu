package com.kashu.tucash.automation.application.internal.commandservices;

import com.kashu.tucash.automation.domain.model.aggregates.RecurringTransaction;
import com.kashu.tucash.automation.domain.model.commands.ChangeRecurringTransactionStatusCommand;
import com.kashu.tucash.automation.domain.model.commands.CreateRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.commands.DeleteRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.commands.UpdateRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.services.RecurringTransactionCommandService;
import com.kashu.tucash.automation.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.AccountRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RecurringTransactionCommandServiceImpl implements RecurringTransactionCommandService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public RecurringTransactionCommandServiceImpl(
            RecurringTransactionRepository recurringTransactionRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Optional<RecurringTransaction> handle(CreateRecurringTransactionCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        var account = accountRepository.findByIdAndUserId(command.accountId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

        var recurringTransaction = new RecurringTransaction(
                user,
                account,
                category,
                command.type(),
                command.amount(),
                command.description(),
                command.startDate(),
                command.endDate(),
                command.frequency()
        );

        var saved = recurringTransactionRepository.save(recurringTransaction);
        return Optional.of(saved);
    }

    @Override
    @Transactional
    public Optional<RecurringTransaction> handle(UpdateRecurringTransactionCommand command) {
        var recurringTransaction = recurringTransactionRepository.findById(command.recurringTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));

        if (command.amount() != null) {
            recurringTransaction.setAmount(command.amount());
        }
        if (command.description() != null) {
            recurringTransaction.setDescription(command.description());
        }
        if (command.startDate() != null) {
            recurringTransaction.setStartDate(command.startDate());
            recurringTransaction.setNextExecutionDate(command.startDate());
        }
        if (command.endDate() != null) {
            recurringTransaction.setEndDate(command.endDate());
        }
        if (command.frequency() != null) {
            recurringTransaction.setFrequency(command.frequency());
        }

        var updated = recurringTransactionRepository.save(recurringTransaction);
        return Optional.of(updated);
    }

    @Override
    @Transactional
    public Optional<RecurringTransaction> handle(ChangeRecurringTransactionStatusCommand command) {
        var recurringTransaction = recurringTransactionRepository.findById(command.recurringTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Recurring transaction not found"));

        recurringTransaction.setActive(command.active());
        var updated = recurringTransactionRepository.save(recurringTransaction);
        return Optional.of(updated);
    }

    @Override
    @Transactional
    public void handle(DeleteRecurringTransactionCommand command) {
        recurringTransactionRepository.deleteById(command.recurringTransactionId());
    }
}
