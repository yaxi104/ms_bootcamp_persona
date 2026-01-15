package com.reactivo.persona.domain.usecase;

import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.Bootcamp;
import com.reactivo.persona.domain.model.RegisterBootcampPerson;
import com.reactivo.persona.domain.spi.BootcampClientPort;
import com.reactivo.persona.domain.spi.BootcampPersonPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootcampPersonUseCaseTest {

    @Mock
    private BootcampPersonPersistencePort bootcampPersonPersistencePort;

    @Mock
    private BootcampClientPort bootcampClientPort;

    private BootcampPersonUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BootcampPersonUseCase(
                bootcampPersonPersistencePort,
                bootcampClientPort
        );
    }

    @Test
    void shouldThrowExceptionWhenRequestIsInvalid() {
        RegisterBootcampPerson request =
                new RegisterBootcampPerson(null, List.of());

        StepVerifier.create(useCase.saveAllBootcampPerson(Mono.just(request)))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                                ((BusinessException) ex).getTechnicalMessage()
                                        == TechnicalMessage.INVALID_PARAMETERS)
                .verify();
    }

    @Test
    void shouldReturnEmptyWhenNoNewBootcamps() {
        Long personId = 1L;
        RegisterBootcampPerson request =
                new RegisterBootcampPerson(personId, List.of(1L, 2L));

        when(bootcampPersonPersistencePort.findAllIdBootcampByIdPerson(personId))
                .thenReturn(Flux.just(1L, 2L));

        StepVerifier.create(useCase.saveAllBootcampPerson(Mono.just(request)))
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFailWhenBootcampLimitExceeded() {
        Long personId = 1L;
        RegisterBootcampPerson request =
                new RegisterBootcampPerson(personId, List.of(4L, 5L, 6L));

        when(bootcampPersonPersistencePort.findAllIdBootcampByIdPerson(personId))
                .thenReturn(Flux.just(1L, 2L, 3L));

        StepVerifier.create(useCase.saveAllBootcampPerson(Mono.just(request)))
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException &&
                                ((BusinessException) ex).getTechnicalMessage()
                                        == TechnicalMessage.BOOTCAMP_LIMIT_EXCEEDED)
                .verify();
    }

    @Test
    void shouldSaveOnlyValidBootcampsWithoutConflict() {
        Long personId = 1L;
        RegisterBootcampPerson request =
                new RegisterBootcampPerson(personId, List.of(2L, 3L));

        when(bootcampPersonPersistencePort.findAllIdBootcampByIdPerson(personId))
                .thenReturn(Flux.just(1L));

        LocalDateTime baseDate = LocalDateTime.of(2024, 1, 1, 0, 0);

        Bootcamp existing = new Bootcamp(
                1L,
                "Java",
                "Java básico",
                baseDate,
                40
        );

        Bootcamp valid = new Bootcamp(
                2L,
                "Spring",
                "Spring WebFlux",
                baseDate.plusMonths(1),
                30
        );

        Bootcamp conflict = new Bootcamp(
                3L,
                "Quarkus",
                "Quarkus reactivo",
                baseDate,
                40
        );

        when(bootcampClientPort.findBootcampsByIds(anyList()))
                .thenReturn(Flux.just(existing, valid, conflict));

        when(bootcampPersonPersistencePort.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(useCase.saveAllBootcampPerson(Mono.just(request)))
                .expectNextMatches(bp ->
                        bp.idPerson().equals(personId)
                                && bp.idBootcamp().equals(2L))
                .expectComplete()
                .verify();

        verify(bootcampPersonPersistencePort).saveAll(any());
    }

    @Test
    void shouldReturnEmptyWhenAllNewBootcampsHaveConflict() {
        Long personId = 1L;
        RegisterBootcampPerson request =
                new RegisterBootcampPerson(personId, List.of(2L));

        when(bootcampPersonPersistencePort.findAllIdBootcampByIdPerson(personId))
                .thenReturn(Flux.just(1L));

        LocalDateTime date = LocalDateTime.of(2024, 1, 1, 0, 0);

        Bootcamp existing = new Bootcamp(
                1L,
                "Java",
                "Java básico",
                date,
                40
        );

        Bootcamp conflict = new Bootcamp(
                2L,
                "Spring",
                "Spring avanzado",
                date,
                40
        );

        when(bootcampClientPort.findBootcampsByIds(anyList()))
                .thenReturn(Flux.just(existing, conflict));

        StepVerifier.create(useCase.saveAllBootcampPerson(Mono.just(request)))
                .expectComplete()
                .verify();

        verify(bootcampPersonPersistencePort, never()).saveAll(any());
    }
}
