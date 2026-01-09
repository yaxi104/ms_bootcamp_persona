package com.reactivo.persona.infrastructure.entrypoints.mapper;

import com.reactivo.persona.domain.model.LoginRequest;
import com.reactivo.persona.infrastructure.entrypoints.dto.request.LoginRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginRequestMapper {
    LoginRequest loginRequestDTOToLoginRequest(LoginRequestDTO dto);
}