package com.reactivo.persona.infrastructure.adapters.persistence.person;

import com.reactivo.persona.domain.model.Person;
import com.reactivo.persona.domain.spi.PersonPersistencePort;
import com.reactivo.persona.infrastructure.adapters.persistence.person.mapper.PersonEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.person.repository.PersonRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class PersonPersistenceAdapter implements PersonPersistencePort {
    private final PersonRepository personRepository;
    private final PersonEntityMapper personEntityMapper;

    @Override
    public Mono<Person> save(Person person) {
        return personRepository.save(personEntityMapper.toEntity(person))
                .map(personEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByEmail(String email) {
        return personRepository.findByEmail(email)
                .map(personEntityMapper::toModel)
                .map(user -> true)
                .defaultIfEmpty(false);
    }

}
