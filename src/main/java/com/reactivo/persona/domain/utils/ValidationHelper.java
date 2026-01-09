package com.reactivo.persona.domain.utils;

import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

public final class ValidationHelper {

    private ValidationHelper() {}

    public static <T> Mono<T> validateRequest(T obj, Predicate<T> condition) {
        return Mono.just(obj)
                   .filter(condition)
                   .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)));
    }
}