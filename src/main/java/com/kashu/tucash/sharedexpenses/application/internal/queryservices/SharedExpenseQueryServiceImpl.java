package com.kashu.tucash.sharedexpenses.application.internal.queryservices;

import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;
import com.kashu.tucash.sharedexpenses.domain.model.queries.*;
import com.kashu.tucash.sharedexpenses.domain.services.SharedExpenseQueryService;
import com.kashu.tucash.sharedexpenses.infrastructure.persistence.jpa.repositories.SharedExpenseParticipantRepository;
import com.kashu.tucash.sharedexpenses.infrastructure.persistence.jpa.repositories.SharedExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SharedExpenseQueryServiceImpl implements SharedExpenseQueryService {

    private final SharedExpenseRepository sharedExpenseRepository;
    private final SharedExpenseParticipantRepository participantRepository;

    public SharedExpenseQueryServiceImpl(
            SharedExpenseRepository sharedExpenseRepository,
            SharedExpenseParticipantRepository participantRepository) {
        this.sharedExpenseRepository = sharedExpenseRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SharedExpense> handle(GetAllSharedExpensesByUserIdQuery query) {
        return sharedExpenseRepository.findAllByUserIdAsCreatorOrParticipant(
                query.userId(),
                query.pageable()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SharedExpense> handle(GetSharedExpenseByIdQuery query) {
        return sharedExpenseRepository.findById(query.sharedExpenseId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SharedExpense> handle(GetSharedExpensesByStatusQuery query) {
        return sharedExpenseRepository.findAllByUserIdAndStatus(
                query.userId(),
                query.status(),
                query.pageable()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SharedExpenseParticipant> handle(GetPendingPaymentsByUserIdQuery query) {
        return participantRepository.findPendingPaymentsByUserId(
                query.userId(),
                query.pageable()
        );
    }
}
