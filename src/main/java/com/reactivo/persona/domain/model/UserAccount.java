package com.reactivo.persona.domain.model;

public record UserAccount(Long id,
                          String email,
                          String password,
                          String role
) {
    public static UserAccount from(RegisterPerson registerPerson, String encodedPassword) {
        return new UserAccount(
                null,
                registerPerson.email(),
                encodedPassword,
                registerPerson.role()
        );
    }
}