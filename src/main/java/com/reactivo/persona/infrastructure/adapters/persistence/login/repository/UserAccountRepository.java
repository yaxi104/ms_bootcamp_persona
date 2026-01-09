package com.reactivo.persona.infrastructure.adapters.persistence.login.repository;

import com.reactivo.persona.infrastructure.adapters.persistence.login.entity.UserAccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserAccountRepository extends ReactiveCrudRepository<UserAccountEntity, Long> {
    Mono<Boolean> existsByEmail(String email);

    Mono<UserAccountEntity> findByEmail(String email);

}
