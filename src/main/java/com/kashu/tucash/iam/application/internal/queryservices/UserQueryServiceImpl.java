package com.kashu.tucash.iam.application.internal.queryservices;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.domain.model.queries.GetUserByEmailQuery;
import com.kashu.tucash.iam.domain.model.queries.GetUserByIdQuery;
import com.kashu.tucash.iam.domain.services.UserQueryService;
import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }
}
