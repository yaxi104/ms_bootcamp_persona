package com.reactivo.persona.domain.usecase;

import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.LoginRequest;
import com.reactivo.persona.domain.model.UserAccount;
import com.reactivo.persona.domain.spi.JwtServicePort;
import com.reactivo.persona.domain.spi.PasswordEncoderPort;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseTest {

    @Mock
    private UserAccountPersistencePort userAccountPersistencePort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private JwtServicePort jwtServicePort;

    private AuthenticationUseCase authenticationUseCase;

    @BeforeEach
    void setUp() {
        authenticationUseCase = new AuthenticationUseCase(
                userAccountPersistencePort,
                passwordEncoderPort,
                jwtServicePort
        );
    }

    @Test
    void loginSuccessTest() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        UserAccount user = new UserAccount(1L, "user@example.com", "encodedPassword", "USER");

        when(userAccountPersistencePort.findByEmail(request.email())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(request.password(), user.password())).thenReturn(Mono.just(true));
        when(jwtServicePort.generateToken(user.email(), user.role())).thenReturn("jwt-token");

        StepVerifier.create(authenticationUseCase.login(request))
                .assertNext(response -> {
                    assertEquals("jwt-token", response.token());
                    assertEquals("USER", response.role());
                })
                .verifyComplete();

        verify(userAccountPersistencePort, times(1)).findByEmail(request.email());
        verify(passwordEncoderPort, times(1)).matches(request.password(), user.password());
        verify(jwtServicePort, times(1)).generateToken(user.email(), user.role());
    }

    @Test
    void loginEmailNotFoundTest() {
        LoginRequest request = new LoginRequest("notfound@example.com", "password123");

        when(userAccountPersistencePort.findByEmail(request.email())).thenReturn(Mono.empty());

        StepVerifier.create(authenticationUseCase.login(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.INVALID_CREDENTIALS
                )
                .verify();

        verify(userAccountPersistencePort, times(1)).findByEmail(request.email());
        verify(passwordEncoderPort, never()).matches(any(), any());
        verify(jwtServicePort, never()).generateToken(any(), any());
    }

    @Test
    void loginPasswordIsIncorrectTest() {
        LoginRequest request = new LoginRequest("user@example.com", "wrongpassword");
        UserAccount user = new UserAccount(1L, "user@example.com", "encodedPassword", "USER");

        when(userAccountPersistencePort.findByEmail(request.email())).thenReturn(Mono.just(user));
        when(passwordEncoderPort.matches(request.password(), user.password())).thenReturn(Mono.just(false));

        StepVerifier.create(authenticationUseCase.login(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException &&
                                ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.INVALID_CREDENTIALS
                )
                .verify();

        verify(userAccountPersistencePort, times(1)).findByEmail(request.email());
        verify(passwordEncoderPort, times(1)).matches(request.password(), user.password());
        verify(jwtServicePort, never()).generateToken(any(), any());
    }
}