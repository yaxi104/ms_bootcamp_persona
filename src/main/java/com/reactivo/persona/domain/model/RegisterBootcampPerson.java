package com.reactivo.persona.domain.model;

import java.util.List;

public record RegisterBootcampPerson(Long idPerson, List<Long> idBootcamps) {
}