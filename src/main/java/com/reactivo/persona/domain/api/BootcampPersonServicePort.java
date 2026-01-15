package com.reactivo.persona.domain.api;

import com.reactivo.persona.domain.model.BootcampPerson;
import com.reactivo.persona.domain.model.RegisterBootcampPerson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampPersonServicePort {
    Flux<BootcampPerson> saveAllBootcampPerson(Mono<RegisterBootcampPerson> registerBootcampPersonMono);
}
