package com.reactivo.persona.infrastructure.entrypoints.dto.request;

import java.util.List;

public record RegisterBootcampPersonDTO(Long idPerson, List<Long> idBootcamps) {
}