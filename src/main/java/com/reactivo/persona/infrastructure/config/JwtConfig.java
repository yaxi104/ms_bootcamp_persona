package com.reactivo.persona.infrastructure.config;

import com.reactivo.persona.domain.spi.JwtServicePort;
import com.reactivo.persona.infrastructure.adapters.security.JwtServiceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtServicePort jwtServicePort(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") Long expiration) {
        return new JwtServiceAdapter(secret, expiration);
    }
}