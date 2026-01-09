package com.reactivo.persona.domain.api;

import com.reactivo.persona.domain.model.Person;
import com.reactivo.persona.domain.model.RegisterPerson;
import reactor.core.publisher.Mono;

public interface PersonServicePort {
    Mono<Person> registerPerson(RegisterPerson person);
}
