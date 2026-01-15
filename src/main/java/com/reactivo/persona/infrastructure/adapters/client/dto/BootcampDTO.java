package com.reactivo.persona.infrastructure.adapters.client.dto;

import java.time.LocalDateTime;

public record BootcampDTO(Long id, String name, String description, LocalDateTime releaseDate, Integer duration) {
}