package com.reactivo.persona.application.config;

import com.reactivo.persona.domain.api.AuthenticationServicePort;
import com.reactivo.persona.domain.api.BootcampPersonServicePort;
import com.reactivo.persona.domain.api.PersonServicePort;
import com.reactivo.persona.domain.spi.BootcampClientPort;
import com.reactivo.persona.domain.spi.BootcampPersonPersistencePort;
import com.reactivo.persona.domain.spi.JwtServicePort;
import com.reactivo.persona.domain.spi.PasswordEncoderPort;
import com.reactivo.persona.domain.spi.PersonPersistencePort;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import com.reactivo.persona.domain.usecase.AuthenticationUseCase;
import com.reactivo.persona.domain.usecase.BootcampPersonUseCase;
import com.reactivo.persona.domain.usecase.PersonUseCase;
import com.reactivo.persona.infrastructure.adapters.client.BootcampClientAdapter;
import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.BootcampPersonPersistenceAdapter;
import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.mapper.BootcampPersonEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.repository.BootcampPersonRepository;
import com.reactivo.persona.infrastructure.adapters.persistence.login.UserAccountPersistenceAdapter;
import com.reactivo.persona.infrastructure.adapters.persistence.login.mapper.UserAccountEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.login.repository.UserAccountRepository;
import com.reactivo.persona.infrastructure.adapters.persistence.person.PersonPersistenceAdapter;
import com.reactivo.persona.infrastructure.adapters.persistence.person.mapper.PersonEntityMapper;
import com.reactivo.persona.infrastructure.adapters.persistence.person.repository.PersonRepository;
import com.reactivo.persona.infrastructure.adapters.security.BCryptPasswordEncoderAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

    private final PersonRepository userRepository;
    private final PersonEntityMapper userEntityMapper;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountEntityMapper userAccountEntityMapper;
    private final BootcampPersonRepository bootcampPersonRepository;
    private final BootcampPersonEntityMapper bootcampPersonEntityMapper;
    private final TransactionalOperator transactionalOperator;
    private final WebClient bootcampWebClient;

    @Bean
    public PasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
    public BootcampPersonPersistencePort bootcampPersonPersistencePort() {
        return new BootcampPersonPersistenceAdapter(
                bootcampPersonRepository, bootcampPersonEntityMapper, transactionalOperator);
    }

    @Bean
    public BootcampClientPort bootcampClientPort() {
        return new BootcampClientAdapter(bootcampWebClient);
    }

    @Bean
    public PersonServicePort personServicePort(
            PasswordEncoderPort passwordEncoderPort,
            PersonPersistencePort personPersistencePort,
            UserAccountPersistencePort userAccountPersistencePort) {
        return new PersonUseCase(passwordEncoderPort, personPersistencePort, userAccountPersistencePort);
    }

    @Bean
    public AuthenticationServicePort authenticationServicePort(
            UserAccountPersistencePort userAccountPersistencePort,
            PasswordEncoderPort passwordEncoderPort,
            JwtServicePort jwtServicePort) {
        return new AuthenticationUseCase(userAccountPersistencePort, passwordEncoderPort, jwtServicePort);
    }

    @Bean
    public BootcampPersonServicePort bootcampPersonServicePort(
            BootcampPersonPersistencePort bootcampPersonPersistencePort,
            BootcampClientPort bootcampClientPort) {
        return new BootcampPersonUseCase(bootcampPersonPersistencePort, bootcampClientPort);
    }
}
