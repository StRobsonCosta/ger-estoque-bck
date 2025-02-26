package com.kavex.xtoke.controle_estoque.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class SecurityTestConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return Mockito.mock(JwtDecoder.class);
    }
}
