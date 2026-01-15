package com.reactivo.persona.infrastructure.adapters.persistence.bootcampperson.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "BOOTCAMP_PERSON")
public class BootcampPersonEntity {
    @Id
    private Long id;
    @JsonProperty("id_person")
    private Long idPerson;
    @JsonProperty("id_bootcamp")
    private Long idBootcamp;

    public BootcampPersonEntity() {
    }

    public BootcampPersonEntity(Long id, Long idPerson, Long idBootcamp) {
        this.id = id;
        this.idPerson = idPerson;
        this.idBootcamp = idBootcamp;
    }

    public Long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(Long idPerson) {
        this.idPerson = idPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdBootcamp() {
        return idBootcamp;
    }

    public void setIdBootcamp(Long idBootcamp) {
        this.idBootcamp = idBootcamp;
    }
}
