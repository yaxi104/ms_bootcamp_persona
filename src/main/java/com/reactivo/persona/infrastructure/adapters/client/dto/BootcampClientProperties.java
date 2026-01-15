package com.reactivo.persona.infrastructure.adapters.client.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "clients.bootcamp")
public class BootcampClientProperties {

    private String baseUrl;
    private long timeoutMs;
}
