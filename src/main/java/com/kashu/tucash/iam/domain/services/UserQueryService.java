package com.kashu.tucash.iam.domain.services;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.iam.domain.model.queries.GetUserByEmailQuery;
import com.kashu.tucash.iam.domain.model.queries.GetUserByIdQuery;

import java.util.Optional;

public interface UserQueryService {
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByEmailQuery query);
}
