package com.reactivo.persona.infrastructure.adapters.persistence.person.mapper;

import com.reactivo.persona.domain.model.Person;
import com.reactivo.persona.infrastructure.adapters.persistence.person.entity.PersonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonEntityMapper {
    Person toModel(PersonEntity entity);

    PersonEntity toEntity(Person person);
}
