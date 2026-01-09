package com.reactivo.persona.domain.usecase;

import com.reactivo.persona.domain.api.PersonServicePort;
import com.reactivo.persona.domain.enums.TechnicalMessage;
import com.reactivo.persona.domain.exceptions.BusinessException;
import com.reactivo.persona.domain.model.Person;
import com.reactivo.persona.domain.model.RegisterPerson;
import com.reactivo.persona.domain.model.UserAccount;
import com.reactivo.persona.domain.spi.PasswordEncoderPort;
import com.reactivo.persona.domain.spi.PersonPersistencePort;
import com.reactivo.persona.domain.spi.UserAccountPersistencePort;
import com.reactivo.persona.domain.utils.ValidationHelper;
import reactor.core.publisher.Mono;

import static com.reactivo.persona.domain.constants.Constants.PATTERN_EMAIL;

public class PersonUseCase implements PersonServicePort {

    private final PasswordEncoderPort passwordEncoderPort;
    private final PersonPersistencePort personPersistencePort;
    private final UserAccountPersistencePort userAccountPersistencePort;

    public PersonUseCase(PasswordEncoderPort passwordEncoderPort, PersonPersistencePort personPersistencePort, UserAccountPersistencePort userAccountPersistencePort) {
        this.passwordEncoderPort = passwordEncoderPort;
        this.personPersistencePort = personPersistencePort;
        this.userAccountPersistencePort = userAccountPersistencePort;
    }

    @Override
    public Mono<Person> registerPerson(RegisterPerson registerPerson) {
        return Mono.when(
                        ValidationHelper.validateRequest(registerPerson, p -> p.name() != null && !p.name().isBlank()),
                        ValidationHelper.validateRequest(registerPerson, p -> p.age() != null),
                        ValidationHelper.validateRequest(registerPerson, p -> p.email() != null && p.email().matches(PATTERN_EMAIL)),
                        ValidationHelper.validateRequest(registerPerson, p -> p.password() != null && p.password().length() >= 8),
                        ValidationHelper.validateRequest(registerPerson, p -> p.role() != null)
                )
                .then(personPersistencePort.existByEmail(registerPerson.email())
                        .flatMap(exists -> exists
                                ? Mono.error(new BusinessException(
                                TechnicalMessage.USER_ALREADY_EXISTS))
                                : Mono.empty()
                        )
                )
                .then(passwordEncoderPort.encode(registerPerson.password()))
                .flatMap(encodedPassword -> {
                    UserAccount userAccount = UserAccount.from(registerPerson, encodedPassword);
                    return userAccountPersistencePort.save(userAccount);
                })
                .flatMap(userAccount ->
                        personPersistencePort.save(
                                new Person(null,
                                        userAccount.id(),
                                        registerPerson.name(),
                                        registerPerson.email(),
                                        registerPerson.age()
                                )
                        )
                );
    }

}
