package com.reactivo.persona.domain.spi;

import com.reactivo.persona.domain.model.Bootcamp;
import reactor.core.publisher.Flux;

import java.util.List;

public interface BootcampClientPort {

    Flux<Bootcamp> findBootcampsByIds(List<Long> ids);
}
