package com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.mapper;

import com.reactivo.persona.domain.model.BootcampPerson;
import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.entity.BootcampPersonEntity;
import org.mapstruct.Mapper;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface BootcampPersonEntityMapper {

    default BootcampPerson toModel(BootcampPersonEntity entity) {
        Objects.requireNonNull(entity, "BootcampPersonEntity no puede ser null");
        return new BootcampPerson(
                entity.getId(),
                entity.getIdPerson(),
                entity.getIdBootcamp()
        );
    }

    default BootcampPersonEntity toEntity(BootcampPerson bootcampPerson) {
        Objects.requireNonNull(bootcampPerson, "BootcampPerson no puede ser null");
        return new BootcampPersonEntity(
                bootcampPerson.id(),
                bootcampPerson.idPerson(),
                bootcampPerson.idBootcamp()
        );
    }
}
