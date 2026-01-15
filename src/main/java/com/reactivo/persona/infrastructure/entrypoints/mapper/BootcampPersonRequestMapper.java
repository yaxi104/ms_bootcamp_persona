package com.reactivo.persona.infrastructure.entrypoints.mapper;

import com.reactivo.persona.domain.model.RegisterBootcampPerson;
import com.reactivo.persona.infrastructure.entrypoints.dto.request.RegisterBootcampPersonDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BootcampPersonRequestMapper {
    RegisterBootcampPerson toDomain(RegisterBootcampPersonDTO dto);
}