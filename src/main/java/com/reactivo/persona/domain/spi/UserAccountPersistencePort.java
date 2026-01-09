package com.reactivo.persona.domain.spi;

import com.reactivo.persona.domain.model.UserAccount;
import reactor.core.publisher.Mono;

public interface UserAccountPersistencePort {
    Mono<UserAccount> save(UserAccount userAccount);

    Mono<Boolean> existByEmail(String email);

    Mono<UserAccount> findByEmail(String email);
}
