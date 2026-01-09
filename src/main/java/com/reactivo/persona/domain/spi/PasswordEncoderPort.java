package com.reactivo.persona.domain.spi;

import reactor.core.publisher.Mono;

public interface PasswordEncoderPort {

    Mono<String> encode(String rawPassword);

    Mono<Boolean> matches(String rawPassword, String encodedPassword);
}