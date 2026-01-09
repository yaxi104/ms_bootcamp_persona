package com.reactivo.persona.infrastructure.entrypoints.mapper;

import com.reactivo.persona.domain.model.LoginRequest;
import com.reactivo.persona.infrastructure.entrypoints.dto.request.LoginRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginRequestMapper {
    LoginRequest loginRequestDTOToLoginRequest(LoginRequestDTO dto);
}