package com.reactivo.persona.infrastructure.entrypoints.handler;

import com.reactivo.persona.domain.api.AuthenticationServicePort;
import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.infrastructure.entrypoints.dto.request.LoginRequestDTO;
import com.reactivo.persona.infrastructure.entrypoints.mapper.LoginRequestMapper;
import com.reactivo.persona.infrastructure.entrypoints.mapper.LoginResponsetMapper;
import com.reactivo.persona.infrastructure.entrypoints.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.Collections;

import static com.reactivo.persona.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationHandlerImpl {

    private final AuthenticationServicePort authenticationServicePort;
    private final LoginRequestMapper loginRequestMapper;
    private final LoginResponsetMapper loginResponsetMapper;

    public Mono<ServerResponse> login(ServerRequest request) {
        String messageId = getMessageId(request);

        return request.bodyToMono(LoginRequestDTO.class)
                .flatMap(loginRequestDto -> authenticationServicePort.login(loginRequestMapper.loginRequestDTOToLoginRequest(loginRequestDto))
                        .doOnSuccess(resp -> log.info("User logged in successfully with messageId: {}", messageId))
                )
                .flatMap(resp -> ServerResponse.ok().bodyValue(loginResponsetMapper.loginResponseToLoginResponseDTO(resp)))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error("Error handling login request", ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(HttpStatus.UNAUTHORIZED, messageId, TechnicalMessage.INVALID_CREDENTIALS))
                .onErrorResume(ex -> buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, messageId, TechnicalMessage.INTERNAL_ERROR));
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String identifier, TechnicalMessage error) {
        APIResponse apiErrorResponse = APIResponse.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .identifier(identifier)
                .date(Instant.now().toString())
                .errors(Collections.emptyList())
                .build();

        return ServerResponse.status(status).bodyValue(apiErrorResponse);
    }

    private String getMessageId(ServerRequest request) {
        return request.headers().firstHeader(X_MESSAGE_ID);
    }
}