package com.reactivo.persona.infrastructure.adapters.persistence.login.mapper;

import com.reactivo.persona.domain.model.UserAccount;
import com.reactivo.persona.infrastructure.adapters.persistence.login.entity.UserAccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAccountEntityMapper {
    UserAccount toModel(UserAccountEntity entity);

    UserAccountEntity toEntity(UserAccount userAccount);
}
