package com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson;

import com.reactivo.persona.domain.model.BootcampPerson;
import com.reactivo.persona.domain.spi.BootcampPersonPersistencePort;
import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.mapper.BootcampPersonEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.repository.BootcampPersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class BootcampPersonPersistenceAdapter implements BootcampPersonPersistencePort {

    private final BootcampPersonRepository bootcampPersonRepository;
    private final BootcampPersonEntityMapper bootcampPersonEntityMapper;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Flux<BootcampPerson> saveAll(Flux<BootcampPerson> bootcampPersonFlux) {
        return transactionalOperator.execute(status ->
                bootcampPersonFlux
                        .map(bootcampPersonEntityMapper::toEntity)
                        .as(bootcampPersonRepository::saveAll)
        ).map(bootcampPersonEntityMapper::toModel);
    }

    @Override
    public Flux<Long> findAllIdBootcampByIdPerson(Long idPerson) {
        return bootcampPersonRepository.findIdBootcampByIdPerson(idPerson);
    }

}
