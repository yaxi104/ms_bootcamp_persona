package com.reactivo.persona.infrastructure.adapters.persistence.login;

import com.reactivo.persona.domain.model.UserAccount;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import com.reactivo.persona.infrastructure.adapters.persistence.login.mapper.UserAccountEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.login.repository.UserAccountRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserAccountPersistenceAdapter implements UserAccountPersistencePort {
    private final UserAccountRepository userAccountRepository;
    private final UserAccountEntityMapper userAccountEntityMapper;

    @Override
    public Mono<UserAccount> save(UserAccount userAccount) {
        return userAccountRepository.save(userAccountEntityMapper.toEntity(userAccount))
                .map(userAccountEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    @Override
    public Mono<UserAccount> findByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .map(userAccountEntityMapper::toModel);
    }
}
