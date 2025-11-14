package com.kashu.tucash.savings.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.savings.domain.services.GoalCommandService;
import com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GoalCommandServiceImpl implements GoalCommandService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalCommandServiceImpl(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
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
}
