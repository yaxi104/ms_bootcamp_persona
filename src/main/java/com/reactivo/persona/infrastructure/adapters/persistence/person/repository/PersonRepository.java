package com.reactivo.persona.infrastructure.adapters.persistence.person.repository;

import com.reactivo.persona.infrastructure.adapters.persistence.person.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<PersonEntity, Long> {
    Mono<PersonEntity> findByEmail(String email);
}
