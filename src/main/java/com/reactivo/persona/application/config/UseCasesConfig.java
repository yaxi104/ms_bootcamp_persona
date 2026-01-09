package com.reactivo.persona.application.config;

import com.reactivo.persona.domain.api.AuthenticationServicePort;
import com.reactivo.persona.domain.api.PersonServicePort;
import com.reactivo.persona.domain.spi.JwtServicePort;
import com.reactivo.persona.domain.spi.PasswordEncoderPort;
import com.reactivo.persona.domain.spi.PersonPersistencePort;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import com.reactivo.persona.domain.usecase.AuthenticationUseCase;
import com.reactivo.persona.domain.usecase.PersonUseCase;
import com.reactivo.persona.infrastructure.adapters.persistence.login.UserAccountPersistenceAdapter;
import com.reactivo.persona.infrastructure.adapters.persistence.login.mapper.UserAccountEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.login.repository.UserAccountRepository;
import com.reactivo.persona.infrastructure.adapters.persistence.person.PersonPersistenceAdapter;
import com.reactivo.persona.infrastructure.adapters.persistence.person.mapper.PersonEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.person.repository.PersonRepository;
import com.reactivo.persona.infrastructure.adapters.security.BCryptPasswordEncoderAdapter;
import com.reactivo.persona.infrastructure.adapters.security.JwtServiceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
    private final PersonRepository userRepository;
    private final PersonEntityMapper userEntityMapper;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountEntityMapper userAccountEntityMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public PersonPersistencePort personPersistencePort() {
        return new PersonPersistenceAdapter(userRepository, userEntityMapper);
    }

    @Bean
    public UserAccountPersistencePort userAccountPersistencePort() {
        return new UserAccountPersistenceAdapter(userAccountRepository, userAccountEntityMapper);
    }

    @Bean
    public JwtServicePort jwtServicePort(@Value("${jwt.secret}") String secret,
                                         @Value("${jwt.expiration}") long expirationMs) {
        return new JwtServiceAdapter(secret, expirationMs);
    }

    @Bean
    public PersonServicePort usersServicePort(PasswordEncoderPort passwordEncoderPort, PersonPersistencePort usersPersistencePort, UserAccountPersistencePort userAccountPersistencePort) {
        return new PersonUseCase(passwordEncoderPort, usersPersistencePort, userAccountPersistencePort);
    }

    @Bean
    public AuthenticationServicePort authenticationServicePort(UserAccountPersistencePort userAccountPersistencePort, PasswordEncoderPort passwordEncoderPort, JwtServicePort jwtServicePort) {
        return new AuthenticationUseCase(userAccountPersistencePort, passwordEncoderPort, jwtServicePort);
    }
}
