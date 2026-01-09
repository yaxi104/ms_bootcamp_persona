package com.reactivo.persona.infrastructure.entrypoints.mapper;

import com.reactivo.persona.domain.model.LoginResponse;
import com.reactivo.persona.infrastructure.entrypoints.dto.response.LoginResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginResponsetMapper {

    LoginResponseDTO loginResponseToLoginResponseDTO(LoginResponse loginResponse);
}
