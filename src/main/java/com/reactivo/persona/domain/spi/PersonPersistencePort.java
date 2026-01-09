package com.reactivo.persona.domain.spi;

import com.reactivo.persona.domain.model.Person;
import reactor.core.publisher.Mono;

public interface PersonPersistencePort {
    Mono<Person> save(Person person);
    Mono<Boolean> existByEmail(String email);
}
