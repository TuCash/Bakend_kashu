package com.kashu.tucash.transactions.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.savings.application.internal.domainservices.BudgetUsageService;
import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.commands.DeleteTransactionCommand;
import com.kashu.tucash.transactions.domain.model.commands.UpdateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import com.kashu.tucash.transactions.domain.services.TransactionCommandService;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.AccountRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetUsageService budgetUsageService;

    public TransactionCommandServiceImpl(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            BudgetUsageService budgetUsageService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.budgetUsageService = budgetUsageService;
    }

    @Override
    @Transactional
    public Optional<Transaction> handle(CreateTransactionCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var account = accountRepository.findByIdAndUserId(command.accountId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

        var transaction = new Transaction(command, user, account, category);
        var createdTransaction = transactionRepository.save(transaction);

        accountRepository.save(account);
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetUsageService.registerExpenseCreation(transaction);
        }

        return Optional.of(createdTransaction);
    }

    @Override
    @Transactional
    public Optional<Transaction> handle(UpdateTransactionCommand command) {
        var transaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaccion no encontrada"));

        var previousCategoryId = transaction.getCategory().getId();
        var previousAmount = transaction.getAmount();
        var previousDate = transaction.getTransactionDate();

        var account = command.accountId() != null
                ? accountRepository.findById(command.accountId()).orElse(null)
                : null;

        var category = command.categoryId() != null
                ? categoryRepository.findById(command.categoryId()).orElse(null)
                : null;

        transaction.update(command, account, category);

        var updatedTransaction = transactionRepository.save(transaction);

        accountRepository.save(transaction.getAccount());
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetUsageService.registerExpenseUpdate(
                    transaction,
                    previousCategoryId,
                    previousDate,
                    previousAmount
            );
        }

        return Optional.of(updatedTransaction);
    }

    @Override
    @Transactional
    public void handle(DeleteTransactionCommand command) {
        var transaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaccion no encontrada"));

        var account = transaction.getAccount();
        switch (transaction.getType()) {
            case INCOME -> account.subtractFromBalance(transaction.getAmount());
            case EXPENSE -> account.addToBalance(transaction.getAmount());
            case TRANSFER -> {
            }
        }

        accountRepository.save(account);
        if (transaction.getType() == TransactionType.EXPENSE) {
            budgetUsageService.registerExpenseRemoval(transaction);
        }
        transactionRepository.delete(transaction);
    }
}
