package com.reactivo.persona.infrastructure.adapters.client;

import com.reactivo.persona.domain.model.Bootcamp;
import com.reactivo.persona.domain.spi.BootcampClientPort;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampClientAdapter implements BootcampClientPort {

    private final WebClient bootcampWebClient;

    @Override
    @CircuitBreaker(name = "bootcamp", fallbackMethod = "fallback")
    @Retry(name = "bootcampRetry")
    @Bulkhead(name = "bootcampBulkhead")
    public Flux<Bootcamp> findBootcampsByIds(List<Long> ids) {
        return bootcampWebClient.post()
                .uri("/bootcamp/ids")
                .bodyValue(Map.of("ids", ids))
                .retrieve()
                .bodyToFlux(Bootcamp.class);
    }

    @SuppressWarnings("unused")
    private Flux<Bootcamp> fallback(List<Long> ids, Throwable ex) {
        log.warn("Fallback executed for BootcampClient due to: {}", ex.toString());
        return Flux.empty();
    }

}
