package com.reactivo.persona.domain.spi;

import com.reactivo.persona.domain.model.BootcampPerson;
import reactor.core.publisher.Flux;

public interface BootcampPersonPersistencePort {
    Flux<BootcampPerson> saveAll(Flux<BootcampPerson> bootcampPersonFlux);

    Flux<Long> findAllIdBootcampByIdPerson(Long idPerson);

}
