package com.reactivo.persona.domain.usecase;

import com.reactivo.persona.domain.api.BootcampPersonServicePort;
import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.Bootcamp;
import com.reactivo.persona.domain.model.BootcampPerson;
import com.reactivo.persona.domain.model.RegisterBootcampPerson;
import com.reactivo.persona.domain.spi.BootcampClientPort;
import com.reactivo.persona.domain.spi.BootcampPersonPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BootcampPersonUseCase implements BootcampPersonServicePort {

    private final BootcampPersonPersistencePort bootcampPersonPersistencePort;
    private final BootcampClientPort bootcampClientPort;

    public BootcampPersonUseCase(
            BootcampPersonPersistencePort bootcampPersonPersistencePort,
            BootcampClientPort bootcampClientPort) {
        this.bootcampPersonPersistencePort = bootcampPersonPersistencePort;
        this.bootcampClientPort = bootcampClientPort;
    }

    @Override
    public Flux<BootcampPerson> saveAllBootcampPerson(Mono<RegisterBootcampPerson> registerMono) {
        return registerMono.flatMapMany(this::prepareBootcamps);
    }

    private Flux<BootcampPerson> prepareBootcamps(RegisterBootcampPerson request) {
        validateRequest(request);

        Long personId = request.idPerson();

        return bootcampPersonPersistencePort.findAllIdBootcampByIdPerson(personId)
                .collectList()
                .flatMapMany(existingIdsList -> processNewBootcamps(personId, request.idBootcamps(), existingIdsList));
    }

    private void validateRequest(RegisterBootcampPerson request) {
        if (request.idPerson() == null || request.idBootcamps() == null || request.idBootcamps().isEmpty()) {
            throw new BusinessException(TechnicalMessage.INVALID_PARAMETERS);
        }
    }

    private Flux<BootcampPerson> processNewBootcamps(Long personId, List<Long> requestedIds, List<Long> existingIdsList) {
        Set<Long> existingIds = new HashSet<>(existingIdsList);
        Set<Long> newIds = requestedIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toSet());

        if (newIds.isEmpty()) {
            return Flux.empty();
        }

        if (existingIds.size() + newIds.size() > 5) {
            return Flux.error(new BusinessException(TechnicalMessage.BOOTCAMP_LIMIT_EXCEEDED));
        }

        Set<Long> idsToFetch = new HashSet<>(existingIds);
        idsToFetch.addAll(newIds);

        return bootcampClientPort.findBootcampsByIds(new ArrayList<>(idsToFetch))
                .collectMap(Bootcamp::id)
                .flatMapMany(bootcampMap -> validateAndMapToBootcampPersons(personId, existingIds, newIds, bootcampMap));
    }

    private Flux<BootcampPerson> validateAndMapToBootcampPersons(Long personId,
                                                                 Set<Long> existingIds,
                                                                 Set<Long> newIds,
                                                                 Map<Long, Bootcamp> bootcampMap) {
        List<Long> validIds = newIds.stream()
                .filter(newId -> existingIds.stream().noneMatch(existId -> {
                    Bootcamp newBootcamp = bootcampMap.get(newId);
                    Bootcamp existBootcamp = bootcampMap.get(existId);
                    return existBootcamp.releaseDate().equals(newBootcamp.releaseDate()) &&
                            existBootcamp.duration().equals(newBootcamp.duration());
                }))
                .toList();

        if (validIds.isEmpty()) {
            return Flux.empty();
        }

        Flux<BootcampPerson> bootcampPersonFlux = Flux.fromIterable(validIds)
                .map(id -> new BootcampPerson(null, personId, id));

        return bootcampPersonPersistencePort.saveAll(bootcampPersonFlux);
    }

}
