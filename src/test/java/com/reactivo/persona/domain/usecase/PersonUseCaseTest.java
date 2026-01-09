package com.reactivo.persona.domain.usecase;

import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.Person;
import com.reactivo.persona.domain.model.RegisterPerson;
import com.reactivo.persona.domain.model.UserAccount;
import com.reactivo.persona.domain.spi.PasswordEncoderPort;
import com.reactivo.persona.domain.spi.PersonPersistencePort;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PersonUseCaseTest {
    private PasswordEncoderPort passwordEncoderPort;
    private PersonPersistencePort personPersistencePort;
    private UserAccountPersistencePort userAccountPersistencePort;
    private PersonUseCase personUseCase;

    @BeforeEach
    void setUp() {
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        personPersistencePort = mock(PersonPersistencePort.class);
        userAccountPersistencePort = mock(UserAccountPersistencePort.class);
        personUseCase = new PersonUseCase(passwordEncoderPort, personPersistencePort, userAccountPersistencePort);
    }

    @Test
    void registerPersonSuccessTest() {
        RegisterPerson registerPerson = new RegisterPerson("John Doe", "john@example.com", 25, "password123", "USER");
        when(personPersistencePort.existByEmail(registerPerson.email())).thenReturn(Mono.just(false));
        when(passwordEncoderPort.encode(registerPerson.password())).thenReturn(Mono.just("encodedPass"));
        when(userAccountPersistencePort.save(any(UserAccount.class))).thenReturn(Mono.just(new UserAccount(1L, registerPerson.email(), "encodedPass", registerPerson.role())));
        when(personPersistencePort.save(any(Person.class))).thenReturn(Mono.just(new Person(1L, 1L, registerPerson.name(), registerPerson.email(), registerPerson.age())));
        StepVerifier.create(personUseCase.registerPerson(registerPerson)).expectNextMatches(person -> person.name().equals("John Doe") && person.email().equals("john@example.com") && person.age() == 25).verifyComplete();
    }

    @Test
    void registerPersonAlreadyExistsTest() {
        RegisterPerson registerPerson = new RegisterPerson("Jane Doe", "jane@example.com", 30, "password123", "ADMIN");
        when(personPersistencePort.existByEmail(registerPerson.email())).thenReturn(Mono.just(true));
        StepVerifier.create(personUseCase.registerPerson(registerPerson)).expectErrorMatches(throwable -> throwable instanceof BusinessException && ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.USER_ALREADY_EXISTS).verify();
    }

    @Test
    void registerPersonInvalidPasswordTest() {
        RegisterPerson registerPerson = new RegisterPerson("Mark", "mark@example.com", 22, "short", "USER");
        when(personPersistencePort.existByEmail(registerPerson.email())).thenReturn(Mono.just(false));
        StepVerifier.create(personUseCase.registerPerson(registerPerson)).expectError(BusinessException.class).verify();
    }

    @Test
    void registerPersonInvalidEmailTest() {
        RegisterPerson registerPerson = new RegisterPerson("Ana", "invalid-email", 28, "password123", "USER");
        when(personPersistencePort.existByEmail(registerPerson.email())).thenReturn(Mono.just(false));
        StepVerifier.create(personUseCase.registerPerson(registerPerson)).expectError(BusinessException.class).verify();
    }

    @Test
    void registerPersonMissingRoleTest() {
        RegisterPerson registerPerson = new RegisterPerson("Carlos", "carlos@example.com", 40, "password123", null);
        when(personPersistencePort.existByEmail(registerPerson.email())).thenReturn(Mono.just(false));
        StepVerifier.create(personUseCase.registerPerson(registerPerson)).expectError(BusinessException.class).verify();
    }
}