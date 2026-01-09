package com.reactivo.persona.domain.spi;

public interface JwtServicePort {
    String generateToken(String email, String role);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    String getRoleFromToken(String token);
}