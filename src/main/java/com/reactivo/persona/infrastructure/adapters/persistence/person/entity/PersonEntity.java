package com.reactivo.persona.infrastructure.adapters.persistence.person.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "PERSON")
public class PersonEntity {
    @Id
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private Integer age;

    public PersonEntity() {
    }

    public PersonEntity(Long id, Long userId, String name, String email, Integer age) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
