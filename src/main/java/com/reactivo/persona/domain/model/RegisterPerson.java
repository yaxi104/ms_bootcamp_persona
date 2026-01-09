package com.reactivo.persona.domain.model;

public record RegisterPerson(String name, String email, Integer age, String password, String role) {
}