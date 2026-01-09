package com.reactivo.persona.domain.api;

import com.reactivo.persona.domain.model.LoginRequest;
import com.reactivo.persona.domain.model.LoginResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationServicePort {
    Mono<LoginResponse> login(LoginRequest loginRequest);
}