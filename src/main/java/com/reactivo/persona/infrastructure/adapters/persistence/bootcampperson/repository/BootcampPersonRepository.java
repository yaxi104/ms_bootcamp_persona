package com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.repository;

import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.entity.BootcampPersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BootcampPersonRepository extends ReactiveCrudRepository<BootcampPersonEntity, Long> {

    Flux<Long> findIdBootcampByIdPerson(Long idPerson);

}
