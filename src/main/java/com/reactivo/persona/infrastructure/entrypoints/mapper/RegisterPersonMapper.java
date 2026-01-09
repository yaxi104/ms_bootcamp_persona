package com.reactivo.persona.infrastructure.entrypoints.mapper;

import com.reactivo.persona.domain.model.RegisterPerson;
import com.reactivo.persona.infrastructure.entrypoints.dto.request.RegisterPersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterPersonMapper {

    RegisterPerson registerPersonDTOToRegisterPerson(RegisterPersonDTO personDTO);
}
