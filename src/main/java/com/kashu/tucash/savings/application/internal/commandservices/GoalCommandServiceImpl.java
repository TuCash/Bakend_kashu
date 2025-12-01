package com.kashu.tucash.savings.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.savings.domain.services.GoalCommandService;
import com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories.GoalRepository;
import com.kashu.tucash.transactions.domain.model.aggregates.Category;
import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.domain.model.commands.CreateTransactionCommand;
import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.AccountRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GoalCommandServiceImpl implements GoalCommandService {

    private static final String SAVINGS_CATEGORY_NAME = "Ahorro";

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public GoalCommandServiceImpl(
            GoalRepository goalRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Goal> handle(CreateGoalCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var goal = new Goal(command, user);
        var createdGoal = goalRepository.save(goal);
        return Optional.of(createdGoal);
    }

    @Override
    public Optional<Goal> handle(UpdateGoalCommand command) {
        var goal = goalRepository.findById(command.goalId())
                .orElseThrow(() -> new IllegalArgumentException("Meta no encontrada"));

        if (command.name() != null) goal.setName(command.name());
        if (command.description() != null) goal.setDescription(command.description());
        if (command.targetAmount() != null) goal.setTargetAmount(command.targetAmount());
        if (command.deadline() != null) goal.setDeadline(command.deadline());

        var updatedGoal = goalRepository.save(goal);
        return Optional.of(updatedGoal);
    }

    @Override
    public Optional<Goal> handle(UpdateGoalProgressCommand command) {
        var goal = goalRepository.findById(command.goalId())
                .orElseThrow(() -> new IllegalArgumentException("Meta no encontrada"));

        goal.updateProgress(command);
        var updatedGoal = goalRepository.save(goal);
        return Optional.of(updatedGoal);
    }

    @Override
    public Optional<Goal> handle(CelebrateGoalCommand command) {
        var goal = goalRepository.findById(command.goalId())
                .orElseThrow(() -> new IllegalArgumentException("Meta no encontrada"));

        goal.celebrate();
        var updatedGoal = goalRepository.save(goal);
        return Optional.of(updatedGoal);
    }

    @Override
    public void handle(DeleteGoalCommand command) {
        goalRepository.deleteById(command.goalId());
    }

    @Override
    @Transactional
    public Optional<Transaction> handle(ContributeToGoalCommand command) {
        // Validar entidades
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var goal = goalRepository.findById(command.goalId())
                .orElseThrow(() -> new IllegalArgumentException("Meta no encontrada"));

        var account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        // Obtener o crear la categoría de ahorro
        var savingsCategory = getOrCreateSavingsCategory();

        // Crear la transacción (EXPENSE - descuenta de la cuenta)
        var createTransactionCommand = new CreateTransactionCommand(
                command.userId(),
                command.accountId(),
                savingsCategory.getId(),
                TransactionType.EXPENSE,
                command.amount(),
                command.description() != null ? command.description() : "Aporte a meta: " + goal.getName(),
                LocalDate.now()
        );

        var transaction = new Transaction(createTransactionCommand, user, account, savingsCategory);
        transaction.setLinkedGoal(goal);

        var savedTransaction = transactionRepository.save(transaction);

        // Actualizar el progreso de la meta
        goal.addProgress(command.amount());
        goalRepository.save(goal);

        return Optional.of(savedTransaction);
    }

    @Override
    @Transactional
    public Optional<Transaction> handle(RevertGoalContributionCommand command) {
        // Validar entidades
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var goal = goalRepository.findById(command.goalId())
                .orElseThrow(() -> new IllegalArgumentException("Meta no encontrada"));

        var originalTransaction = transactionRepository.findById(command.transactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));

        // Validar que la transacción pertenece a la meta
        if (originalTransaction.getLinkedGoal() == null ||
                !originalTransaction.getLinkedGoal().getId().equals(command.goalId())) {
            throw new IllegalArgumentException("La transacción no pertenece a esta meta");
        }

        var account = originalTransaction.getAccount();
        var savingsCategory = getOrCreateSavingsCategory();

        // Crear transacción de devolución (INCOME - devuelve a la cuenta)
        var revertTransactionCommand = new CreateTransactionCommand(
                command.userId(),
                account.getId(),
                savingsCategory.getId(),
                TransactionType.INCOME,
                originalTransaction.getAmount(),
                "Devolución de aporte: " + goal.getName(),
                LocalDate.now()
        );

        var revertTransaction = new Transaction(revertTransactionCommand, user, account, savingsCategory);
        revertTransaction.setLinkedGoal(goal);

        var savedTransaction = transactionRepository.save(revertTransaction);

        // Restar del progreso de la meta
        goal.subtractProgress(originalTransaction.getAmount());
        goalRepository.save(goal);

        // Marcar la transacción original como revertida (opcional: eliminarla)
        originalTransaction.setLinkedGoal(null);
        transactionRepository.save(originalTransaction);

        return Optional.of(savedTransaction);
    }

    @Override
    public List<Transaction> getGoalContributions(Long goalId) {
        return transactionRepository.findByLinkedGoalIdOrderByTransactionDateDesc(goalId);
    }

    private Category getOrCreateSavingsCategory() {
        return categoryRepository.findByNameAndUserIsNull(SAVINGS_CATEGORY_NAME)
                .orElseGet(() -> {
                    var newCategory = new Category(
                            SAVINGS_CATEGORY_NAME,
                            CategoryType.EXPENSE,
                            "savings",
                            "#4CAF50"
                    );
                    return categoryRepository.save(newCategory);
                });
    }
}
