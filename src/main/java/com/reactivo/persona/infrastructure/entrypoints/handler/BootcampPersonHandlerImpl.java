package com.reactivo.persona.infrastructure.entrypoints.handler;

import com.reactivo.persona.domain.api.BootcampPersonServicePort;
import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.BootcampPerson;
import com.reactivo.persona.infrastructure.entrypoints.dto.BootcampPersonResponseDTO;
import com.reactivo.persona.infrastructure.entrypoints.dto.request.RegisterBootcampPersonDTO;
import com.reactivo.persona.infrastructure.entrypoints.mapper.BootcampPersonRequestMapper;
import com.reactivo.persona.infrastructure.entrypoints.util.ErrorDTO;
import com.reactivo.persona.infrastructure.entrypoints.util.HandlerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.reactivo.persona.infrastructure.entrypoints.util.Constants.AUTH_TOKEN;
import static com.reactivo.persona.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampPersonHandlerImpl {

    private final BootcampPersonServicePort bootcampPersonServicePort;
    private final BootcampPersonRequestMapper mapper;
    private final HandlerUtils handlerUtils;

    public Mono<ServerResponse> saveAllBootcampPerson(ServerRequest request) {
        String messageId = handlerUtils.getMessageId(request);
        String token = request.headers().firstHeader("Authorization");

        if (messageId == null) {
            return handlerUtils.buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    null,
                    TechnicalMessage.INVALID_PARAMETERS,
                    List.of(ErrorDTO.builder()
                            .code(TechnicalMessage.INVALID_PARAMETERS.getCode())
                            .message("Header X-Message-Id es requerido")
                            .build())
            );
        }

        Mono<com.reactivo.persona.domain.model.RegisterBootcampPerson> registerMono = request
                .bodyToMono(RegisterBootcampPersonDTO.class)
                .map(mapper::toDomain)
                .filter(Objects::nonNull);

        Flux<BootcampPerson> savedFlux =
                bootcampPersonServicePort.saveAllBootcampPerson(registerMono);

        return savedFlux
                .map(bp -> new BootcampPersonResponseDTO(bp.idPerson(), bp.idBootcamp()))
                .collectList()
                .flatMap(list -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list))
                .contextWrite(ctx -> {
                    if (messageId != null) ctx = ctx.put(X_MESSAGE_ID, messageId);
                    if (token != null) ctx = ctx.put(AUTH_TOKEN, token);
                    return ctx;
                })
                .onErrorResume(BusinessException.class, ex -> {
                    log.warn("BusinessException: {}", ex.getTechnicalMessage().getMessage());
                    return handlerUtils.buildErrorResponse(
                            HttpStatus.BAD_REQUEST,
                            messageId,
                            ex.getTechnicalMessage(),
                            List.of(ErrorDTO.builder()
                                    .code(ex.getTechnicalMessage().getCode())
                                    .message(ex.getTechnicalMessage().getMessage())
                                    .build())
                    );
                })
                .onErrorResume(ex -> {
                    log.error("Error inesperado en BootcampPersonHandler", ex);
                    return handlerUtils.buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build())
                    );
                });
    }
}
