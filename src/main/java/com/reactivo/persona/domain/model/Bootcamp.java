package com.reactivo.persona.domain.model;

import java.time.LocalDateTime;

public record Bootcamp(Long id, String name, String description, LocalDateTime releaseDate, Integer duration) {
}