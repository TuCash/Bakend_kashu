package com.kashu.tucash.reminders.application.internal.queryservices;

import com.kashu.tucash.reminders.domain.model.aggregates.Reminder;
import com.kashu.tucash.reminders.domain.model.queries.GetActiveRemindersQuery;
import com.kashu.tucash.reminders.domain.model.queries.GetAllRemindersByUserIdQuery;
import com.kashu.tucash.reminders.domain.model.queries.GetReminderByIdQuery;
import com.kashu.tucash.reminders.domain.services.ReminderQueryService;
import com.kashu.tucash.reminders.infrastructure.persistence.jpa.repositories.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReminderQueryServiceImpl implements ReminderQueryService {

    private final ReminderRepository reminderRepository;

    public ReminderQueryServiceImpl(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public Optional<Reminder> handle(GetReminderByIdQuery query) {
        return reminderRepository.findById(query.reminderId());
    }

    @Override
    public List<Reminder> handle(GetAllRemindersByUserIdQuery query) {
        return reminderRepository.findByFilters(
                query.userId(),
                query.type(),
                query.isActive()
        );
    }

    @Override
    public List<Reminder> handle(GetActiveRemindersQuery query) {
        return reminderRepository.findByUserIdAndIsActiveTrueAndIsNotifiedFalseOrderByReminderDateAscReminderTimeAsc(
                query.userId()
        );
    }
}
