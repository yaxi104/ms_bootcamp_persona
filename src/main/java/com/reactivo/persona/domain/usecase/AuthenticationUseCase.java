package com.reactivo.persona.domain.usecase;

import com.reactivo.persona.domain.api.AuthenticationServicePort;
import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.LoginRequest;
import com.reactivo.persona.domain.model.LoginResponse;
import com.reactivo.persona.domain.spi.JwtServicePort;
import com.reactivo.persona.domain.spi.PasswordEncoderPort;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import reactor.core.publisher.Mono;

public class AuthenticationUseCase implements AuthenticationServicePort {

    private final UserAccountPersistencePort userAccountPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtServicePort jwtServicePort;

    public AuthenticationUseCase(UserAccountPersistencePort userAccountPersistencePort, PasswordEncoderPort passwordEncoderPort, JwtServicePort jwtServicePort) {
        this.userAccountPersistencePort = userAccountPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtServicePort = jwtServicePort;
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return userAccountPersistencePort.findByEmail(loginRequest.email())
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_CREDENTIALS)))
                .flatMap(user ->
                        passwordEncoderPort.matches(loginRequest.password(), user.password())
                                .flatMap(matches -> {
                                    if (Boolean.FALSE.equals(matches)) {
                                        return Mono.error(new BusinessException(TechnicalMessage.INVALID_CREDENTIALS));
                                    }
                                    String token = jwtServicePort.generateToken(user.email(), user.role());
                                    return Mono.just(new LoginResponse(token, user.role()));
                                })
                );
    }
}